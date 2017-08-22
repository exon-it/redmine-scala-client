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

package by.exonit.redmine.client.playws.standalone.managers

import by.exonit.redmine.client.managers.WebClient.{RequestDSL, ResponseDSL}
import by.exonit.redmine.client.managers.impl.RedmineManagerFactory
import by.exonit.redmine.client.playws.standalone.fixtures.{ClientDriverFixture, WebClientFixture}
import by.exonit.redmine.client.playws.standalone.BasicSpec
import com.github.restdriver.clientdriver.ClientDriverRequest._
import com.github.restdriver.clientdriver.RestClientDriver._


class RedmineManagerFactoryImplSpec extends BasicSpec with ClientDriverFixture with WebClientFixture {

  case class RequestResult(status: Int, body: String)

  "RedmineManagerFactory" when {

    "creating unauthenticated manager" must {
      "not supply API key or basic-authentication request" in {
        clientDriver.addExpectation(
          onRequestTo("/test").withMethod(Method.GET).withoutHeader("key"), giveResponse("TEST", "text/plain"))

        val mf = new RedmineManagerFactory(webClient)
        val manager = mf.createUnauthenticated(clientDriver.getBaseUrl).requestManager
        val request = for {
          _ <- manager.baseRequest
          _ <- RequestDSL.addSegments("test")
          _ <- manager.authenticateRequest()
        } yield ()
        val responseCommand = for {
          s <- ResponseDSL.getStatusCode
          b <- ResponseDSL.getBodyAsString
        } yield RequestResult(s,b)
        
        val requestFuture = manager.client.execute(request, responseCommand)
        whenReady(requestFuture.runAsync) {res =>
          res.status shouldBe 200
          res.body shouldBe "TEST"
          clientDriver.verify()
        }
      }
    }

    "creating API key authenticated manager" must {
      "provide API key as query parameter" in {
        val apiKey = "testApiKey"

        clientDriver.addExpectation(
          onRequestTo("/test").withParam("key", apiKey).withMethod(Method.GET), giveResponse("TEST", "text/plain"))

        val mf = new RedmineManagerFactory(webClient)
        val manager = mf.createWithApiKey(clientDriver.getBaseUrl, apiKey).requestManager
        val request = for {
          _ <- manager.baseRequest
          _ <- RequestDSL.addSegments("test")
          _ <- manager.authenticateRequest()
        } yield ()
        val responseCommand = for {
          s <- ResponseDSL.getStatusCode
          b <- ResponseDSL.getBodyAsString
        } yield RequestResult(s,b)

        val requestFuture = manager.client.execute(request, responseCommand)
        whenReady(requestFuture.runAsync) {res =>
          res.status shouldBe 200
          res.body shouldBe "TEST"
          clientDriver.verify()
        }
      }
    }

    "creating manager with user authentication" must {
      "supply basic authentication with HTTP request" in {
        val user = "testUser"
        val password = "testPass"

        clientDriver.addExpectation(
          onRequestTo("/test").withMethod(Method.GET).withBasicAuth(user, password),
          giveResponse("TEST", "text/plain"))

        val mf = new RedmineManagerFactory(webClient)
        val manager = mf.createWithUserAuth(clientDriver.getBaseUrl, user, password).requestManager
        val request = for {
          _ <- manager.baseRequest
          _ <- RequestDSL.addSegments("test")
          _ <- manager.authenticateRequest()
        } yield ()
        val responseCommand = for {
          s <- ResponseDSL.getStatusCode
          b <- ResponseDSL.getBodyAsString
        } yield RequestResult(s,b)

        val requestFuture = manager.client.execute(request, responseCommand)
        whenReady(requestFuture.runAsync) {res =>
          res.status shouldBe 200
          res.body shouldBe "TEST"
          clientDriver.verify()
        }
      }
    }
  }
}
