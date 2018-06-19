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

import scala.collection.immutable._

object RoleSerializers {
  lazy val all: Seq[Serializer[_]] = Seq(
    roleIdSerializer, roleLinkSerializer, roleSerializer)

  def deserializeRoleId: PartialFunction[JValue, RoleId] = {
    case JInt(id) => RoleId(id)
  }

  def serializeRoleId: PartialFunction[Any, JValue] = {
    case RoleId(id) => JInt(id)
  }

  object roleIdSerializer extends CustomSerializer[RoleId](
    _ => deserializeRoleId -> serializeRoleId)

  def deserializeRoleLink(implicit formats: Formats): PartialFunction[JValue, RoleLink] = {
    case j: JObject =>
      RoleLink(
        (j \ "id").extract[BigInt],
        (j \ "name").extract[String],
        (j \ "inherited").extractOpt[Boolean])
  }

  object roleLinkSerializer extends CustomSerializer[RoleLink](
    formats => deserializeRoleLink(formats) -> PartialFunction.empty)

  def deserializeRole(implicit formats: Formats): PartialFunction[JValue, Role] = {
    case j: JObject =>
      Role(
        (j \ "id").extract[BigInt],
        (j \ "name").extract[String],
        (j \ "permissions").extract[Set[String]])
  }

  object roleSerializer extends CustomSerializer[Role](
    formats => deserializeRole(formats) -> PartialFunction.empty)

}
