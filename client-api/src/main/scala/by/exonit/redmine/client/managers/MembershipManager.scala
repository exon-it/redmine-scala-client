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

package by.exonit.redmine.client.managers

import by.exonit.redmine.client._
import monix.eval.Task

import scala.collection.immutable._

trait MembershipManager {
  def getMemberships(project: ProjectIdLike): Task[PagedList[Membership]]

  def getMemberships(projectKey: String): Task[PagedList[Membership]]

  def createMembership(project: ProjectIdLike, membership: Membership.New): Task[Membership]

  def updateMembership(id: MembershipIdLike, update: Membership.Update): Task[Unit]

  def deleteMembership(id: MembershipIdLike): Task[Unit]

  def createMembershipForUser(project: ProjectIdLike, user: UserIdLike, roles: RoleIdLike*): Task[Membership]

  def createMembershipForGroup(project: ProjectIdLike, group: GroupIdLike, roles: RoleIdLike*): Task[Membership]
}
