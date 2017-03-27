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

import org.joda.time.{LocalDate, DateTime}
import scala.collection.immutable._

trait VersionIdLike extends Identifiable[BigInt]

case class VersionId(id: BigInt) extends VersionIdLike

case class VersionLink(id: BigInt, name: String) extends VersionIdLike

object Version {
  sealed abstract class Status(val token: String)
  object Status {
    case object Open extends Status("open")
    case object Locked extends Status("locked")
    case object Closed extends Status("closed")
    case class Unknown(override val token: String) extends Status(token)

    def apply(token: String): Status = {
      Seq(Open, Locked, Closed)
        .find { s => s.token == token }
        .getOrElse(Unknown(token))
    }

    def unapply(status: Status): Option[String] = Option(status).map(_.token)
  }

  sealed abstract class Sharing(val token: String)
  object Sharing {
    case object None extends Sharing("none")
    case object Descendants extends Sharing("descendants")
    case object Hierarchy extends Sharing("hierarchy")
    case object Tree extends Sharing("tree")
    case object System extends Sharing("system")
    case class Unknown(override val token: String) extends Sharing(token)

    def apply(token: String): Sharing = {
      Seq(None, Descendants, Hierarchy, Tree, System)
        .find { s => s.token == token }
        .getOrElse(Unknown(token))
    }

    def unapply(sharing: Sharing): Option[String] = Option(sharing).map(_.token)
  }

  case class New(name: String) extends CustomFieldUpdateSetFSF[New] {
    val status = new FluentSettableField[Status, New](this)
    val sharing = new FluentSettableField[Sharing, New](this)
    val dueDate = new FluentSettableField[LocalDate, New](this)
    val description = new FluentSettableField[String, New](this)
  }

  object Update {
    def apply() = new Update
  }
  class Update extends CustomFieldUpdateSetFSF[Update] {
    val name = new FluentSettableField[String, Update](this)
    val status = new FluentSettableField[Status, Update](this)
    val sharing = new FluentSettableField[Sharing, Update](this)
    val dueDate = new FluentSettableField[Option[LocalDate], Update](this)
    val description = new FluentSettableField[Option[String], Update](this)
  }
}

case class Version(
  id: BigInt,
  project: ProjectLink,
  name: String,
  description: Option[String],
  status: Version.Status,
  sharing: Version.Sharing,
  dueDate: Option[LocalDate],
  createdOn: DateTime,
  updatedOn: DateTime,
  customFields: Option[Set[CustomField]]) extends VersionIdLike with OptionalCustomFieldSet
