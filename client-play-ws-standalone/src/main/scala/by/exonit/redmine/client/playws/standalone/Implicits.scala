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

package by.exonit.redmine.client.playws.standalone

import akka.stream.scaladsl.StreamConverters
import by.exonit.redmine.client.managers.WebClient.RequestDSL
import by.exonit.redmine.client.managers.WebClient.RequestDSL.Body.{EmptyBody, FileBody, InMemoryByteBody, StreamedBody}
import play.api.libs.ws
import play.api.libs.ws.{StandaloneWSRequest, WSAuthScheme}

/**
  * Created by antonov_i on 01.03.2017.
  */
object Implicits {

  implicit class StringExtensions(s: String) {

    def trimRight(trimChars: Char*): String =
      (s.reverseIterator dropWhile trimChars.contains)
        .toSeq.reverseIterator.mkString

  }

  implicit class OptionMutatable[A](value: A) {
    def mutateIf[B, C >: A](opt: => Option[B], mutator: A => B => C): C = {
      opt match {
        case Some(o) => mutator(value)(o)
        case None => value
      }
    }
  }

  implicit class ExtendedWSRequest(r: StandaloneWSRequest) {
    def withDslAuth(auth: RequestDSL.AuthenticationMethod): StandaloneWSRequest = {
      auth match {
        case RequestDSL.AuthenticationMethod.Basic(user, password) =>
          r.withAuth(user, password.mkString, WSAuthScheme.BASIC)
        case RequestDSL.AuthenticationMethod.Digest(user, password) =>
          r.withAuth(user, password.mkString, WSAuthScheme.DIGEST)
        case RequestDSL.AuthenticationMethod.Bearer(token) =>
          r.addHttpHeaders("Authentication" -> s"Bearer $token")
      }
    }

    def withDslBody(body: RequestDSL.Body): StandaloneWSRequest = {
      import play.api.libs.ws.DefaultBodyWritables._
      body match {
        case EmptyBody() =>
          r.withBody(ws.EmptyBody)
        case FileBody(file) =>
          r.withBody(file)
        case InMemoryByteBody(b) =>
          r.withBody(b)
        case StreamedBody(streamProvider) =>
          r.withBody(ws.SourceBody(StreamConverters.fromInputStream(streamProvider)))
      }
    }
  }
}
