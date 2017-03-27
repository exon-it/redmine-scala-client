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

package by.exonit.redmine.client.managers.impl

import by.exonit.redmine.client._
import by.exonit.redmine.client.managers.{MembershipManager, RequestManager}
import by.exonit.redmine.client.managers.WebClient.RequestDSL
import monix.eval.Task

import scala.collection.immutable._

class MembershipManagerImpl(requestManager: RequestManager) extends MembershipManager {

  def getMemberships(project: ProjectIdLike): Task[PagedList[Membership]] = {
    val request = for {
      _ <- requestManager.baseRequest
      _ <- RequestDSL.addSegments("projects", project.id.toString, "memberships.json")
    } yield ()
    requestManager.getEntityPagedList[Membership](request, "memberships")
  }

  def getMemberships(projectKey: String): Task[PagedList[Membership]] = {
    val request = for {
      _ <- requestManager.baseRequest
      _ <- RequestDSL.addSegments("projects", projectKey, "memberships.json")
    } yield ()
    requestManager.getEntityPagedList[Membership](request, "memberships")
  }

  def createMembership(project: ProjectIdLike, membership: Membership.New): Task[Membership] = {
    val request = for {
      _ <- requestManager.baseRequest
      _ <- RequestDSL.addSegments("projects", project.id.toString, "memberships.json")
    } yield ()
    requestManager.postEntityWithResponse[Membership.New, Membership](request, "membership", membership, "membership")
  }

  def updateMembership(id: MembershipIdLike, update: Membership.Update): Task[Unit] = {
    val request = for {
      _ <- requestManager.baseRequest
      _ <- RequestDSL.addSegments("memberships", s"${id.id}.json")
    } yield ()
    requestManager.putEntity(request, "membership", update)
  }

  def deleteMembership(id: MembershipIdLike): Task[Unit] = {
    val request = for {
      _ <- requestManager.baseRequest
      _ <- RequestDSL.addSegments("memberships", s"${id.id}.json")
    } yield ()
    requestManager.deleteEntity(request)
  }

  def createMembershipForUser(project: ProjectIdLike, user: UserIdLike, roles: RoleIdLike*): Task[Membership] = {
    createMembership(project, Membership.New(Left(user), Seq(roles: _*)))
  }

  def createMembershipForGroup(project: ProjectIdLike, group: GroupIdLike, roles: RoleIdLike*): Task[Membership] = {
    createMembership(project, Membership.New(Right(group), Seq(roles: _*)))
  }
}
