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
import monix.eval.Task

//noinspection AccessorLikeMethodIsEmptyParen
trait UserManager {
  def getCurrentUser(includes: User.Include*): Task[User]

  def getUsers(params: (String, String)*): Task[PagedList[User]]

  def getUsers(params: Seq[(String, String)], includes: User.Include*): Task[PagedList[User]]

  def getUser(id: UserIdLike, includes: User.Include*): Task[User]

  def createUser(user: User.New): Task[User]

  def updateUser(id: UserIdLike, update: User.Update): Task[Unit]

  def deleteUser(id: UserIdLike): Task[Unit]

  def getGroups(): Task[PagedList[Group]]

  def getGroup(id: GroupIdLike, includes: Group.Include*): Task[Group]

  def createGroup(group: Group.New): Task[Group]

  def updateGroup(id: GroupIdLike, update: Group.Update): Task[Unit]

  def deleteGroup(id: GroupIdLike): Task[Unit]

  def addUserToGroup(user: UserIdLike, group: GroupIdLike): Task[Unit]

  def removeUserFromGroup(user: UserIdLike, group: GroupIdLike): Task[Unit]
}
