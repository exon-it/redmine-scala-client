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
import by.exonit.redmine.client.managers.{RequestManager, WikiManager}
import by.exonit.redmine.client.managers.WebClient.RequestDSL
import monix.eval.Task

class WikiManagerImpl(requestManager: RequestManager) extends WikiManager {

  def getPages(project: ProjectIdLike): Task[PagedList[WikiPage]] = {
    val request = for {
      _ <- requestManager.baseRequest
      _ <- RequestDSL.addSegments("projects", project.id.toString, "wiki", "index.json")
    } yield ()
    requestManager.getEntityPagedList[WikiPage](request, "wiki_pages")
  }

  def getPage(project: ProjectIdLike, page: WikiPageIdLike): Task[WikiPageDetails] = {
    val request = for {
      _ <- requestManager.baseRequest
      _ <- RequestDSL.addSegments("projects", project.id.toString, "wiki", s"${page.id}.json")
    } yield ()
    requestManager.getEntity[WikiPageDetails](request, "wiki_page")
  }

  def createPage(project: ProjectIdLike, page: WikiPage.New): Task[WikiPageDetails] = {
    val request = for {
      _ <- requestManager.baseRequest
      _ <- RequestDSL.addSegments("projects", project.id.toString, "wiki", s"${page.title}.json")
    } yield ()
    requestManager.putEntityWithResponse[WikiPage.New, WikiPageDetails](request, "wiki_page", page, "wiki_page")
  }

  def updatePage(project: ProjectIdLike, id: WikiPageIdLike, update: WikiPage.Update): Task[Unit] = {
    val request = for {
      _ <- requestManager.baseRequest
      _ <- RequestDSL.addSegments("projects", project.id.toString, "wiki", s"${id.id}.json")
    } yield ()
    requestManager.putEntity(request, "wiki_page", update)
  }

  def deletePage(project: ProjectIdLike, id: WikiPageIdLike): Task[Unit] = {
    val request = for {
      _ <- requestManager.baseRequest
      _ <- RequestDSL.addSegments("projects", project.id.toString, "wiki", s"${id.id}.json")
    } yield ()
    requestManager.deleteEntity(request)
  }
}
