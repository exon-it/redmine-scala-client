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
import scala.collection.immutable._

trait TimeEntryIdLike extends Identifiable[BigInt]

case class TimeEntryId(id: BigInt) extends TimeEntryIdLike

case class TimeEntry(
  id: BigInt,
  project: ProjectLink,
  issueId: Option[IssueId],
  user: Option[UserLink],
  activity: ActivityLink,
  spentOn: DateTime,
  hours: BigDecimal,
  comment: Option[String],
  createdOn: DateTime,
  updatedOn: DateTime,
  customFields: Option[Set[CustomField]]) extends TimeEntryIdLike with OptionalCustomFieldSet

object TimeEntry {

  case class New(
    issueOrProjectId: Either[IssueIdLike, ProjectIdLike], hours: BigDecimal) extends CustomFieldUpdateSetFSF[New] {
    val spentOn = new FluentSettableField[DateTime, New](this)
    val activity = new FluentSettableField[ActivityIdLike, New](this)
    val comments = new FluentSettableField[String, New](this)
  }

  class Update extends CustomFieldUpdateSetFSF[Update] {
    val issueOrProjectId = new FluentSettableField[Either[IssueIdLike, ProjectIdLike], Update](this)
    val hours = new FluentSettableField[BigDecimal, Update](this)
    val spentOn = new FluentSettableField[DateTime, Update](this)
    val activity = new FluentSettableField[ActivityIdLike, Update](this)
    val comments = new FluentSettableField[Option[String], Update](this)
  }

}
