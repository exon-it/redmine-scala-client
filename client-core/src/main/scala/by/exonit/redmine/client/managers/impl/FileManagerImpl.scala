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
