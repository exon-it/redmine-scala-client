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

package by.exonit.redmine.client.playws.standalone

import java.io.FileInputStream
import java.net.{URL, URLEncoder}

import akka.actor.ActorSystem
import akka.stream.{ActorMaterializer, IOResult}
import akka.stream.scaladsl.StreamConverters
import akka.util.ByteString
import by.exonit.redmine.client.managers.WebClient
import by.exonit.redmine.client.managers.WebClient._
import cats.{~>, Id}
import cats.data._
import monix.eval.Task
import play.api.libs.ws.{DefaultBodyWritables, StandaloneWSRequest, StandaloneWSResponse, WSAuthScheme, WSBody, InMemoryBody => WSInMemoryBody, SourceBody => WSSourceBody}
import play.api.libs.ws.ahc.{AhcWSClientConfig, StandaloneAhcWSClient}

import scala.collection.immutable
import scala.concurrent.{ExecutionContext, Future}
import scala.util.control.NonFatal

object PlayWSStandaloneWebClient {
  case class WSRequestData(
    url: Option[URL] = None,
    method: Option[String] = None,
    auth: Option[Either[String, (String, String, WSAuthScheme)]] = None,
    body: Option[WSBody] = None,
    queryParams: immutable.List[(String, String)] = immutable.List.empty,
    headers: immutable.Map[String, String] = immutable.Map.empty
  )
}

class PlayWSStandaloneWebClient(
  actorSystemName: String = "default"
)
  (implicit clientConfig: AhcWSClientConfig = AhcWSClientConfig(), ec: ExecutionContext = ExecutionContext.global)
  extends WebClient with DefaultBodyWritables {

  import Implicits._
  import PlayWSStandaloneWebClient._

  implicit val actorSystem = ActorSystem(actorSystemName, defaultExecutionContext = Some(ec))
  implicit val materializer = ActorMaterializer()
  val client = StandaloneAhcWSClient(clientConfig)

  val requestCompiler = new (RequestDSL.RequestOp ~> State[WSRequestData, ?]) {

    override def apply[A](fa: RequestDSL.RequestOp[A]) = fa match {
      case RequestDSL.SetUrl(url) =>
        State.modify[WSRequestData](_.copy(url = Some(new URL(url))))
      case RequestDSL.AddSegments(segments @ _*) =>
        State.modify[WSRequestData]{ state =>
          val encodedSegments = segments.map(URLEncoder.encode(_, "UTF-8"))
          val newUrl = state.url.map { r =>
            new URL(r, s"${r.getPath.trimRight('/')}/${encodedSegments.mkString("/")}")
          }
          state.copy(url = newUrl)
        }
      case RequestDSL.AddQueries(queries @ _*) =>
        State.modify[WSRequestData](state => state.copy(queryParams = state.queryParams ++ queries))
      case RequestDSL.SetHeaders(headers @ _*) =>
        State.modify[WSRequestData](state => state.copy(headers = state.headers ++ headers))
      case RequestDSL.SetMethod(method) =>
        State.modify[WSRequestData](state => state.copy(method = Some(method)))
      case RequestDSL.SetAuth(auth) =>
        val newAuth = auth match {
          case RequestDSL.AuthenticationMethod.Basic(user, password) =>
            Right(user, password.mkString, WSAuthScheme.BASIC)
          case RequestDSL.AuthenticationMethod.Digest(user, password) =>
            Right(user, password.mkString, WSAuthScheme.DIGEST)
          case RequestDSL.AuthenticationMethod.Bearer(token) =>
            Left(token)
        }
        State.modify[WSRequestData](state => state.copy(auth = Some(newAuth)))
      case RequestDSL.SetBody(body) =>
        val newBody = body match {
          case RequestDSL.Body.EmptyBody() =>
            None
          case RequestDSL.Body.FileBody(file) =>
            Some(WSSourceBody(StreamConverters.fromInputStream(() => new FileInputStream(file))))
          case RequestDSL.Body.InMemoryByteBody(bytes) =>
            Some(WSInMemoryBody(ByteString(bytes)))
          case RequestDSL.Body.StreamedBody(streamProvider) =>
            Some(WSSourceBody(StreamConverters.fromInputStream(streamProvider)))
        }
        State.modify[WSRequestData](state => state.copy(body = newBody))
      case RequestDSL.NoOp() =>
        State.pure[WSRequestData, Unit](())
    }
  }

  def responseCompiler(response: StandaloneWSResponse) =
    new (ResponseDSL.ResponseOp ~> Id) {
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

  def streamingResponseCompiler(response: StandaloneWSResponse) =
    new (StreamingResponseDSL.StreamingResponseOp ~> Id) {
    override def apply[A](fa: StreamingResponseDSL.StreamingResponseOp[A]) = fa match {
      case StreamingResponseDSL.GetBodyStream(osp) => Task.deferFuture {
        response.bodyAsSource.runWith(StreamConverters.fromOutputStream(osp)).flatMap {
          case r: IOResult if r.wasSuccessful => Future.successful(())
          case r: IOResult => Future.failed(r.getError)
        }
      }
      case StreamingResponseDSL.GetStatusCode() =>
        response.status
      case StreamingResponseDSL.GetHeaders() =>
        response.headers.mapValues(_.mkString(","))
    }
  }

  override def execute[T](
    requestCommand: RequestDSL.Request[Unit],
    responseCommand: ResponseDSL.Response[T]
  ): Task[T] = Task.evalOnce {
    compileRequestCommand(requestCommand)
  } flatMap { req => Task.fromFuture(req.execute) } map {
    compileResponseCommand(responseCommand)
  }

  def compileRequestCommand(requestCommand: RequestDSL.Request[Unit]): StandaloneWSRequest = {
    def mapAuth[A <: StandaloneWSRequest](request: A)(auth: Either[String, (String, String, WSAuthScheme)]): A#Self = {
      auth match {
        case Left(token) =>
          request.addHttpHeaders("Authentication" -> s"Bearer $token")
        case Right((user, pass, scheme)) =>
          request.withAuth(user, pass, scheme)
      }
    }

    val finalState = requestCommand.foldMap(requestCompiler).run(WSRequestData()).value._1
    finalState.url match {
      case Some(url) =>
        client.url(url.toExternalForm)
          .mutateIf(finalState.method, _.withMethod)
          .mutateIf(finalState.body, r => { (b: WSBody) => r.withBody(b) })
          .withHttpHeaders(finalState.headers.toSeq: _*)
          .withQueryStringParameters(finalState.queryParams: _*)
          .mutateIf(finalState.auth, mapAuth)
      case None =>
        throw new UnsupportedOperationException("Unable to compile request from provided AST")
    }
  }

  def compileResponseCommand[T](responseCommand: ResponseDSL.Response[T]): StandaloneWSResponse => T =
    response => {
      responseCommand.foldMap(responseCompiler(response))
    }
  override def executeStreaming[T](
    requestCommand: RequestDSL.Request[Unit],
    responseCommand: StreamingResponseDSL.StreamingResponse[T]
  ): Task[T] =
    Task.evalOnce {
      compileRequestCommand(requestCommand)
    } flatMap { req => Task.fromFuture(req.stream()) } map {
      compileStreamingResponseCommand(responseCommand)
    }
  def compileStreamingResponseCommand[T](
    responseCommand: StreamingResponseDSL.StreamingResponse[T]
  ): StandaloneWSResponse => T = response => {
    responseCommand.foldMap(streamingResponseCompiler(response))
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
