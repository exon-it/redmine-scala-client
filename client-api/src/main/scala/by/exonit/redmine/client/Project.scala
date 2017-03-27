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

trait ProjectIdLike extends Identifiable[BigInt]

case class ProjectId(id: BigInt) extends ProjectIdLike

case class ProjectLink(id: BigInt, name: String) extends ProjectIdLike

case class Project(id: BigInt,
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
                   enabledModules: Option[Set[ModuleLink]]) extends ProjectIdLike with OptionalCustomFieldSet

object Project {

  sealed abstract class Include(val token: String)

  object New {
    def apply(name: String, identifier: String) = new New(name, identifier)
  }
  class New(val name: String, val identifier: String) extends CustomFieldUpdateSetFSF[New] {
    val description = new FluentSettableField[String, New](this)
    val homepage = new FluentSettableField[String, New](this)
    val isPublic = new FluentSettableField[Boolean, New](this)
    val parent = new FluentSettableField[ProjectId, New](this)
    val inheritMembers = new FluentSettableField[Boolean, New](this)

    val trackers = new FluentSettableField[Set[TrackerId], New](this)
    val enabledModuleNames = new FluentSettableField[Set[String], New](this)
  }

  object Update {
    def apply() = new Update
  }
  class Update extends CustomFieldUpdateSetFSF[Update] {
    val name = new FluentSettableField[String, Update](this)
    val description = new FluentSettableField[Option[String], Update](this)
    val homepage = new FluentSettableField[Option[String], Update](this)
    val isPublic = new FluentSettableField[Boolean, Update](this)
    val parent = new FluentSettableField[Option[ProjectId], Update](this)
    val inheritMembers = new FluentSettableField[Boolean, Update](this)

    val trackers = new FluentSettableField[Set[TrackerId], Update](this)
    val enabledModuleNames = new FluentSettableField[Set[String], Update](this)
  }

  object Include {

    case object Trackers extends Include("trackers")

    case object IssueCategories extends Include("issue_categories")

    case object EnabledModules extends Include("enabled_modules")

  }

}
