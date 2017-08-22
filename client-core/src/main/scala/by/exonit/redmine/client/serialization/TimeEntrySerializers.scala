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

package by.exonit.redmine.client.serialization

import by.exonit.redmine.client._
import org.json4s._
import org.json4s.JsonDSL._

import scala.collection.immutable._

object TimeEntrySerializers {
  import Implicits._
  lazy val all: Seq[Serializer[_]] = Seq(
    timeEntryIdSerializer, timeEntrySerializer, newTimeEntrySerializer, timeEntryUpdateSerializer)

  def deserializeTimeEntryId: PartialFunction[JValue, TimeEntryId] = {
    case JInt(id) => TimeEntryId(id)
  }

  def serializeTimeEntryId: PartialFunction[Any, JValue] = {
    case TimeEntryId(id) => JInt(id)
  }

  object timeEntryIdSerializer extends CustomSerializer[TimeEntryId](
    _ => deserializeTimeEntryId -> serializeTimeEntryId)

  def deserializeTimeEntry(implicit formats: Formats): PartialFunction[JValue, TimeEntry] = {
    case j: JObject =>
      TimeEntry(
        (j \ "id").extract[BigInt],
        (j \ "project").extract[ProjectLink],
        (j \ "issue" \ "id").extractOpt[IssueId],
        (j \ "user").extractOpt[UserLink],
        (j \ "activity").extract[ActivityLink],
        RedmineDateParser.parseLocalDate((j \ "spent_on").extract[String]),
        (j \ "hours").extract[BigDecimal],
        (j \ "comment").extractOpt[String],
        RedmineDateParser.parse((j \ "created_on").extract[String]),
        RedmineDateParser.parse((j \ "updated_on").extract[String]),
        (j \ "custom_fields").toOption.map(_.extract[Set[CustomField]]))
  }

  object timeEntrySerializer extends CustomSerializer[TimeEntry](
    formats => deserializeTimeEntry(formats) -> PartialFunction.empty)

  def serializeNewTimeEntry(implicit formats: Formats): PartialFunction[Any, JValue] = {
    case te: TimeEntry.New =>
      ("issue_id" -> te.issueOrProjectId.left.toOption.map(_.id)) ~
        ("project_id" -> te.issueOrProjectId.right.toOption.map(_.id)) ~
        ("hours" -> te.hours) ~
        ("spent_on" -> te.spentOn.map(_.toRedmine2ShortDate)) ~
        ("activity_id" -> te.activity.map(_.id)) ~
        ("comments" -> te.comments) ~
        ("custom_fields" -> te.customFields.map(_.map(Extraction.decompose)))
  }

  object newTimeEntrySerializer extends CustomSerializer[TimeEntry.New](
    formats => PartialFunction.empty -> serializeNewTimeEntry(formats))

  def serializeTimeEntryUpdate(implicit formats: Formats): PartialFunction[Any, JValue] = {
    case te: TimeEntry.Update =>
      ("issue_id" -> te.issueOrProjectId.flatMap(_.left.toOption.map(_.id))) ~
        ("project_id" -> te.issueOrProjectId.flatMap(_.right.toOption.map(_.id))) ~
        ("hours" -> te.hours) ~
        ("spent_on" -> te.spentOn.map(_.toRedmine2ShortDate)) ~
        ("activity_id" -> te.activity.map(_.id)) ~
        ("comments" -> te.comments.map(_.orJNull).orJNothing) ~
        ("custom_fields" -> te.customFields.map(_.map(Extraction.decompose)))
  }

  object timeEntryUpdateSerializer extends CustomSerializer[TimeEntry.Update](
    formats => PartialFunction.empty -> serializeTimeEntryUpdate(formats))

}
