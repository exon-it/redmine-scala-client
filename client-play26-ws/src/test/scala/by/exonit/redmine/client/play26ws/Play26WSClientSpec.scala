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

import by.exonit.redmine.client.managers.WebClient.{RequestDSL, ResponseDSL}
import by.exonit.redmine.client.play26ws.fixtures.{ClientDriverFixture, WebClientFixture}
import com.github.restdriver.clientdriver.ClientDriverRequest._
import com.github.restdriver.clientdriver.RestClientDriver._
import play.api.http.Status


/**
  * Created by antonov_i on 28.02.2017.
  */
class Play26WSClientSpec extends BasicSpec with ClientDriverFixture with WebClientFixture {
  "Play-WS 2.6 Web Client" must {
    "issue correct requests when adding segments in setUrl" in {
      clientDriver.addExpectation(
        onRequestTo("/test").withMethod(Method.GET), giveResponse("TEST", "text/plain"))

      val request = for {
        _ <- RequestDSL.setUrl(clientDriver.getBaseUrl + "/test")
      } yield ()
      val responseCommand = for {
        s <- ResponseDSL.getStatusCode
        b <- ResponseDSL.getBodyAsString
      } yield s -> b

      val requestFuture = webClient.execute(request, responseCommand)
      whenReady(requestFuture.unsafeToFuture) {case (status, body) =>
        status shouldBe Status.OK
        body shouldBe "TEST"
        clientDriver.verify()
      }
    }

    "issue correct requests when adding segments with addSegments" in {
      clientDriver.addExpectation(
        onRequestTo("/test1/test2").withMethod(Method.GET), giveResponse("TEST", "text/plain"))

      val request = for {
        _ <- RequestDSL.setUrl(clientDriver.getBaseUrl)
        _ <- RequestDSL.addSegments("test1", "test2")
      } yield ()
      val responseCommand = for {
        s <- ResponseDSL.getStatusCode
        b <- ResponseDSL.getBodyAsString
      } yield s -> b

      val requestFuture = webClient.execute(request, responseCommand)
      whenReady(requestFuture.unsafeToFuture) {case (status, body) =>
        status shouldBe Status.OK
        body shouldBe "TEST"
        clientDriver.verify()
      }
    }

    "issue correct requests when adding segments with both setUrl and addSegments" in {
      clientDriver.addExpectation(
        onRequestTo("/test1/test2").withMethod(Method.GET), giveResponse("TEST", "text/plain"))

      val request = for {
        _ <- RequestDSL.setUrl(clientDriver.getBaseUrl + "/test1")
        _ <- RequestDSL.addSegments("test2")
      } yield ()
      val responseCommand = for {
        s <- ResponseDSL.getStatusCode
        b <- ResponseDSL.getBodyAsString
      } yield s -> b

      val requestFuture = webClient.execute(request, responseCommand)
      whenReady(requestFuture.unsafeToFuture) {case (status, body) =>
        status shouldBe Status.OK
        body shouldBe "TEST"
        clientDriver.verify()
      }
    }

    "prevents double slashes when adding segments with both setUrl and addSegments" in {
      clientDriver.addExpectation(
        onRequestTo("/test1/test2").withMethod(Method.GET), giveResponse("TEST", "text/plain"))

      val request = for {
        _ <- RequestDSL.setUrl(clientDriver.getBaseUrl + "/test1/")
        _ <- RequestDSL.addSegments("test2")
      } yield ()
      val responseCommand = for {
        s <- ResponseDSL.getStatusCode
        b <- ResponseDSL.getBodyAsString
      } yield s -> b

      val requestFuture = webClient.execute(request, responseCommand)
      whenReady(requestFuture.unsafeToFuture) {case (status, body) =>
        status shouldBe Status.OK
        body shouldBe "TEST"
        clientDriver.verify()
      }
    }

  }
}
