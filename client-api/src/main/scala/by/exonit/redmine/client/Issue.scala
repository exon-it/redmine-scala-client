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

import org.joda.time.{DateTime, LocalDate}

import scala.collection.immutable._

/**
 * Identifiable issue entity trait
 */
trait IssueIdLike extends Identifiable[BigInt]

/**
 * Issue ID entity type
 * @param id Issue ID
 */
case class IssueId(id: BigInt) extends IssueIdLike

/**
 * Child issue entity type
 */
trait ChildIssueLike extends IssueIdLike {
  /**
   * Issue subject
   * @return Issue subject
   */
  def subject: String

  /**
   * Issue tracker
   * @return Issue tracker
   */
  def tracker: TrackerLink
}

/**
 * Child issue entity type
 *
 * Is returned as additional data for issue read operation
 * @param id Issue ID
 * @param subject Issue subject
 * @param tracker Issue tracker
 */
case class ChildIssue(
  id: BigInt, subject: String, tracker: TrackerLink) extends ChildIssueLike

/**
 * Issue entity type
 * @param id ID
 * @param subject Subject
 * @param parentId Parent issue
 * @param estimatedHours Estimated work hours
 * @param spentHours Spent hours
 * @param assignee Assigned user
 * @param priority Priority
 * @param doneRatio Done ratio in percents
 * @param project Project
 * @param author Author
 * @param startDate Start date
 * @param dueDate Due date
 * @param tracker Project tracker for issue
 * @param description Description
 * @param createdOn Created-on timestamp
 * @param updatedOn Updated-on timestamp
 * @param status Status
 * @param fixedVersion Version
 * @param category Category
 * @param isPrivate Issue private status
 * @param relations Issue relations
 * @param attachments Attachments
 * @param customFields Custom field values
 * @param journals Journals
 * @param changesets Changesets
 * @param watchers Watcher users
 * @param children Child issues
 */
case class Issue(
  id: BigInt,
  subject: String,
  parentId: Option[IssueId],
  estimatedHours: Option[BigDecimal],
  spentHours: Option[BigDecimal],
  assignee: Option[UserLink],
  priority: PriorityLink,
  doneRatio: Int,
  project: ProjectLink,
  author: Option[UserLink],
  startDate: Option[LocalDate],
  dueDate: Option[LocalDate],
  tracker: TrackerLink,
  description: Option[String],
  createdOn: DateTime,
  updatedOn: DateTime,
  status: IssueStatusLink,
  fixedVersion: Option[VersionLink],
  category: Option[CategoryLink],
  isPrivate: Boolean,
  relations: Option[Set[IssueRelation]],
  attachments: Option[Set[Attachment]],
  customFields: Option[Set[CustomField]],
  journals: Option[Set[Journal]],
  changesets: Option[Set[Changeset]],
  watchers: Option[Set[UserLink]],
  children: Option[Set[ChildIssue]]) extends ChildIssueLike with OptionalCustomFieldSet {

  override def toString: String = {
    s"Issue [id=$id, subject=$subject]"
  }
}

/**
 * Issue entity type companion object
 */
object Issue {

  /**
   * Issue read operation additional include type
   * @param token Include token
   */
  sealed abstract class Include(val token: String)

  /**
   * Predefined additional includes for issue read operation
   */
  object Include {

    /**
     * Issue journals
     */
    case object Journals extends Include("journals")

    /**
     * Issue relations
     */
    case object Relations extends Include("relations")

    /**
     * Issue attachments
     */
    case object Attachments extends Include("attachments")

    /**
     * Issue changesets
     */
    case object Changesets extends Include("changesets")

    /**
     * Issue watchers
     */
    case object Watchers extends Include("watchers")

    /**
     * Child issues
     */
    case object Children extends Include("children")

  }

  /**
   * Issue upload entity type
   * @param token Upload token
   * @param filename Attachment file name
   * @param description Attachment description
   * @param contentType Attachment MIME type
   */
  case class Upload(token: String, filename: String, description: Option[String], contentType: String)

  /**
   * New issue entity type companion object
   */
  object New {
    def apply(subject: String, project: ProjectIdLike) = new New(subject, project)
  }

