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

import scala.collection.immutable._
import org.joda.time.DateTime

trait UserIdLike extends Identifiable[BigInt]

case class UserId(id: BigInt) extends UserIdLike

case class UserLink(id: BigInt, name: String) extends UserIdLike

object User {
  sealed abstract class Include(val token: String)
  object Include {
    case object Memberships extends Include("memberships")
    case object Groups extends Include("groups")
  }

  sealed abstract class Status(val number: Int)
  object Status {
    case object Anonymous extends Status(0)
    case object Active extends Status(1)
    case object Registered extends Status(2)
    case object Locked extends Status(3)
    case class Unknown(override val number: Int) extends Status(number)

    def apply(number: Int): Status = {
      Seq(Anonymous, Active, Registered, Locked)
        .find { s => s.number == number }
        .getOrElse(Unknown(number))
    }

    def unapply(status: Status): Option[Int] = Option(status).map(_.number)
  }

  case class New(login: String, firstName: String, lastName: String, email: String) extends CustomFieldUpdateSetFSF[New] {
    val password = new FluentSettableField[String, New](this)
    val authSourceId = new FluentSettableField[BigInt, New](this)
    val mailNotification = new FluentSettableField[String, New](this)
    val mustChangePassword = new FluentSettableField[Boolean, New](this)
  }

  case class Update() extends CustomFieldUpdateSetFSF[Update] {
    val login = new FluentSettableField[String, Update](this)
    val firstName = new FluentSettableField[String, Update](this)
    val lastName = new FluentSettableField[String, Update](this)
    val email = new FluentSettableField[String, Update](this)
    val password = new FluentSettableField[String, Update](this)
    val authSourceId = new FluentSettableField[BigInt, Update](this)
    val mailNotification = new FluentSettableField[String, Update](this)
    val mustChangePassword = new FluentSettableField[Boolean, Update](this)
  }
}

case class User(
  id: BigInt,
  login: Option[String],
  firstName: String,
  lastName: String,
  email: Option[String],
  createdOn: DateTime,
  lastLoginOn: Option[DateTime],
  status: Option[User.Status],
  apiKey: Option[String],
  authSourceId: Option[BigInt],
  customFields: Option[Set[CustomField]],
  memberships: Option[Set[Membership]],
  groups: Option[Set[GroupLink]])
    extends UserIdLike with OptionalCustomFieldSet
