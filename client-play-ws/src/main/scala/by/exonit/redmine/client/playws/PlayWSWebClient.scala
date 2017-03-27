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

package by.exonit.redmine.client.playws

import java.net.{URL, URLEncoder}

import akka.actor.ActorSystem
import akka.stream.{ActorMaterializer, IOResult}
import akka.stream.scaladsl.StreamConverters
import akka.util.ByteString
import by.exonit.redmine.client.managers.WebClient
import by.exonit.redmine.client.managers.WebClient._
import cats.{~>, Id}
import monix.eval.Task
import play.api.libs.ws.{StreamedResponse, WSAuthScheme, WSResponse, EmptyBody => WSEmptyBody, FileBody =>
WSFileBody, InMemoryBody => WSInMemoryBody, StreamedBody => WSStreamedBody}
import play.api.libs.ws.ahc.{AhcWSClient, AhcWSClientConfig, AhcWSRequest}

import scala.concurrent.{ExecutionContext, Future}
import scala.util.control.NonFatal

class PlayWSWebClient(
  actorSystemName: String = "default"
)
  (implicit clientConfig: AhcWSClientConfig = AhcWSClientConfig(), ec: ExecutionContext = ExecutionContext.global)
  extends WebClient {

  import Implicits._

  implicit val actorSystem  = ActorSystem(actorSystemName, defaultExecutionContext = Some(ec))
  implicit val materializer = ActorMaterializer()
  val client = AhcWSClient(clientConfig)

  def compileRequestCommand(requestCommand: RequestDSL.Request[Unit]): AhcWSRequest = {
    var requestOption: Option[AhcWSRequest] = None
    requestCommand.foldMap(new (RequestDSL.RequestOp ~> Id) {
      override def apply[A](fa: RequestDSL.RequestOp[A]): Id[A] = fa match {
        case RequestDSL.SetUrl(url) =>
          requestOption = Some(client.url(url).asInstanceOf[AhcWSRequest])
          ()
        case RequestDSL.AddSegments(segments @ _*) =>
          val encodedSegments = segments.map(URLEncoder.encode(_, "UTF-8"))
          requestOption = requestOption.map {r =>
            val baseUrl = new URL(r.url)
            val newUrl = new URL(baseUrl, s"${baseUrl.getPath.trimRight('/')}/${encodedSegments.mkString("/")}")
            r.copy(url = newUrl.toExternalForm)(materializer)
          }
          ()
        case RequestDSL.AddQueries(queries @ _*) =>
          requestOption = requestOption.map(r => r.withQueryString(queries: _*).asInstanceOf[AhcWSRequest])
          ()
        case RequestDSL.SetHeaders(headers @ _*) =>
          requestOption = requestOption.map(r => r.withHeaders(headers: _*).asInstanceOf[AhcWSRequest])
          ()
        case RequestDSL.SetMethod(method) =>
          requestOption = requestOption.map(r => r.withMethod(method).asInstanceOf[AhcWSRequest])
          ()
        case RequestDSL.SetAuth(auth) =>
          auth match {
            case RequestDSL.AuthenticationMethod.Basic(user, password) =>
              requestOption = requestOption.map {r =>
                r.withAuth(user, password.mkString, WSAuthScheme.BASIC).asInstanceOf[AhcWSRequest]
              }
            case RequestDSL.AuthenticationMethod.Digest(user, password) =>
              requestOption = requestOption.map {r =>
                r.withAuth(user, password.mkString, WSAuthScheme.DIGEST).asInstanceOf[AhcWSRequest]
              }
            case RequestDSL.AuthenticationMethod.Bearer(token) =>
              requestOption = requestOption.map {r =>
                r.withHeaders("Authentication" -> s"Bearer $token").asInstanceOf[AhcWSRequest]
              }
          }
          ()
        case RequestDSL.SetBody(body) =>
          requestOption = requestOption.map {r =>
            body match {
              case RequestDSL.Body.EmptyBody() =>
                r.withBody(WSEmptyBody).asInstanceOf[AhcWSRequest]
              case RequestDSL.Body.FileBody(file) =>
                r.withBody(WSFileBody(file)).asInstanceOf[AhcWSRequest]
              case RequestDSL.Body.InMemoryByteBody(bytes) =>
                r.withBody(WSInMemoryBody(ByteString(bytes))).asInstanceOf[AhcWSRequest]
              case RequestDSL.Body.StreamedBody(streamProvider) =>
                r.withBody(WSStreamedBody(StreamConverters.fromInputStream(streamProvider))).asInstanceOf[AhcWSRequest]
            }
          }
          ()
        case RequestDSL.NoOp() =>
          ()
      }
    })
    requestOption.getOrElse(throw new UnsupportedOperationException("Unable to compile request from provided AST"))
  }

  def compileResponseCommand[T](responseCommand: ResponseDSL.Response[T]): WSResponse => T = response => {
    responseCommand.foldMap(new (ResponseDSL.ResponseOp ~> Id) {
      override def apply[A](fa: ResponseDSL.ResponseOp[A]): Id[A] = fa match {
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
      override def apply[A](fa: StreamingResponseDSL.StreamingResponseOp[A]): Id[A] = fa match {
        case StreamingResponseDSL.GetBodyStream(osp) => Task.deferFuture {
          response.body.runWith(StreamConverters.fromOutputStream(osp)).flatMap {
            case r: IOResult if r.wasSuccessful => Future.successful(Unit)
            case r: IOResult => Future.failed(r.getError)
          }
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
    try {
      materializer.shutdown()
    } catch {
      case NonFatal(_) =>
    }
    try {
      actorSystem.terminate()
    } catch {
      case NonFatal(_) =>
    }
  }
}
