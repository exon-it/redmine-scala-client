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
import by.exonit.redmine.client.managers.{IssueManager, RequestManager}
import cats.data.NonEmptyList
import cats.effect.{IO, Timer}
import cats.syntax.all._

import scala.collection.immutable._

class IssueManagerImpl(requestManager: RequestManager)(implicit timer: Timer[IO]) extends IssueManager {

  def getIssuesByProjectKey(projectKey: String): IO[PagedList[Issue]] = IO.suspend {
    getIssues("project_id" -> projectKey)
  }

  def getIssues(params: (String, String)*): IO[PagedList[Issue]] = IO.suspend {
    val request = for {
      _ <- requestManager.baseRequest
      _ <- RequestDSL.addSegments("issues.json")
      _ <- RequestDSL.addQueries(params: _*)
    } yield ()
    requestManager.getEntityPagedList[Issue](request, "issues")
  }

  def getIssuesByQuery(queryId: SavedQueryIdLike, projectKey: Option[String]): IO[PagedList[Issue]] = IO.suspend {
    val params = Seq(Some("query_id" -> queryId.id.toString), projectKey.map {key => "project_id" -> key}).flatten
    getIssues(params: _*)
  }

  def getIssuesBySummary(summaryField: String, projectKey: Option[String]): IO[PagedList[Issue]] = IO.suspend {
    val params = Seq(Some("subject" -> summaryField), projectKey.map {key => "project_id" -> key}).flatten
    getIssues(params: _*)
  }

  def getIssues(params: Seq[(String, String)]): IO[PagedList[Issue]] = getIssues(params: _*)

  def createIssue(issue: Issue.New): IO[Issue] = IO.suspend {
    val request = for {
      _ <- requestManager.baseRequest
      _ <- RequestDSL.addSegments("issues.json")
    } yield ()
    requestManager.postEntityWithResponse[Issue.New, Issue](request, "issue", issue, "issue")
  }

  def updateIssue(id: IssueIdLike, update: Issue.Update): IO[Unit] = IO.suspend {
    val request = for {
      _ <- requestManager.baseRequest
      _ <- RequestDSL.addSegments("issues", s"${id.id}.json")
    } yield ()
    requestManager.putEntity(request, "issue", update)
  }

  def deleteIssue(id: IssueIdLike): IO[Unit] = IO.suspend {
    val request = for {
      _ <- requestManager.baseRequest
      _ <- RequestDSL.addSegments("issues", s"${id.id}.json")
    } yield ()
    requestManager.deleteEntity(request)
  }

  def getCategories(project: ProjectIdLike): IO[PagedList[Category]] = IO.suspend {
    val request = for {
      _ <- requestManager.baseRequest
      _ <- RequestDSL.addSegments("projects", project.id.toString, "issue_categories.json")
    } yield ()
    requestManager.getEntityPagedList[Category](request, "issue_categories")
  }

  def createCategory(project: ProjectIdLike, category: Category.New): IO[Category] = IO.suspend {
    val request = for {
      _ <- requestManager.baseRequest
      _ <- RequestDSL.addSegments("projects", project.id.toString, "issue_categories.json")
    } yield ()
    requestManager.postEntityWithResponse[Category.New, Category](request, "issue_category", category, "issue_category")
  }

  def updateCategory(id: CategoryIdLike, update: Category.Update): IO[Unit] = IO.suspend {
    val request = for {
      _ <- requestManager.baseRequest
      _ <- RequestDSL.addSegments("issue_categories", s"${id.id}.json")
    } yield ()
    requestManager.putEntity(request, "issue_category", update)
  }

  def deleteCategory(id: CategoryIdLike, reassignTo: Option[CategoryIdLike] = None): IO[Unit] = IO.suspend {
    val request = for {
      _ <- requestManager.baseRequest
      _ <- RequestDSL.addSegments("issue_categories",s"${id.id}.json")
    } yield ()
    val reassignRequest = reassignTo.fold(request) {reassignToId =>
      for {
        _ <- request
        _ <- RequestDSL.addQueries("reassign_to" -> reassignToId.id.toString())
      } yield ()
    }
    requestManager.deleteEntity(reassignRequest)
  }

