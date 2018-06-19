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

import scala.collection.immutable._

trait MembershipIdLike extends Identifiable[BigInt]

case class MembershipId(id: BigInt) extends MembershipIdLike

trait MembershipLike extends MembershipIdLike {
  def project: ProjectLink
  def roles: Set[RoleLink]
}

case class IdentityMembership(
  id: BigInt,
  project: ProjectLink,
  roles: Set[RoleLink]
) extends MembershipLike

case class Membership(
  id: BigInt,
  project: ProjectLink,
  user: Either[UserLink, GroupLink],
  roles: Set[RoleLink]
) extends MembershipLike

object Membership {

  case class New(
    user: Either[UserIdLike, GroupIdLike],
    roles: Seq[RoleIdLike]
  )

  case class Update(
    roles: Seq[RoleIdLike]
  )

}

