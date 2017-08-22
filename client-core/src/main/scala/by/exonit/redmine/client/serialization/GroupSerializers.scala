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

object GroupSerializers {
  lazy val all: Seq[Serializer[_]] = Seq(
    groupIdSerializer,
    groupLinkSerializer,
    groupSerializer,
    newGroupSerializer,
    groupUpdateSerializer
  )

  def serializeGroupId: PartialFunction[Any, JValue] = {
    case GroupId(id) => JInt(id)
  }

  def deserializeGroupId: PartialFunction[JValue, GroupId] = {
    case JInt(id) => GroupId(id)
  }

  def deserializeGroupLink(implicit formats: Formats): PartialFunction[JValue, GroupLink] = {
    case j: JObject =>
      GroupLink(
        (j \ "id").extract[BigInt], (j \ "name").extract[String])
  }

  def deserializeGroup(implicit formats: Formats): PartialFunction[JValue, Group] = {
    case j: JObject =>
      Group(
        (j \ "id").extract[BigInt],
        (j \ "name").extract[String],
        (j \ "users").extractOpt[Set[UserLink]],
        (j \ "memberships").toOption.map(_.extract[Set[IdentityMembership]]),
        (j \ "custom_fields").toOption.map(_.extract[Set[CustomField]]))
  }

  def serializeNewGroup(implicit formats: Formats): PartialFunction[Any, JValue] = {
    case g: Group.New =>
      ("name" -> g.name) ~
        ("user_ids" -> g.users.map(Extraction.decompose)) ~
        ("custom_fields" -> g.customFields.map(_.map(Extraction.decompose)))
  }

  def serializeGroupUpdate(implicit formats: Formats): PartialFunction[Any, JValue] = {
    case g: Group.Update =>
      ("name" -> g.name) ~
        ("custom_fields" -> g.customFields.map(_.map(Extraction.decompose)))
  }

  object groupIdSerializer extends CustomSerializer[GroupId](
    _ => deserializeGroupId -> serializeGroupId)

  object groupLinkSerializer extends CustomSerializer[GroupLink](
    formats => deserializeGroupLink(formats) -> PartialFunction.empty)

  object groupSerializer extends CustomSerializer[Group](
    formats => deserializeGroup(formats) -> PartialFunction.empty)

  object newGroupSerializer extends CustomSerializer[Group.New](
    formats => PartialFunction.empty -> serializeNewGroup(formats))

  object groupUpdateSerializer extends CustomSerializer[Group.Update](
    formats => PartialFunction.empty -> serializeGroupUpdate(formats))

}
