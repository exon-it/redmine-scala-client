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

import scala.collection.immutable._

//noinspection AccessorLikeMethodIsEmptyParen
trait IssueManager {
  def getIssues(parameters: (String, String)*): IO[PagedList[Issue]]
  def getIssues(parameters: Seq[(String, String)]): IO[PagedList[Issue]]
  def getIssuesByProjectKey(key: String): IO[PagedList[Issue]]
  def getIssuesByQuery(queryId: SavedQueryIdLike, projectKey: Option[String] = None): IO[PagedList[Issue]]
  def getIssuesBySummary(summaryField: String, projectKey: Option[String] = None): IO[PagedList[Issue]]
  def getIssue(id: IssueIdLike, includes: Issue.Include*): IO[Issue]
  def createIssue(issue: Issue.New): IO[Issue]
  def updateIssue(id: IssueIdLike, update: Issue.Update): IO[Unit]
  def deleteIssue(id: IssueIdLike): IO[Unit]

  def addWatcherToIssue(watcher: UserIdLike, issue: IssueIdLike): IO[Unit]
  def deleteWatcherFromIssue(watcher: UserIdLike, issue: IssueIdLike): IO[Unit]

  def getRelation(id: IssueRelationIdLike): IO[IssueRelation]
  def getRelationsByIssue(issue: IssueIdLike): IO[PagedList[IssueRelation]]
  def createRelation(issueFrom: IssueIdLike, relation: IssueRelation.New): IO[IssueRelation]
  def deleteRelation(id: IssueRelationIdLike): IO[Unit]
  def deleteRelationsOfIssue(issue: IssueIdLike): IO[Unit]

  def getPriorities(): IO[PagedList[Priority]]

  def getCategories(project: ProjectIdLike): IO[PagedList[Category]]
  def createCategory(project: ProjectIdLike, category: Category.New): IO[Category]
  def updateCategory(id: CategoryIdLike, update: Category.Update): IO[Unit]
  def deleteCategory(id: CategoryIdLike, reassignTo: Option[CategoryIdLike] = None): IO[Unit]

  def getStatuses(): IO[PagedList[IssueStatus]]

  def getTrackers(): IO[PagedList[Tracker]]

  def getSavedQueries(): IO[PagedList[SavedQuery]]
}
