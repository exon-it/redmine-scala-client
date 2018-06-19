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
import by.exonit.redmine.client.managers.WebClient.RequestDSL
import by.exonit.redmine.client.managers.{ProjectManager, RequestManager}
import monix.eval.Task

class ProjectManagerImpl(requestManager: RequestManager) extends ProjectManager {

  override def getProjects(params: (String, String)*): Task[PagedList[Project]] = {
    val request = for {
      _ <- requestManager.baseRequest
      _ <- RequestDSL.addSegments("projects.json")
      _ <- RequestDSL.addQueries(params: _*)
    } yield ()
    requestManager.getEntityPagedList[Project](request, "projects")
  }

  override def getProject(id: ProjectIdLike, includes: Project.Include*): Task[Project] = {
    val request = for {
      _ <- requestManager.baseRequest
      _ <- RequestDSL.addSegments("projects", s"${id.id}.json")
      _ <- RequestBlocks.include(includes)
    } yield ()
    requestManager.getEntity[Project](request, "project")
  }

  override def getProjectByKey(key: String, includes: Project.Include*): Task[Project] = {
    val request = for {
      _ <- requestManager.baseRequest
      _ <- RequestDSL.addSegments("projects", s"$key.json")
      _ <- RequestBlocks.include(includes)
    } yield ()
    requestManager.getEntity[Project](request, "project")
  }

  override def createProject(project: Project.New): Task[Project] = {
    val request = for {
      _ <- requestManager.baseRequest
      _ <- RequestDSL.addSegments("projects")
    } yield ()
    requestManager.postEntityWithResponse[Project.New, Project](request, "project", project, "project")
  }

  override def updateProject(id: ProjectIdLike, update: Project.Update): Task[Unit] = {
    val request = for {
      _ <- requestManager.baseRequest
      _ <- RequestDSL.addSegments("projects", s"${id.id}.json")
    } yield ()
    requestManager.putEntity(request, "project", update)
  }

  override def deleteProject(id: ProjectIdLike): Task[Unit] = {
    val request = for {
      _ <- requestManager.baseRequest
      _ <- RequestDSL.addSegments("projects", s"${id.id}.json")
    } yield ()
    requestManager.deleteEntity(request)
  }

  override def getVersions(project: ProjectIdLike, params: (String, String)*): Task[PagedList[Version]] = {
    val request = for {
      _ <- requestManager.baseRequest
      _ <- RequestDSL.addSegments("projects", project.id.toString, "versions.json")
      _ <- RequestDSL.addQueries(params: _*)
    } yield ()
    requestManager.getEntityPagedList[Version](request, "versions")
  }

  override def getVersion(id: VersionIdLike): Task[Version] = {
    val request = for {
      _ <- requestManager.baseRequest
      _ <- RequestDSL.addSegments("versions", s"${id.id}.json")
    } yield ()
    requestManager.getEntity[Version](request, "version")
  }

  override def createVersion(project: ProjectIdLike, version: Version.New): Task[Version] = {
    val request = for {
      _ <- requestManager.baseRequest
      _ <- RequestDSL.addSegments("projects", project.id.toString, "versions.json")
    } yield ()
    requestManager.postEntityWithResponse[Version.New, Version](request, "version", version, "version")
  }

  override def updateVersion(id: VersionIdLike, update: Version.Update): Task[Unit] = {
    val request = for {
      _ <- requestManager.baseRequest
      _ <- RequestDSL.addSegments("versions", s"${id.id}.json")
    } yield ()
    requestManager.putEntity(request, "version", update)
  }

  override def deleteVersion(id: VersionIdLike): Task[Unit] = {
    val request = for {
      _ <- requestManager.baseRequest
      _ <- RequestDSL.addSegments("versions", s"${id.id}.json")
    } yield ()
    requestManager.deleteEntity(request)
  }

  override def getNews(project: ProjectIdLike): Task[PagedList[News]] = {
    val request = for {
      _ <- requestManager.baseRequest
      _ <- RequestDSL.addSegments("projects", project.id.toString, "news.json")
    } yield ()
    requestManager.getEntityPagedList[News](request, "news")
  }

  /** Get news across all projects
    */
  override def getAllNews(): Task[PagedList[News]] = {
    val request = for {
      _ <- requestManager.baseRequest
      _ <- RequestDSL.addSegments("news.json")
    } yield ()
    requestManager.getEntityPagedList[News](request, "news")
  }
}

