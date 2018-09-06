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

import scala.collection.immutable._
import cats.effect.IO

//noinspection AccessorLikeMethodIsEmptyParen
trait UserManager {
  def getCurrentUser(includes: User.Include*): IO[User]

  def getUsers(params: (String, String)*): IO[PagedList[User]]

  def getUsers(params: Seq[(String, String)], includes: User.Include*): IO[PagedList[User]]

  def getUser(id: UserIdLike, includes: User.Include*): IO[User]

  def createUser(user: User.New): IO[User]

  def updateUser(id: UserIdLike, update: User.Update): IO[Unit]

  def deleteUser(id: UserIdLike): IO[Unit]

  def getGroups(): IO[PagedList[Group]]

  def getGroup(id: GroupIdLike, includes: Group.Include*): IO[Group]

  def createGroup(group: Group.New): IO[Group]

  def updateGroup(id: GroupIdLike, update: Group.Update): IO[Unit]

  def deleteGroup(id: GroupIdLike): IO[Unit]

  def addUserToGroup(user: UserIdLike, group: GroupIdLike): IO[Unit]

  def removeUserFromGroup(user: UserIdLike, group: GroupIdLike): IO[Unit]
}
