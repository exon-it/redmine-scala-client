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

package by.exonit.redmine.client

import enumeratum.EnumEntry.Snakecase
import enumeratum._
import org.joda.time.{DateTime, LocalDate}

import scala.collection.immutable._

/**
  * Identifiable issue entity trait
  */
trait IssueIdLike extends Identifiable[BigInt]

/**
  * Issue ID entity type
  *
  * @param id Issue ID
  */
case class IssueId(id: BigInt) extends IssueIdLike

/**
  * Child issue entity type
  */
trait ChildIssueLike extends IssueIdLike {
  /**
    * Issue subject
    *
    * @return Issue subject
    */
  def subject: String

  /**
    * Issue tracker
    *
    * @return Issue tracker
    */
  def tracker: TrackerLink
}

/**
  * Child issue entity type
  *
  * Is returned as additional data for issue read operation
  *
  * @param id      Issue ID
  * @param subject Issue subject
  * @param tracker Issue tracker
  */
case class ChildIssue(
  id: BigInt, subject: String, tracker: TrackerLink
) extends ChildIssueLike

/**
  * Issue entity type
  *
  * @param id             ID
  * @param subject        Subject
  * @param parentId       Parent issue
  * @param estimatedHours Estimated work hours
  * @param spentHours     Spent hours
  * @param assignee       Assigned user
  * @param priority       Priority
  * @param doneRatio      Done ratio in percents
  * @param project        Project
  * @param author         Author
  * @param startDate      Start date
  * @param dueDate        Due date
  * @param tracker        Project tracker for issue
  * @param description    Description
  * @param createdOn      Created-on timestamp
  * @param updatedOn      Updated-on timestamp
  * @param status         Status
  * @param fixedVersion   Version
  * @param category       Category
  * @param isPrivate      Issue private status
  * @param relations      Issue relations
  * @param attachments    Attachments
  * @param customFields   Custom field values
  * @param journals       Journals
  * @param changesets     Changesets
  * @param watchers       Watcher users
  * @param children       Child issues
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
  children: Option[Set[ChildIssue]]
) extends ChildIssueLike with OptionalCustomFieldSet {

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
    */
  sealed abstract class Include extends EnumEntry with Snakecase

  /**
    * Predefined additional includes for issue read operation
    */
  object Include extends Enum[Include] {

    val values: IndexedSeq[Include] = findValues

    /**
      * Issue journals
      */
    case object Journals extends Include

    /**
      * Issue relations
      */
    case object Relations extends Include

    /**
      * Issue attachments
      */
    case object Attachments extends Include

    /**
      * Issue changesets
      */
    case object Changesets extends Include

    /**
      * Issue watchers
      */
    case object Watchers extends Include

    /**
      * Child issues
      */
    case object Children extends Include

  }

  /**
    * Issue upload entity type
    *
    * @param token       Upload token
    * @param filename    Attachment file name
    * @param description Attachment description
    * @param contentType Attachment MIME type
    */
  case class Upload(token: String, filename: String, description: Option[String], contentType: String)

  /**
    * New issue entity type for create operation
    *
    * @param subject Issue subject
    * @param project Issue project
    */
  case class New(
    subject: String,
    project: ProjectIdLike,
    author: Option[UserIdLike] = None,
    tracker: Option[TrackerIdLike] = None,
    status: Option[IssueStatusIdLike] = None,
    description: Option[String] = None,
    startDate: Option[LocalDate] = None,
    dueDate: Option[LocalDate] = None,
    priority: Option[Priority] = None,
    version: Option[VersionIdLike] = None,
    assignee: Option[UserIdLike] = None,
    parentIssue: Option[IssueIdLike] = None,
    doneRatio: Option[Int] = None,
    category: Option[CategoryIdLike] = None,
    estimatedHours: Option[BigDecimal] = None,
    isPrivate: Option[Boolean] = None,
    watchers: Option[Set[UserIdLike]] = None,
    uploads: Option[Set[Upload]] = None,
    customFields: Option[Set[CustomField.Update]] = None
  )

  /**
    * Issue update entity type companion object
    */
  object Update {

    /**
      * Update notes
      *
      * @param message   Journal message
      * @param isPrivate `true` if message should be private
      */
    case class Notes(message: String, isPrivate: Boolean = false)
  }

  /**
    * Issue update entity type
    */
  case class Update(
    author: Option[UserIdLike] = None,
    subject: Option[String] = None,
    description: Option[Option[String]] = None,
    startDate: Option[Option[LocalDate]] = None,
    dueDate: Option[Option[LocalDate]] = None,
    notes: Option[Update.Notes] = None,
    project: Option[ProjectIdLike] = None,
    tracker: Option[TrackerIdLike] = None,
    status: Option[IssueStatusIdLike] = None,
    priority: Option[PriorityIdLike] = None,
    version: Option[Option[VersionIdLike]] = None,
    doneRatio: Option[Int] = None,
    category: Option[CategoryIdLike] = None,
    estimatedHours: Option[Option[BigDecimal]] = None,
    isPrivate: Option[Boolean] = None,
    uploads: Option[Set[Upload]] = None,
    parentIssue: Option[Option[IssueIdLike]] = None,
    customFields: Option[Set[CustomField.Update]] = None
  )

}
