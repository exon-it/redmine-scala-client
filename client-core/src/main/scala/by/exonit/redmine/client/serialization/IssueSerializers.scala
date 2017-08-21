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

object IssueSerializers {
  import by.exonit.redmine.client.serialization.Implicits._

  lazy val all: Seq[Serializer[_]] = Seq(
    issueIdSerializer,
    childIssueSerializer,
    issueSerializer,
    newIssueSerializer,
    issueUpdateSerializer,
    issueUploadSerializer)

  def deserializeIssueId: PartialFunction[JValue, IssueId] = {
    case JInt(id) => IssueId(id)
  }

  def serializeIssueId: PartialFunction[Any, JValue] = {
    case IssueId(id) => JInt(id)
  }

  object issueIdSerializer extends CustomSerializer[IssueId](
    _ => deserializeIssueId -> serializeIssueId)

  def deserializeChildIssue(implicit formats: Formats): PartialFunction[JValue, ChildIssue] = {
    case j: JObject =>
      ChildIssue(
        (j \ "id").extract[BigInt], (j \ "subject").extract[String], (j \ "tracker").extract[TrackerLink])
  }

  object childIssueSerializer extends CustomSerializer[ChildIssue](
    formats => (
      deserializeChildIssue(formats), PartialFunction.empty))

  def deserializeIssue(implicit formats: Formats): PartialFunction[JValue, Issue] = {
    case j: JObject =>
      val id = (j \ "id").extract[BigInt]
      val subject = (j \ "subject").extract[String]
      val description = (j \ "description").extractOpt[String]
      val parent = (j \ "parent" \ "id").extractOpt[BigInt].map(IssueId)
      val estimatedHours = (j \ "estimated_hours").extractOpt[BigDecimal]
      val spentHours = (j \ "spent_hours").extractOpt[BigDecimal]
      val assignee = (j \ "assigned_to").extractOpt[UserLink]
      val priority = (j \ "priority").extract[PriorityLink]
      val doneRatio = (j \ "done_ratio").extract[Int]
      val project = (j \ "project").extract[ProjectLink]
      val author = (j \ "author").extractOpt[UserLink]
      val startDate = (j \ "start_date").extractOpt[String].map(RedmineDateParser.parse).map(_.toLocalDate)
      val dueDate = (j \ "due_date").extractOpt[String].map(RedmineDateParser.parse).map(_.toLocalDate)
      val tracker = (j \ "tracker").extract[TrackerLink]
      val createdOn = RedmineDateParser.parse((j \ "created_on").extract[String])
      val updatedOn = RedmineDateParser.parse((j \ "updated_on").extract[String])
      val status = (j \ "status").extract[IssueStatusLink]
      val fixedVersion = (j \ "fixed_version").extractOpt[VersionLink]
      val category = (j \ "category").extractOpt[CategoryLink]
      val isPrivate = (j \ "is_private").extractOpt[Boolean].getOrElse(false)
      val relations = (j \ "relations").toOption.map(_.extract[Set[IssueRelation]])
      val attachments = (j \ "attachments").toOption.map(_.extract[Set[Attachment]])
      val customFields = (j \ "custom_fields").toOption.map(_.extract[Set[CustomField]])
      val journals = (j \ "journals").toOption.map(_.extract[Set[Journal]])
      val changesets = (j \ "changesets").toOption.map(_.extract[Set[Changeset]])
      val watchers = (j \ "watchers").toOption.map(_.extract[Set[UserLink]])
      val children = (j \ "children").toOption.map(_.extract[Set[ChildIssue]])
      Issue(
        id,
        subject,
        parent,
        estimatedHours,
        spentHours,
        assignee,
        priority,
        doneRatio,
        project,
        author,
        startDate,
        dueDate,
        tracker,
        description,
        createdOn,
        updatedOn,
        status,
        fixedVersion,
        category,
        isPrivate,
        relations,
        attachments,
        customFields,
        journals,
        changesets,
        watchers,
        children)
  }

