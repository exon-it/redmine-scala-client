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

import scala.collection.immutable._

object GroupSerializers {
  lazy val all: Seq[Serializer[_]] = Seq(
    groupIdSerializer, groupLinkSerializer, groupSerializer)

  def serializeGroupId(implicit formats: Formats): PartialFunction[Any, JValue] = {
    case GroupId(id) => JInt(id)
  }

  def deserializeGroupId(implicit formats: Formats): PartialFunction[JValue, GroupId] = {
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

  object groupIdSerializer extends CustomSerializer[GroupId](
    formats => deserializeGroupId(formats) -> serializeGroupId(formats))

  object groupLinkSerializer extends CustomSerializer[GroupLink](
    formats => deserializeGroupLink(formats) -> PartialFunction.empty)

  object groupSerializer extends CustomSerializer[Group](
    formats => deserializeGroup(formats) -> PartialFunction.empty)

}
