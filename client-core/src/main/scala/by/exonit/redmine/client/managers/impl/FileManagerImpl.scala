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

import by.exonit.redmine.client.managers.WebClient.RequestDSL
import by.exonit.redmine.client.{PagedList, ProjectFile, ProjectIdLike}
import by.exonit.redmine.client.managers.{FileManager, RequestManager}
import monix.eval.Task

class FileManagerImpl(requestManager: RequestManager) extends FileManager {
  override def getFiles(projectId: ProjectIdLike): Task[PagedList[ProjectFile]] = {
    val request = for {
      _ <- requestManager.baseRequest
      _ <- RequestDSL.addSegments("projects", projectId.id.toString, "files.json")
    } yield ()
    requestManager.getEntityPagedList[ProjectFile](request, "files")
  }

  override def createFile(
    projectId: ProjectIdLike,
    file: ProjectFile.New
  ): Task[Unit] = {
    val request = for {
      _ <- requestManager.baseRequest
      _ <- RequestDSL.addSegments("projects", projectId.id.toString, "files.json")
    } yield ()
    requestManager.postEntity(request, "file", file)
  }
}
