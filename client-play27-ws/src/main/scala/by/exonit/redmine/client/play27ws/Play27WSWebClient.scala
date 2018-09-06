/*
 * Copyright 2018 Exon IT
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package by.exonit.redmine.client.play27ws

import java.net.{URL, URLEncoder}

import akka.stream.{IOResult, Materializer}
import akka.stream.scaladsl.StreamConverters
import by.exonit.redmine.client.managers.WebClient
import by.exonit.redmine.client.managers.WebClient._
import cats.data.State
import cats.{Id, ~>}
import cats.effect.IO
import play.api.libs.ws.{WSClient, WSRequest, WSResponse}

import scala.util.control.NonFatal
import Implicits._
import by.exonit.redmine.client.play27ws.Play27WSWebClient.{RequestInterpreter, RequestState, ResponseInterpreter, StreamingResponseInterpreter}

class Play27WSWebClient(val client: WSClient)
  (implicit mat: Materializer)
  extends WebClient {

  def compileRequestCommand(requestCommand: RequestDSL.Request[Unit]): IO[WSRequest] = IO {
    val interpreter = new RequestInterpreter
    val requestState = requestCommand.foldMap(interpreter).runS(RequestState()).value

    val finalUrl = requestState.url.getOrElse(
      throw new UnsupportedOperationException("Unable to compile request from provided AST: no base URL specified"))
    val baseRequest = client.url(finalUrl)
      .withMethod(requestState.method)
      .withHttpHeaders(requestState.headers.toSeq: _*)
      .withQueryStringParameters(requestState.queryParams: _*)
    val requestWithAuth = requestState.auth match {
      case Some(x) => baseRequest.withDslAuth(x)
      case None => baseRequest
    }
    requestWithAuth.withDslBody(requestState.body)
  }

  def compileResponseCommand[T](responseCommand: ResponseDSL.Response[T]): WSResponse => IO[T] = response => IO {
    responseCommand.foldMap(new ResponseInterpreter(response))
  }

  def compileStreamingResponseCommand[T](
    responseCommand: StreamingResponseDSL.StreamingResponse[T]
  ): WSResponse => IO[T] = response => IO {
    responseCommand.foldMap(new StreamingResponseInterpreter(response))
  }

  override def execute[T](
    requestCommand: RequestDSL.Request[Unit],
    responseCommand: ResponseDSL.Response[T]
  ): IO[T] =
    compileRequestCommand(requestCommand) flatMap { req =>
      IO.fromFuture(IO { req.execute() })
    } flatMap compileResponseCommand(responseCommand)

  override def executeStreaming[T](
    requestCommand: RequestDSL.Request[Unit],
    responseCommand: StreamingResponseDSL.StreamingResponse[T]
  ): IO[T] =
    compileRequestCommand(requestCommand) flatMap { req =>
      IO.fromFuture(IO { req.stream() })
    } flatMap compileStreamingResponseCommand(responseCommand)

  def close(): Unit = {
    try {
      client.close()
    } catch {
      case NonFatal(_) =>
      case ex: Throwable => throw ex
    }
  }
}
object Play27WSWebClient {

  type RequestStateT[A] = State[RequestState, A]

  case class RequestState(
    url: Option[String] = None,
    queryParams: Seq[(String, String)] = Seq.empty,
    headers: Map[String, String] = Map.empty,
    method: String = "GET",
    auth: Option[RequestDSL.AuthenticationMethod] = None,
    body: Option[RequestDSL.Body] = None
  )

  class RequestInterpreter extends (RequestDSL.RequestOp ~> State[RequestState, ?]) {

    override def apply[A](fa: RequestDSL.RequestOp[A]) = fa match {
      case RequestDSL.SetUrl(u) =>
        State.modify[RequestState](_.copy(url = Some(u)))
      case RequestDSL.AddSegments(segments@_*) =>
        State.modify[RequestState]{ s =>
          val encodedSegments = segments.map(URLEncoder.encode(_, "UTF-8"))
          val newUrl = s.url.map { u =>
            val baseUrl = new URL(u)
            val newUrl = new URL(baseUrl, s"${baseUrl.getPath.trimRight('/')}/${encodedSegments.mkString("/")}")
            newUrl.toExternalForm
          }
          s.copy(url = newUrl)
        }
      case RequestDSL.AddQueries(queries@_*) =>
        State.modify[RequestState]{s =>
          s.copy(queryParams = s.queryParams ++ queries)
        }
      case RequestDSL.SetHeaders(h@_*) =>
        State.modify[RequestState]{s =>
          s.copy(headers = s.headers ++ h)
        }
      case RequestDSL.SetMethod(m) =>
        State.modify[RequestState]{s =>
          s.copy(method = m)
        }
      case RequestDSL.SetAuth(a) =>
        State.modify[RequestState]{s =>
          s.copy(auth = a)
        }
      case RequestDSL.SetBody(b) =>
        State.modify[RequestState]{s =>
          s.copy(body = b)
        }
      case RequestDSL.NoOp() =>
        State.modify[RequestState](identity)
    }
  }

  class ResponseInterpreter(response: WSResponse) extends (ResponseDSL.ResponseOp ~> Id) {
    override def apply[A](fa: ResponseDSL.ResponseOp[A]) = fa match {
      case ResponseDSL.GetBodyAsBytes() =>
        response.bodyAsBytes.toArray
      case ResponseDSL.GetBodyAsString() =>
        response.body
      case ResponseDSL.GetStatusCode() =>
        response.status
      case ResponseDSL.GetStatusText() =>
        response.statusText
      case ResponseDSL.GetHeaders() =>
        response.headers.mapValues(_.mkString(","))
    }
  }

  class StreamingResponseInterpreter(response: WSResponse)(implicit mat: Materializer)
    extends (StreamingResponseDSL.StreamingResponseOp ~> Id) {
    override def apply[A](fa: StreamingResponseDSL.StreamingResponseOp[A]) = fa match {
      case StreamingResponseDSL.GetBodyStream(osp) =>
        IO.fromFuture {
          IO {
            response.bodyAsSource.runWith(StreamConverters.fromOutputStream(osp))
          }
        } flatMap {
          case r: IOResult if r.wasSuccessful => IO.unit
          case r: IOResult => IO.raiseError(r.getError)
        }
      case StreamingResponseDSL.GetStatusCode() =>
        response.status
      case StreamingResponseDSL.GetHeaders() =>
        response.headers.mapValues(_.mkString(","))
    }
  }
}
