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

package by.exonit.redmine.client.serialization

import by.exonit.redmine.client._
import org.joda.time.{DateTime, DateTimeZone, LocalDate}
import org.json4s._
import org.json4s.jackson.Serialization
import org.json4s.jackson.JsonMethods._

class IssueSerializersSpec extends BasicSpec {
  implicit val formats: Formats = Serialization.formats(NoTypeHints) ++ Serializers.all
  "Issue serializer" when {
    "deserializing valid issue JSON" should {
      "produce valid issue instance" in {
        val issueFile = this.getClass.getResourceAsStream("/issues/issue_basic.json")
        val json = parse(issueFile)
        val issue = json.extract[Issue]
        inside(issue) { case i: Issue =>
          i.id shouldBe 1234
          inside(i.project) { case ProjectLink(id, name) =>
            id shouldBe 12345
            name shouldBe "Test project"
          }
          inside(i.tracker) { case TrackerLink(id, name) =>
            id shouldBe 23451
            name shouldBe "Test tracker"
          }
          inside(i.status) { case IssueStatusLink(id, name) =>
            id shouldBe 34512
            name shouldBe "Test status"
          }
          inside(i.priority) { case PriorityLink(id, name) =>
            id shouldBe 45123
            name shouldBe "Test priority"
          }
          inside(i.author.value) { case UserLink(id, name) =>
            id shouldBe 51234
            name shouldBe "Test Author"
          }
          inside(i.assignee.value) { case UserLink(id, name) =>
            id shouldBe 54321
            name shouldBe "Test Assignee"
          }
          inside(i.category.value) { case CategoryLink(id, name) =>
            id shouldBe 43215
            name shouldBe "Test category"
          }
          inside(i.fixedVersion.value) { case VersionLink(id, name) =>
            id shouldBe 32154
            name shouldBe "Test version"
          }
          inside(i.parentId.value) { case IssueId(id) =>
            id shouldBe 21543
          }
          i.subject shouldBe "Test issue"
          i.description.value shouldBe "Test description"
          i.startDate.value shouldBe new LocalDate(2015, 7, 17)
          i.dueDate.value shouldBe new LocalDate(2015, 7, 23)
          i.doneRatio shouldBe 30
          i shouldBe 'private
          i.estimatedHours.value shouldBe BigDecimal("11.2")
          i.spentHours.value shouldBe BigDecimal("0.9")
          i.customFields.value should contain only CustomField
            .SingleValue(32154, "Test custom field", Some("Test CF value"))
          i.createdOn.toInstant shouldBe new DateTime(2015, 7, 17, 12, 40, 15, DateTimeZone.UTC).toInstant
          i.updatedOn.toInstant shouldBe new DateTime(2015, 8, 19, 7, 25, 14, DateTimeZone.UTC).toInstant
          i.relations shouldBe None
          i.attachments shouldBe None
          i.journals shouldBe None
          i.changesets shouldBe None
          i.watchers shouldBe None
          i.children shouldBe None
        }
      }
    }
  }

  "Issue ID serializer" when {
    "deserializing issue ID" should {
      "produce correct IssueId for integer values" in {
        val issueId = JInt(1234).extract[IssueId]
        issueId should not be null
        inside(issueId) { case IssueId(id) =>
          id shouldBe 1234
        }
      }

      "throw MappingException for string values" in {
        intercept[MappingException] {
          JString("1234").extract[IssueId]
        }
      }
    }

    "serializing issue ID" should {
      "produce JInt" in {
        val id: BigInt = 1234
        val issueId = IssueId(id)
        val json = Extraction.decompose(issueId)
        json shouldBe JInt(id)
      }

      "produce JNull for null ID" in {
        val issueId: IssueId = null
        val json = Extraction.decompose(issueId)
        json shouldBe JNull
      }
    }
  }
}
