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

package by.exonit.redmine.client.play26ws

import java.net.{URL, URLEncoder}

import akka.stream.{IOResult, Materializer}
import akka.stream.scaladsl.StreamConverters
import by.exonit.redmine.client.managers.WebClient
import by.exonit.redmine.client.managers.WebClient._
import cats.{~>, Id}
import cats.effect.IO
import play.api.libs.ws.{WSClient, WSRequest, WSResponse}

import scala.util.control.NonFatal

class Play26WSWebClient(val client: WSClient)
  (implicit mat: Materializer)
  extends WebClient {

  import Implicits._

  def compileRequestCommand(requestCommand: RequestDSL.Request[Unit]): IO[WSRequest] = IO {
    var url: Option[String] = None
    var queryParams: Seq[(String, String)] = Seq.empty
    var headers: Map[String, String] = Map.empty
    var method: String = "GET"
    var auth: Option[RequestDSL.AuthenticationMethod] = None
    var body: Option[RequestDSL.Body] = None

    requestCommand.foldMap(new (RequestDSL.RequestOp ~> Id) {
      override def apply[A](fa: RequestDSL.RequestOp[A]) = fa match {
        case RequestDSL.SetUrl(u) =>
          url = Some(u)
          ()
        case RequestDSL.AddSegments(segments @ _*) =>
          val encodedSegments = segments.map(URLEncoder.encode(_, "UTF-8"))
          url = url.map {u =>
            val baseUrl = new URL(u)
            val newUrl = new URL(baseUrl, s"${baseUrl.getPath.trimRight('/')}/${encodedSegments.mkString("/")}")
            newUrl.toExternalForm
          }
          ()
        case RequestDSL.AddQueries(queries @ _*) =>
          queryParams ++= queries
          ()
        case RequestDSL.SetHeaders(h @ _*) =>
          headers ++= h
          ()
        case RequestDSL.SetMethod(m) =>
          method = m
          ()
        case RequestDSL.SetAuth(a) =>
          auth = Some(a)
          ()
        case RequestDSL.SetBody(b) =>
          b match {
            case RequestDSL.Body.EmptyBody() =>
              body = None
            case bb =>
              body = Some(bb)
          }
          ()
        case RequestDSL.NoOp() =>
          ()
      }
    })
    val finalUrl = url.getOrElse(throw new UnsupportedOperationException("Unable to compile request from provided AST: no base URL specified"))
    val baseRequest = client.url(finalUrl)
      .withMethod(method)
      .addHttpHeaders(headers.toSeq: _*)
      .addQueryStringParameters(queryParams: _*)
    val requestWithAuth = auth match {
      case Some(x) => baseRequest.withDslAuth(x)
      case None => baseRequest
    }
    body match {
      case Some(b) => requestWithAuth.withDslBody(b)
      case None => requestWithAuth
    }
  }

  def compileResponseCommand[T](responseCommand: ResponseDSL.Response[T]): WSResponse => IO[T] = response => IO {
    responseCommand.foldMap(new (ResponseDSL.ResponseOp ~> Id) {
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
    })
  }

  def compileStreamingResponseCommand[T](
    responseCommand: StreamingResponseDSL.StreamingResponse[T]
  ): WSResponse => IO[T] = response => IO {
    responseCommand.foldMap(new (StreamingResponseDSL.StreamingResponseOp ~> Id) {
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
    })
  }

  override def execute[T](
    requestCommand: RequestDSL.Request[Unit],
    responseCommand: ResponseDSL.Response[T]
  ): IO[T] =
    compileRequestCommand(requestCommand) flatMap {req =>
      IO.fromFuture(IO {req.execute()})
    } flatMap compileResponseCommand(responseCommand)

  override def executeStreaming[T](
    requestCommand: RequestDSL.Request[Unit],
    responseCommand: StreamingResponseDSL.StreamingResponse[T]
  ): IO[T] =
    compileRequestCommand(requestCommand) flatMap {req =>
      IO.fromFuture(IO {req.stream()})
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
