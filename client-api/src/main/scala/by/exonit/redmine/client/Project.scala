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

import enumeratum.EnumEntry.Snakecase
import enumeratum._
import org.joda.time.DateTime

import scala.collection.immutable._

trait ProjectIdLike extends Identifiable[BigInt]

case class ProjectId(id: BigInt) extends ProjectIdLike

case class ProjectLink(id: BigInt, name: String) extends ProjectIdLike

case class Project(
  id: BigInt,
  identifier: String,
  parent: Option[ProjectLink],
  name: String,
  description: Option[String],
  homepage: Option[String],
  createdOn: DateTime,
  updatedOn: DateTime,
  isPublic: Option[Boolean],
  customFields: Option[Set[CustomField]],
  trackers: Option[Set[TrackerLink]],
  issueCategories: Option[Set[CategoryLink]],
  enabledModules: Option[Set[ModuleLink]],
  timeEntryActivities: Option[Set[ActivityLink]]
) extends ProjectIdLike with OptionalCustomFieldSet

object Project {

  case class New(
    name: String,
    identifier: String,
    description: Option[String] = None,
    homepage: Option[String] = None,
    isPublic: Option[Boolean] = None,
    parent: Option[ProjectIdLike] = None,
    inheritMembers: Option[Boolean] = None,
    trackers: Option[Set[TrackerIdLike]] = None,
    enabledModuleNames: Option[Set[String]] = None,
    customFields: Option[Set[CustomField.Update]] = None
  )

  case class Update(
    name: Option[String] = None,
    description: Option[Option[String]] = None,
    homepage: Option[Option[String]] = None,
    isPublic: Option[Boolean] = None,
    parent: Option[Option[ProjectIdLike]] = None,
    inheritMembers: Option[Boolean] = None,
    trackers: Option[Set[TrackerIdLike]] = None,
    enabledModuleNames: Option[Set[String]] = None,
    customFields: Option[Set[CustomField.Update]] = None
  )

  sealed abstract class Include extends EnumEntry with Snakecase

  object Include extends Enum[Include] {

    val values: IndexedSeq[Include] = findValues

    case object Trackers extends Include

    case object IssueCategories extends Include

    case object EnabledModules extends Include

    case object TimeEntryActivities extends Include
  }

}
