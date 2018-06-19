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
import monix.eval.Task

import scala.collection.immutable._

//noinspection AccessorLikeMethodIsEmptyParen
trait IssueManager {
  def getIssues(parameters: (String, String)*): Task[PagedList[Issue]]
  def getIssues(parameters: Seq[(String, String)]): Task[PagedList[Issue]]
  def getIssuesByProjectKey(key: String): Task[PagedList[Issue]]
  def getIssuesByQuery(queryId: SavedQueryIdLike, projectKey: Option[String] = None): Task[PagedList[Issue]]
  def getIssuesBySummary(summaryField: String, projectKey: Option[String] = None): Task[PagedList[Issue]]
  def getIssue(id: IssueIdLike, includes: Issue.Include*): Task[Issue]
  def createIssue(issue: Issue.New): Task[Issue]
  def updateIssue(id: IssueIdLike, update: Issue.Update): Task[Unit]
  def deleteIssue(id: IssueIdLike): Task[Unit]

  def addWatcherToIssue(watcher: UserIdLike, issue: IssueIdLike): Task[Unit]
  def deleteWatcherFromIssue(watcher: UserIdLike, issue: IssueIdLike): Task[Unit]

  def getRelation(id: IssueRelationIdLike): Task[IssueRelation]
  def getRelationsByIssue(issue: IssueIdLike): Task[PagedList[IssueRelation]]
  def createRelation(issueFrom: IssueIdLike, relation: IssueRelation.New): Task[IssueRelation]
  def deleteRelation(id: IssueRelationIdLike): Task[Unit]
  def deleteRelationsOfIssue(issue: IssueIdLike): Task[Unit]

  def getPriorities(): Task[PagedList[Priority]]

  def getCategories(project: ProjectIdLike): Task[PagedList[Category]]
  def createCategory(project: ProjectIdLike, category: Category.New): Task[Category]
  def updateCategory(id: CategoryIdLike, update: Category.Update): Task[Unit]
  def deleteCategory(id: CategoryIdLike, reassignTo: Option[CategoryIdLike] = None): Task[Unit]

  def getStatuses(): Task[PagedList[IssueStatus]]

  def getTrackers(): Task[PagedList[Tracker]]

  def getSavedQueries(): Task[PagedList[SavedQuery]]
}
