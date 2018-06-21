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

package by.exonit.redmine.client.managers

import by.exonit.redmine.client._
import cats.effect.IO

trait MembershipManager {
  def getMemberships(project: ProjectIdLike): IO[PagedList[Membership]]

  def getMemberships(projectKey: String): IO[PagedList[Membership]]

  def createMembership(project: ProjectIdLike, membership: Membership.New): IO[Membership]

  def updateMembership(id: MembershipIdLike, update: Membership.Update): IO[Unit]

  def deleteMembership(id: MembershipIdLike): IO[Unit]

  def createMembershipForUser(project: ProjectIdLike, user: UserIdLike, roles: RoleIdLike*): IO[Membership]

  def createMembershipForGroup(project: ProjectIdLike, group: GroupIdLike, roles: RoleIdLike*): IO[Membership]
}