  override def getRelation(id: IssueRelationIdLike): IO[IssueRelation] = IO.suspend {
    val request = for {
      _ <- requestManager.baseRequest
      _ <- RequestDSL.addSegments("relations", s"${id.id}.json")
    } yield ()
    requestManager.getEntity[IssueRelation](request, "relation")
  }

  override def getRelationsByIssue(issue: IssueIdLike): IO[PagedList[IssueRelation]] = IO.suspend {
    val request = for {
      _ <- requestManager.baseRequest
      _ <- RequestDSL.addSegments("issues", issue.id.toString, "relations.json")
    } yield ()
    requestManager.getEntityPagedList[IssueRelation](request, "relations")
  }

  def createRelation(issueFrom: IssueIdLike, relation: IssueRelation.New): IO[IssueRelation] = IO.suspend {
    val request = for {
      _ <- requestManager.baseRequest
      _ <- RequestDSL.addSegments("issues", issueFrom.id.toString, "relations.json")
    } yield ()
    requestManager.postEntityWithResponse[IssueRelation.New, IssueRelation](request, "relation", relation, "relation")
  }

  def deleteRelationsOfIssue(issue: IssueIdLike): IO[Unit] = IO.suspend {
    getIssue(issue, Issue.Include.Relations).flatMap { i =>
      val relationsList = i.relations.map(_.to[List])
      relationsList.flatMap(NonEmptyList.fromList) match {
        case Some(l) =>
          l.parTraverse(deleteRelation(_)).map(_ => ())
        case None => IO.unit
      }
    }
  }

  def getIssue(id: IssueIdLike, includes: Issue.Include*): IO[Issue] = IO.suspend {
    val request = for {
      _ <- requestManager.baseRequest
      _ <- RequestDSL.addSegments("issues", s"${id.id}.json")
      _ <- RequestBlocks.include(includes)
    } yield ()
    requestManager.getEntity[Issue](request, "issue")
  }

  def deleteRelation(id: IssueRelationIdLike): IO[Unit] = IO.suspend {
    val request = for {
      _ <- requestManager.baseRequest
      _ <- RequestDSL.addSegments("relations", s"${id.id}.json")
    } yield ()
    requestManager.deleteEntity(request)
  }


  def addWatcherToIssue(watcher: UserIdLike, issue: IssueIdLike): IO[Unit] = IO.suspend {
    val request = for {
      _ <- requestManager.baseRequest
      _ <- RequestDSL.addSegments("issues", issue.id.toString, "watchers.json")
    } yield ()
    requestManager.postEntity(request, "user_id", watcher.id)
  }

  def deleteWatcherFromIssue(watcher: UserIdLike, issue: IssueIdLike): IO[Unit] = IO.suspend {
    val request = for {
      _ <- requestManager.baseRequest
      _ <- RequestDSL.addSegments("issues", issue.id.toString, "watchers", s"${watcher.id}.json")
    } yield ()
    requestManager.deleteEntity(request)
  }

  def getPriorities(): IO[PagedList[Priority]] = IO.suspend {
    val request = for {
      _ <- requestManager.baseRequest
      _ <- RequestDSL.addSegments("enumerations", "issue_priorities.json")
    } yield ()
    requestManager.getEntityPagedList[Priority](request, "issue_priorities")
  }

  def getSavedQueries(): IO[PagedList[SavedQuery]] = IO.suspend {
    val request = for {
      _ <- requestManager.baseRequest
      _ <- RequestDSL.addSegments("queries.json")
    } yield ()
    requestManager.getEntityPagedList[SavedQuery](request, "queries")
  }

  def getStatuses(): IO[PagedList[IssueStatus]] = IO.suspend {
    val request = for {
      _ <- requestManager.baseRequest
      _ <- RequestDSL.addSegments("issue_statuses.json")
    } yield ()
    requestManager.getEntityPagedList[IssueStatus](request, "issue_statuses")
  }

  def getTrackers(): IO[PagedList[Tracker]] = IO.suspend {
    val request = for {
      _ <- requestManager.baseRequest
      _ <- RequestDSL.addSegments("trackers.json")
    } yield ()
    requestManager.getEntityPagedList[Tracker](request, "trackers")
  }
}