  /**
   * New issue entity type for create operation
   * @param subject Issue subject
   * @param project Issue project
   */
  class New(val subject: String, val project: ProjectIdLike) extends CustomFieldUpdateSetFSF[New] {
    /**
     * Issue author
     * @todo Not implemented yet (for Redmine 3.1.0)
     */
    val author = new FluentSettableField[UserIdLike, New](this)

    /**
     * Issue tracker
     */
    val tracker = new FluentSettableField[TrackerIdLike, New](this)

    /**
     * Issue status
     */
    val status = new FluentSettableField[IssueStatusIdLike, New](this)

    /**
     * Issue description
     */
    val description = new FluentSettableField[String, New](this)

    /**
     * Issue start date
     */
    val startDate = new FluentSettableField[LocalDate, New](this)

    /**
     * Issue due date
     */
    val dueDate = new FluentSettableField[LocalDate, New](this)

    /**
     * Issue priority
     */
    val priority = new FluentSettableField[PriorityIdLike, New](this)

    /**
     * Issue fixed version
     */
    val version = new FluentSettableField[VersionIdLike, New](this)

    /**
     * Issue assigned user
     */
    val assignee = new FluentSettableField[UserIdLike, New](this)

    /**
     * Parent issue
     */
    val parentIssue = new FluentSettableField[IssueIdLike, New](this)

    /**
     * Done ration in percent
     */
    val doneRatio = new FluentSettableField[Int, New](this)

    /**
     * Issue category
     */
    val category = new FluentSettableField[CategoryIdLike, New](this)

    /**
     * Estimated hours
     */
    val estimatedHours = new FluentSettableField[BigDecimal, New](this)

    /**
     * Private issue flag
     */
    val isPrivate = new FluentSettableField[Boolean, New](this)

    /**
     * Issue watchers
     */
    val watchers = new FluentSettableField[Set[UserIdLike], New](this)

    /**
     * Issue attachments
     */
    val uploads = new FluentSettableField[Set[Upload], New](this)
  }

  /**
   * Issue update entity type companion object
   */
  object Update {

    /**
     * Update notes
     * @param message Journal message
     * @param isPrivate `true` if message should be private
     */
    case class Notes(message: String, isPrivate: Boolean = false)

    def apply() = new Update
  }

  /**
   * Issue update entity type
   */
  class Update extends CustomFieldUpdateSetFSF[Update] {
    /**
     * Issue author
     * @todo Not implemented yet (for Redmine 3.1.0)
     */
    val author = new FluentSettableField[UserIdLike, Update](this)

    /**
     * Issue subject
     */
    val subject = new FluentSettableField[String, Update](this)

    /**
     * Issue description
     */
    val description = new FluentSettableField[Option[String], Update](this)

    /**
     * Issue start date
     */
    val startDate = new FluentSettableField[Option[LocalDate], Update](this)

    /**
     * Issue due date
     */
    val dueDate = new FluentSettableField[Option[LocalDate], Update](this)

    /**
     * Issue update notes
     */
    val notes = new FluentSettableField[Update.Notes, Update](this)

    /**
     * Issue project
     */
    val project = new FluentSettableField[ProjectIdLike, Update](this)

    /**
     * Issue tracker
     */
    val tracker = new FluentSettableField[TrackerIdLike, Update](this)

    /**
     * Issue status
     */
    val status = new FluentSettableField[IssueStatusIdLike, Update](this)

    /**
     * Issue priority
     */
    val priority = new FluentSettableField[PriorityIdLike, Update](this)

    /**
     * Issue fixed version
     */
    val version = new FluentSettableField[Option[VersionIdLike], Update](this)

    /**
     * Issue done ratio in percent
     */
    val doneRatio = new FluentSettableField[Int, Update](this)

    /**
     * Issue category
     */
    val category = new FluentSettableField[CategoryIdLike, Update](this)

    /**
     * Estimated work hours
     */
    val estimatedHours = new FluentSettableField[Option[BigDecimal], Update](this)

    /**
     * Issue private flag
     */
    val isPrivate = new FluentSettableField[Boolean, Update](this)

    /**
     * Uploads to attach to issue
     */
    val uploads = new FluentSettableField[Set[Upload], Update](this)
  }

}
