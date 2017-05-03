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

package by.exonit.redmine.client.managers

import java.io.{File, InputStream, OutputStream}

import cats.free.Free
import cats.free.Free.liftF
import monix.eval.Task

import scala.collection.immutable._

object WebClient {

  object Constants {
    lazy val ApiKeyQueryParameterName = "key"
    lazy val ContentTypeHeader        = "Content-Type"
  }

  object RequestDSL {

    sealed trait AuthenticationMethod

    object AuthenticationMethod {

      sealed case class Basic(user: String, password: IndexedSeq[Char]) extends AuthenticationMethod

      sealed case class Digest(user: String, password: IndexedSeq[Char]) extends AuthenticationMethod

      sealed case class Bearer(token: IndexedSeq[Char]) extends AuthenticationMethod

    }

    sealed trait Body

    object Body {

      sealed case class EmptyBody() extends Body

      sealed case class FileBody(file: File) extends Body

      sealed case class InMemoryByteBody(body: Array[Byte]) extends Body

      sealed case class StreamedBody(streamProvider: () => InputStream) extends Body

    }

    type Request[Req] = Free[RequestOp, Req]

    sealed trait RequestOp[Req]

    sealed case class SetUrl(url: String) extends RequestOp[Unit]

    sealed case class AddSegments(segments: String*) extends RequestOp[Unit]

    sealed case class AddQueries(queries: (String, String)*) extends RequestOp[Unit]

    sealed case class SetHeaders(headers: (String, String)*) extends RequestOp[Unit]

    sealed case class SetMethod(method: String) extends RequestOp[Unit]

    sealed case class SetAuth(auth: AuthenticationMethod) extends RequestOp[Unit]

    sealed case class SetBody(body: Body) extends RequestOp[Unit]

    sealed case class NoOp() extends RequestOp[Unit]

    def setUrl(url: String): Request[Unit] =
      liftF[RequestOp, Unit](SetUrl(url))

    def addSegments(segments: String*): Request[Unit] =
      liftF[RequestOp, Unit](AddSegments(segments: _*))

    def addQueries(queries: (String, String)*): Request[Unit] =
      liftF[RequestOp, Unit](AddQueries(queries: _*))

    def setHeaders(headers: (String, String)*): Request[Unit] =
      liftF[RequestOp, Unit](SetHeaders(headers: _*))

    def setMethod(method: String): Request[Unit] =
      liftF[RequestOp, Unit](SetMethod(method))

    def setAuth(auth: AuthenticationMethod): Request[Unit] =
      liftF[RequestOp, Unit](SetAuth(auth))

    def setContentType(contentType: String, charset: String): Request[Unit] =
      setHeaders(Constants.ContentTypeHeader -> s"${contentType.toLowerCase}; charset=${charset.toLowerCase}")

    def setContentType(contentType: String): Request[Unit] =
      setHeaders(Constants.ContentTypeHeader -> s"${contentType.toLowerCase}")

    def setBody(body: Body): Request[Unit] =
      liftF[RequestOp, Unit](SetBody(body))

    def noOp(): Request[Unit] =
      liftF[RequestOp, Unit](NoOp())
  }

  object ResponseDSL {

    sealed trait ResponseOp[Res]

    sealed case class GetBodyAsBytes() extends ResponseOp[Array[Byte]]

    sealed case class GetBodyAsString() extends ResponseOp[String]

    sealed case class GetStatusCode() extends ResponseOp[Int]

    sealed case class GetStatusText() extends ResponseOp[String]

    sealed case class GetHeaders() extends ResponseOp[Map[String, String]]

    type Response[Res] = Free[ResponseOp, Res]

    def getHeaders: Response[Map[String, String]] =
      liftF[ResponseOp, Map[String, String]](GetHeaders())

    def getStatusCode: Response[Int] =
      liftF[ResponseOp, Int](GetStatusCode())

    def getStatusText: Response[String] =
      liftF[ResponseOp, String](GetStatusText())

    def getBodyAsString: Response[String] =
      liftF[ResponseOp, String](GetBodyAsString())

    def getBodyAsBytes: Response[Array[Byte]] =
      liftF[ResponseOp, Array[Byte]](GetBodyAsBytes())
  }

  object StreamingResponseDSL {

    sealed trait StreamingResponseOp[Res]

    sealed case class GetStatusCode() extends StreamingResponseOp[Int]

    sealed case class GetHeaders() extends StreamingResponseOp[Map[String, String]]

    sealed case class GetBodyStream(outputStreamProvider: () => OutputStream) extends StreamingResponseOp[Task[Unit]]

    type StreamingResponse[A] = Free[StreamingResponseOp, A]

    def getHeaders: StreamingResponse[Map[String, String]] =
      liftF[StreamingResponseOp, Map[String, String]](GetHeaders())

    def getStatusCode: StreamingResponse[Int] =
      liftF[StreamingResponseOp, Int](GetStatusCode())

    def getBodyStream(outputStreamProvider: () => OutputStream): StreamingResponse[Task[Unit]] =
      liftF[StreamingResponseOp, Task[Unit]](GetBodyStream(outputStreamProvider))
  }
}

trait WebClient {

  import WebClient._

  def execute[T](requestCommand: RequestDSL.Request[Unit], responseCommand: ResponseDSL.Response[T]): Task[T]

  def executeStreaming[T](
    requestCommand : RequestDSL.Request[Unit],
    responseCommand: StreamingResponseDSL.StreamingResponse[T]): Task[T]
}
