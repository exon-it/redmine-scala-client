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

package by.exonit.redmine.client.serialization

import by.exonit.redmine.client._
import org.json4s._
import org.json4s.JsonDSL._

import scala.collection.immutable.Seq

object UserSerializers {
  lazy val all: Seq[Serializer[_]] = Seq(
    userIdSerializer, userLinkSerializer, userSerializer, newUserSerializer, userUpdateSerializer)

  def deserializeUserId: PartialFunction[JValue, UserId] = {
    case JInt(id) => UserId(id)
  }

  def serializeUserId: PartialFunction[Any, JValue] = {
    case UserId(id) => JInt(id)
  }

  object userIdSerializer extends CustomSerializer[UserId](
    _ => deserializeUserId -> serializeUserId)

  def deserializeUserLink(implicit formats: Formats): PartialFunction[JValue, UserLink] = {
    case j: JObject =>
      UserLink(
        (j \ "id").extract[BigInt],
        (j \ "name").extract[String])
  }

  object userLinkSerializer extends CustomSerializer[UserLink](
    formats => deserializeUserLink(formats) -> PartialFunction.empty)

  def deserializeUserStatus: PartialFunction[JValue, User.Status] = {
    case JInt(s) => User.Status(s.toInt)
  }

  def serializeUserStatus: PartialFunction[Any, JValue] = {
    case User.Status(id) => JInt(id)
  }

  object userStatusSerializer extends CustomSerializer[User.Status](
    _ => deserializeUserStatus -> serializeUserStatus)

  def deserializeUser(implicit formats: Formats): PartialFunction[JValue, User] = {
    case j: JObject =>
      User(
        (j \ "id").extract[BigInt],
        (j \ "login").extractOpt[String],
        (j \ "firstname").extract[String],
        (j \ "lastname").extract[String],
        (j \ "mail").extractOpt[String],
        RedmineDateParser.parse((j \ "created_on").extract[String]),
        (j \ "last_login_on").extractOpt[String].map(RedmineDateParser.parse),
        (j \ "status").extractOpt[User.Status],
        (j \ "api_key").extractOpt[String],
        (j \ "auth_source_id").extractOpt[BigInt],
        (j \ "custom_fields").toOption.map(_.extract[Set[CustomField]]),
        (j \ "memberships").toOption.map(_.extract[Set[Membership]]),
        (j \ "groups").toOption.map(_.extract[Set[GroupLink]])
      )
  }

  object userSerializer extends CustomSerializer[User](
    formats => deserializeUser(formats) -> PartialFunction.empty)

  def serializeNewUser(implicit formats: Formats): PartialFunction[Any, JValue] = {
    case u: User.New =>
      ("login" -> u.login) ~
        ("firstname" -> u.firstName) ~
        ("lastname" -> u.lastName) ~
        ("mail" -> u.email) ~
        ("password" -> u.password) ~
        ("auth_source_id" -> u.authSourceId) ~
        ("mail_notification" -> u.mailNotification) ~
        ("must_change_passwd" -> u.mustChangePassword) ~
        ("generate_password" -> u.generatePassword) ~
        ("custom_fields" -> u.customFields.map(_.map(Extraction.decompose)))
  }

  object newUserSerializer extends CustomSerializer[User.New](
    formats => PartialFunction.empty -> serializeNewUser(formats))

  def serializeUserUpdate(implicit formats: Formats): PartialFunction[Any, JValue] = {
    case u: User.Update =>
      ("login" -> u.login) ~
        ("firstname" -> u.firstName) ~
        ("lastname" -> u.lastName) ~
        ("mail" -> u.email) ~
        ("password" -> u.password) ~
        ("auth_source_id" -> u.authSourceId) ~
        ("mail_notification" -> u.mailNotification) ~
        ("must_change_passwd" -> u.mustChangePassword) ~
        ("generate_password" -> u.generatePassword) ~
        ("custom_fields" -> u.customFields.map(_.map(Extraction.decompose)))
  }

  object userUpdateSerializer extends CustomSerializer[User.Update](
    formats => PartialFunction.empty -> serializeUserUpdate(formats))

}
