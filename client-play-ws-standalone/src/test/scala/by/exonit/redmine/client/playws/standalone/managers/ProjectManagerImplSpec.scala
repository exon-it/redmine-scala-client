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

package by.exonit.redmine.client.playws.standalone.managers

import by.exonit.redmine.client.Project
import by.exonit.redmine.client.managers.impl.RedmineManagerFactory
import by.exonit.redmine.client.playws.standalone.fixtures.{ClientDriverFixture, WebClientFixture}
import by.exonit.redmine.client.playws.standalone.BasicSpec
import com.github.restdriver.clientdriver.RestClientDriver._
import org.scalatest.concurrent.PatienceConfiguration.Timeout
import org.scalatest.time.{Seconds, Span}

/**
  * Created by keritaf on 04.12.16.
  */
class ProjectManagerImplSpec extends BasicSpec with ClientDriverFixture with WebClientFixture {
  "Project manager for Play-WS implementation" when {
    "getting projects list" should {
      "issue correct requests for paged load" ignore {
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
        whenReady(request.unsafeToFuture) {_ =>
          clientDriver.verify()
        }
      }

      "return projects for default request" in {
        val projectsJson = this.getClass.getResourceAsStream("/project/redmine_projects_basic.json")
        clientDriver.addExpectation(
          onRequestTo("/projects.json").withParam("offset", 0).withParam("limit", 25),
          giveResponseAsBytes(projectsJson, jsonContentType))
        val manager = RedmineManagerFactory(webClient).createUnauthenticated(clientDriver.getBaseUrl)
        val pm = manager.projectManager
        val request = pm.getProjects()
        whenReady(request.unsafeToFuture, Timeout(Span(4, Seconds))) {projects =>
          clientDriver.verify()
          projects should not be null
          projects.total shouldBe 3
          projects.items.size shouldBe 3
          inside(projects.items.find(_.id == 1).value) {case p: Project =>
            p.name shouldBe "Test1"
          }
          inside(projects.items.find(_.id == 2).value) {case p: Project =>
            p.name shouldBe "test1 child1"
          }
        }
      }

      "return projects for multi-page list" ignore {
        val issuesPage1 = this.getClass.getResourceAsStream("/responses/projects_paged_1.json")
        val issuesPage2 = this.getClass.getResourceAsStream("/responses/projects_paged_2.json")
        clientDriver.addExpectation(
          onRequestTo("/projects.json").withParam("offset", 0).withParam("limit", 25),
          giveResponseAsBytes(issuesPage1, jsonContentType))
        clientDriver.addExpectation(
          onRequestTo("/projects.json").withParam("offset", 25).withParam("limit", 25),
          giveResponseAsBytes(issuesPage2, jsonContentType))
        val manager = RedmineManagerFactory(webClient)
          .createUnauthenticated(clientDriver.getBaseUrl)
        val im = manager.projectManager
        val request = im.getProjects()
        whenReady(request.unsafeToFuture) {projects1 =>
          clientDriver.verify()
          projects1 should not be null
          projects1.total shouldBe 50
          projects1.items.size shouldBe 25
          inside(projects1.items.find(_.id == 20).value) {case p: Project =>
            p.name shouldBe "1"
          }

          whenReady(projects1.next.value.unsafeToFuture) { projects2 =>
            projects2.items.size shouldBe 6
            inside(projects2.items.find(_.id == 49).value) {case p: Project =>
              p.name shouldBe "30"
            }
          }
        }
      }
    }
  }
}
