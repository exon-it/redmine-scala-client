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

package by.exonit.redmine.client

import org.joda.time.DateTime
import scala.collection.immutable

class IssueSpec extends BasicSpec {
  "Issue" when {
    val defaultIssue = Issue(
      42, "test subject", None, None, None, None, PriorityLink(1, "priority"), 0, ProjectLink(1, "project"), None, None,
      None, TrackerLink(1, "tracker"), None, DateTime.now(), DateTime.now(), IssueStatusLink(1, "status"), None, None,
      isPrivate = false, None, None, None, None, None, None, None)

    "getting string representation" should {
      "return customized string" in {
        val issue = defaultIssue
        issue.toString() shouldBe "Issue [id=42, subject=test subject]"
      }
    }

    "getting custom field by id" should {
      "return the field if found" in {
        val testField = CustomField.SingleValue(42, "test", Some("test value"))
        val customFields = immutable.Set[CustomField](testField)
        val issue = defaultIssue.copy(customFields = Some(customFields))
        issue.getCustomFieldById(CustomFieldId(42)).value shouldBe testField
      }

      "return None if no custom field exists" in {
        val issue = defaultIssue.copy(customFields = Some(immutable.Set()))
        issue.getCustomFieldById(CustomFieldId(42)) shouldBe None
      }

      "return None if custom fields are not defined" in {
        val issue = defaultIssue
        issue.getCustomFieldById(CustomFieldId(42)) shouldBe None
      }
    }

    "getting custom field by name" should {
      "return the field if found" in {
        val testField = CustomField.SingleValue(42, "test", Some("test value"))
        val customFields = immutable.Set[CustomField](testField)
        val issue = defaultIssue.copy(customFields = Some(customFields))
        issue.getCustomFieldByName("test").value shouldBe testField
      }

      "return None if no custom field exists" in {
        val issue = defaultIssue.copy(customFields = Some(immutable.Set()))
        issue.getCustomFieldByName("test") shouldBe None
      }

      "return None if custom fields are not defined" in {
        val issue = defaultIssue
        issue.getCustomFieldByName("test") shouldBe None
      }
    }
  }

  "Issue update" should {
    "allow creation using object apply method" in {
      Issue.Update() shouldBe an[Issue.Update]
    }
    "create fluent settable fields when created" in {
      val issueUpdate = new Issue.Update
      issueUpdate.category should not be null
      issueUpdate.description should not be null
      issueUpdate.doneRatio should not be null
      issueUpdate.dueDate should not be null
      issueUpdate.estimatedHours should not be null
      issueUpdate.isPrivate should not be null
      issueUpdate.notes should not be null
      issueUpdate.priority should not be null
      issueUpdate.project should not be null
      issueUpdate.startDate should not be null
      issueUpdate.status should not be null
      issueUpdate.subject should not be null
      issueUpdate.tracker should not be null
      issueUpdate.uploads should not be null
      issueUpdate.version should not be null
    }
  }

  "New issue" should {
    "allow creation using object apply method" in {
      Issue.New("subject", ProjectId(1)) shouldBe an[Issue.New]
    }

    "create fluent settable fields when created" in {
      val newIssue = new Issue.New("subject", ProjectId(1))
      newIssue.category should not be null
      newIssue.description should not be null
      newIssue.doneRatio should not be null
      newIssue.dueDate should not be null
      newIssue.estimatedHours should not be null
      newIssue.isPrivate should not be null
      newIssue.priority should not be null
      newIssue.startDate should not be null
      newIssue.status should not be null
      newIssue.tracker should not be null
      newIssue.uploads should not be null
      newIssue.version should not be null
    }
  }
}