  object issueSerializer extends CustomSerializer[Issue](
    formats => (
      deserializeIssue(formats), PartialFunction.empty))

  def serializeIssueUpdate(implicit formats: Formats): PartialFunction[Any, JValue] = {
    case u: Issue.Update =>
      ("subject" -> u.subject) ~
        ("description" -> u.description.map(_.map(Extraction.decompose).orJNull)) ~
        ("start_date" -> u.startDate.map(_.map(_.toRedmine2ShortDate).map(Extraction.decompose).orJNull)) ~
        ("due_date" -> u.dueDate.map(_.map(_.toRedmine2ShortDate).map(Extraction.decompose).orJNull)) ~
        ("project_id" -> u.project.map(_.id)) ~
        ("tracker_id" -> u.tracker.map(_.id)) ~
        ("status_id" -> u.status.map(_.id)) ~
        ("priority_id" -> u.priority.map(_.id)) ~
        ("version_id" -> u.version.map(_.map(_.id).map(Extraction.decompose).orJNull)) ~
        ("done_ratio" -> u.doneRatio) ~
        ("estimated_hours" -> u.estimatedHours.map(_.map(Extraction.decompose).orJNull)) ~
        ("category_id" -> u.category.map(_.id)) ~
        ("is_private" -> u.isPrivate) ~
        ("custom_fields" -> u.customFields.map(_.map(Extraction.decompose))) ~
        ("uploads" -> u.uploads.map(_.map(Extraction.decompose))) ~
        ("notes" -> u.notes.map(_.message).map(Extraction.decompose)) ~
        ("private_notes" -> u.notes.map(_.isPrivate)) ~
        ("author_id" -> u.author.map(_.id)) ~
        ("parent_issue_id" -> u.parentIssue.map(_.map(_.id).map(Extraction.decompose).orJNull))
  }

  object issueUpdateSerializer extends CustomSerializer[Issue.Update](
    formats => (
      PartialFunction.empty, serializeIssueUpdate(formats)))

  def serializeNewIssue(implicit formats: Formats): PartialFunction[Any, JValue] = {
    case i: Issue.New =>
      ("subject" -> i.subject) ~
        ("project_id" -> i.project.id) ~
        ("status_id" -> i.status.map(_.id)) ~
        ("description" -> i.description) ~
        ("start_date" -> i.startDate.map(_.toRedmine2ShortDate)) ~
        ("due_date" -> i.dueDate.map(_.toRedmine2ShortDate)) ~
        ("tracker_id" -> i.tracker.map(_.id)) ~
        ("status_id" -> i.status.map(_.id)) ~
        ("priority_id" -> i.priority.map(_.id)) ~
        ("version_id" -> i.version.map(_.id)) ~
        ("done_ratio" -> i.doneRatio) ~
        ("estimated_hours" -> i.estimatedHours) ~
        ("category_id" -> i.category.map(_.id)) ~
        ("is_private" -> i.isPrivate) ~
        ("custom_fields" -> i.customFields.map(_.map(Extraction.decompose))) ~
        ("uploads" -> i.uploads.map(_.map(Extraction.decompose))) ~
        ("author_id" -> i.author.map(_.id)) ~
        ("parent_issue_id" -> i.parentIssue.map(_.id)) ~
        ("assigned_to_id" -> i.assignee.map(_.id))
  }

  object newIssueSerializer extends CustomSerializer[Issue.New](
    formats => (
      PartialFunction.empty, serializeNewIssue(formats)))

  def serializeIssueUpload: PartialFunction[Any, JValue] = {
    case Issue.Upload(token, filename, description, contentType) =>
      ("token" -> token) ~
        ("filename" -> filename) ~
        ("description" -> description) ~
        ("content_type" -> contentType)
  }

  object issueUploadSerializer
    extends CustomSerializer[Issue.Upload](
      _ => PartialFunction.empty -> serializeIssueUpload)

}
