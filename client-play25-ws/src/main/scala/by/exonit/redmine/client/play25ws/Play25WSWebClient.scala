/*
 * Copyright 2017 Exon IT
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

package by.exonit.redmine.client.play25ws

import java.net.{URL, URLEncoder}

import akka.stream.{IOResult, Materializer}
import akka.stream.scaladsl.StreamConverters
import by.exonit.redmine.client.managers.WebClient
import by.exonit.redmine.client.managers.WebClient._
import cats.{~>, Id}
import monix.eval.Task
import play.api.libs.ws.{StreamedResponse, WSClient, WSRequest, WSResponse}

import scala.concurrent.{ExecutionContext, Future}
import scala.util.control.NonFatal

class Play25WSWebClient(val client: WSClient)
  (implicit mat: Materializer, ec: ExecutionContext = ExecutionContext.global)
  extends WebClient {

  import Implicits._

  def compileRequestCommand(requestCommand: RequestDSL.Request[Unit]): WSRequest = {
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
      .withHeaders(headers.toSeq: _*)
      .withQueryString(queryParams: _*)
    val requestWithAuth = auth match {
      case Some(x) => baseRequest.withDslAuth(x)
      case None => baseRequest
    }
    body match {
      case Some(b) => requestWithAuth.withDslBody(b)
      case None => requestWithAuth
    }
  }

  def compileResponseCommand[T](responseCommand: ResponseDSL.Response[T]): WSResponse => T = response => {
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
          response.allHeaders.mapValues(_.mkString(","))
      }
    })
  }

  def compileStreamingResponseCommand[T](
    responseCommand: StreamingResponseDSL.StreamingResponse[T]
  ): StreamedResponse => T = response => {
    responseCommand.foldMap(new (StreamingResponseDSL.StreamingResponseOp ~> Id) {
      override def apply[A](fa: StreamingResponseDSL.StreamingResponseOp[A]) = fa match {
        case StreamingResponseDSL.GetBodyStream(osp) =>
          Task.deferFuture {
            response.body.runWith(StreamConverters.fromOutputStream(osp))
          } flatMap {
            case r: IOResult if r.wasSuccessful => Task.unit
            case r: IOResult => Task.raiseError(r.getError)
          }
        case StreamingResponseDSL.GetStatusCode() =>
          response.headers.status
        case StreamingResponseDSL.GetHeaders() =>
          response.headers.headers.mapValues(_.mkString(","))
      }
    })
  }

  override def execute[T](requestCommand: RequestDSL.Request[Unit], responseCommand: ResponseDSL.Response[T]):
  Task[T] =
    Task.evalOnce {
      compileRequestCommand(requestCommand)
    } flatMap {req => Task.fromFuture(req.execute())} map {
      compileResponseCommand(responseCommand)
    }

  override def executeStreaming[T](
    requestCommand: RequestDSL.Request[Unit],
    responseCommand: StreamingResponseDSL.StreamingResponse[T]
  ): Task[T] =
    Task.evalOnce {
      compileRequestCommand(requestCommand)
    } flatMap {req => Task.fromFuture(req.stream())} map {
      compileStreamingResponseCommand(responseCommand)
    }

  def close(): Unit = {
    try {
      client.close()
    } catch {
      case NonFatal(_) =>
    }
  }
}
