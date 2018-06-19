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

package by.exonit.redmine.client.managers.impl

import by.exonit.redmine.client._
import by.exonit.redmine.client.managers.WebClient.RequestDSL
import by.exonit.redmine.client.managers.{RequestManager, UserManager}
import cats.data.NonEmptyList
import monix.eval.Task

import scala.collection.immutable._

class UserManagerImpl(requestManager: RequestManager) extends UserManager {

  def getCurrentUser(includes: User.Include*): Task[User] = {
    val request = for {
      _ <- requestManager.baseRequest
      _ <- RequestDSL.addSegments("users", "current.json")
      _ <- RequestBlocks.include(includes)
    } yield ()
    requestManager.getEntity[User](request, "user")
  }

  def getUsers(params: (String, String)*): Task[PagedList[User]] = {
    val request = for {
      _ <- requestManager.baseRequest
      _ <- RequestDSL.addSegments("users.json")
      _ <- RequestDSL.addQueries(params: _*)
    } yield ()
    requestManager.getEntityPagedList[User](request, "users")
  }

  def getUsers(params: Seq[(String, String)], includes: User.Include*): Task[PagedList[User]] = {
    val allParams = if (includes.nonEmpty) {
      params :+ "include" -> includes.map(_.entryName).mkString(",")
    } else {
      params
    }
    getUsers(allParams: _*)
  }

  def getUser(id: UserIdLike, includes: User.Include*): Task[User] = {
    val request = for {
      _ <- requestManager.baseRequest
      _ <- RequestDSL.addSegments("users", s"${id.id}.json")
      _ <- RequestBlocks.include(includes)
    } yield ()
    requestManager.getEntity[User](request, "user")
  }

  def createUser(user: User.New): Task[User] = {
    val request = for {
      _ <- requestManager.baseRequest
      _ <- RequestDSL.addSegments("users.json")
    } yield ()
    requestManager.postEntityWithResponse[User.New, User](request, "user", user, "user")
  }

  def updateUser(id: UserIdLike, update: User.Update): Task[Unit] = {
    val request = for {
      _ <- requestManager.baseRequest
      _ <- RequestDSL.addSegments("users", s"${id.id}.json")
    } yield ()
    requestManager.putEntity(request, "user", update)
  }

  def deleteUser(id: UserIdLike): Task[Unit] = {
    val request = for {
      _ <- requestManager.baseRequest
      _ <- RequestDSL.addSegments("users", s"${id.id}.json")
    } yield ()
    requestManager.deleteEntity(request)
  }

  def getGroups(): Task[PagedList[Group]] = {
    val request = for {
      _ <- requestManager.baseRequest
      _ <- RequestDSL.addSegments("groups.json")
    } yield ()
    requestManager.getEntityPagedList[Group](request, "groups")
  }

  def getGroup(id: GroupIdLike, includes: Group.Include*): Task[Group] = {
    val request = for {
      _ <- requestManager.baseRequest
      _ <- RequestDSL.addSegments("groups", s"${id.id}.json")
      _ <- RequestBlocks.include(includes)
    } yield ()
    requestManager.getEntity[Group](request, "group")
  }

  def createGroup(group: Group.New): Task[Group] = {
    val request = for {
      _ <- requestManager.baseRequest
      _ <- RequestDSL.addSegments("groups.json")
    } yield ()
    requestManager.postEntityWithResponse[Group.New, Group](request, "group", group, "group")
  }

  def updateGroup(id: GroupIdLike, update: Group.Update): Task[Unit] = {
    val request = for {
      _ <- requestManager.baseRequest
      _ <- RequestDSL.addSegments("groups", s"${id.id}.json")
    } yield ()
    requestManager.putEntity(request, "group", update)
  }

  def deleteGroup(id: GroupIdLike): Task[Unit] = {
    val request = for {
      _ <- requestManager.baseRequest
      _ <- RequestDSL.addSegments("groups", s"${id.id}.json")
    } yield ()
    requestManager.deleteEntity(request)
  }

  def addUserToGroup(user: UserIdLike, group: GroupIdLike): Task[Unit] = {
    val request = for {
      _ <- requestManager.baseRequest
      _ <- RequestDSL.addSegments("groups", group.id.toString, "users.json")
    } yield ()
    requestManager.postEntity(request, "user_id", user.id)
  }

  def removeUserFromGroup(user: UserIdLike, group: GroupIdLike): Task[Unit] = {
    val request = for {
      _ <- requestManager.baseRequest
      _ <- RequestDSL.addSegments("groups", group.id.toString, "users", s"${user.id}.json")
    } yield ()
    requestManager.deleteEntity(request)
  }
}
