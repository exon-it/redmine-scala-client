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

object MembershipSerializers {
  lazy val all: Seq[Serializer[_]] = Seq(
    membershipIdSerializer,
    membershipSerializer,
    newMembershipSerializer,
    membershipUpdateSerializer)

  def serializeMembershipId(implicit formats: Formats): PartialFunction[Any, JValue] = {
    case MembershipId(id) => JInt(id)
  }

  def deserializeMembershipId(implicit formats: Formats): PartialFunction[JValue, MembershipId] = {
    case JInt(id) => MembershipId(id)
  }

  object membershipIdSerializer extends CustomSerializer[MembershipId](
    formats => deserializeMembershipId(formats) -> serializeMembershipId(formats))

  def deserializeMembership(implicit formats: Formats): PartialFunction[JValue, Membership] = {
    case j: JObject =>
      val user = (j \ "user").extractOpt[UserLink]
      val group = (j \ "group").extractOpt[GroupLink]
      val identity = user.toLeft(group.get)
      Membership(
        (j \ "id").extract[BigInt],
        (j \ "project").extract[ProjectLink],
        identity,
        (j \ "roles").extract[Set[RoleLink]])
  }

  object membershipSerializer extends CustomSerializer[Membership](
    formats => deserializeMembership(formats) -> PartialFunction.empty)

  def serializeNewMembership(implicit formats: Formats): PartialFunction[Any, JValue] = {
    case Membership.New(user, roles) =>
      val identity = user.fold(u => "user_id" -> u.id, g => "user_id" -> g.id)
      identity ~ ("role_ids" -> roles.map(Extraction.decompose(_)))
  }

  object newMembershipSerializer extends CustomSerializer[Membership.New](
    formats => PartialFunction.empty -> serializeNewMembership(formats))

  def serializeMembershipUpdate(implicit formats: Formats): PartialFunction[Any, JValue] = {
    case u: Membership.Update => Extraction.decompose("role_ids" -> u.roles.map(_.id))
  }

  object membershipUpdateSerializer extends CustomSerializer[Membership.Update](
    formats => PartialFunction.empty -> serializeMembershipUpdate(formats))

}
