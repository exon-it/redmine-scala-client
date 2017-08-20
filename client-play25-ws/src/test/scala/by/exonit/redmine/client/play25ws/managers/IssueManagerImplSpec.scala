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

package by.exonit.redmine.client.play25ws.managers

import by.exonit.redmine.client.managers.impl.RedmineManagerFactory
import by.exonit.redmine.client.{Issue, IssueId}
import by.exonit.redmine.client.play25ws.BasicSpec
import by.exonit.redmine.client.play25ws.fixtures.{ClientDriverFixture, WebClientFixture}
import com.github.restdriver.clientdriver.RestClientDriver._

class IssueManagerImplSpec extends BasicSpec with ClientDriverFixture with WebClientFixture {
  "Issue manager Play-WS implementation" when {
    "getting issue by id" should {
      "use correct request for unauthenticated client" in {
        val issueId: BigInt = 1234
        val issueJson = this.getClass.getResourceAsStream("/responses/issue_basic.json")
        clientDriver
          .addExpectation(onRequestTo(s"/issues/$issueId.json"), giveResponseAsBytes(issueJson, jsonContentType))

        val manager = RedmineManagerFactory(webClient).createUnauthenticated(clientDriver.getBaseUrl)
        val im = manager.issueManager
        val request = im.getIssue(IssueId(issueId))
        whenReady(request.runAsync) {_ =>
          clientDriver.verify()
        }
      }

      "use correct request for API key client" in {
        val key = "testKey"
        val issueId: BigInt = 1234
        val issueJson = this.getClass.getResourceAsStream("/responses/issue_basic.json")
        clientDriver.addExpectation(
          onRequestTo(s"/issues/$issueId.json").withParam("key", key), giveResponseAsBytes(issueJson, jsonContentType))

        val manager = RedmineManagerFactory(webClient).createWithApiKey(clientDriver.getBaseUrl, key)
        val im = manager.issueManager
        val request = im.getIssue(IssueId(issueId))
        whenReady(request.runAsync) {_ =>
          clientDriver.verify()
        }
      }
    }

    "getting issue list" should {
      "issue correct requests for paged load" in {
        val issuesPage1 = this.getClass.getResourceAsStream("/responses/issues_paged_1.json")
        val issuesPage2 = this.getClass.getResourceAsStream("/responses/issues_paged_2.json")
        clientDriver.addExpectation(
          onRequestTo("/issues.json").withParam("offset", 0).withParam("limit", 25),
          giveResponseAsBytes(issuesPage1, jsonContentType))
        clientDriver.addExpectation(
          onRequestTo("/issues.json").withParam("offset", 25).withParam("limit", 25),
          giveResponseAsBytes(issuesPage2, jsonContentType))
        val manager = RedmineManagerFactory(webClient)
          .createUnauthenticated(clientDriver.getBaseUrl)
        val im = manager.issueManager
        val request = im.getIssues()
        whenReady(request.flatMap(_.next.value).runAsync) {_ =>
          clientDriver.verify()
        }
      }

      "return issues for one-page list" in {
        val issuesJson = this.getClass.getResourceAsStream("/responses/issues_basic.json")
        clientDriver.addExpectation(
          onRequestTo("/issues.json").withParam("offset", 0).withParam("limit", 25),
          giveResponseAsBytes(issuesJson, jsonContentType))
        val manager = RedmineManagerFactory(webClient)
          .createUnauthenticated(clientDriver.getBaseUrl)
        val im = manager.issueManager
        val request = im.getIssues()
        whenReady(request.runAsync) {issues =>
          clientDriver.verify()
          issues should not be null
          issues.total shouldBe 2
          inside(issues.items.find(_.id == 48).value) {case i: Issue =>
            i.subject shouldBe "29"
          }
          inside(issues.items.find(_.id == 49).value) {case i: Issue =>
            i.subject shouldBe "30"
          }
        }
      }

      "return issues for multi-page list" in {
        val issuesPage1 = this.getClass.getResourceAsStream("/responses/issues_paged_1.json")
        val issuesPage2 = this.getClass.getResourceAsStream("/responses/issues_paged_2.json")
        clientDriver.addExpectation(
          onRequestTo("/issues.json").withParam("offset", 0).withParam("limit", 25),
          giveResponseAsBytes(issuesPage1, jsonContentType))
        clientDriver.addExpectation(
          onRequestTo("/issues.json").withParam("offset", 25).withParam("limit", 25),
          giveResponseAsBytes(issuesPage2, jsonContentType))
        val manager = RedmineManagerFactory(webClient).createUnauthenticated(clientDriver.getBaseUrl)
        val im = manager.issueManager
        val request = im.getIssues()
        whenReady(request.runAsync) {issues =>
          issues should not be null
          issues.total shouldBe 30
          issues.items.size shouldBe 25
          inside(issues.items.find(_.id == 49).value) {case i: Issue =>
            i.subject shouldBe "30"
          }

          whenReady(issues.next.value.runAsync) { issues2 =>
            clientDriver.verify()
            issues2.items.size shouldBe 5
            inside(issues2.items.find(_.id == 20).value) {case i: Issue =>
              i.subject shouldBe "1"
            }
          }
        }
      }
    }
  }
}
