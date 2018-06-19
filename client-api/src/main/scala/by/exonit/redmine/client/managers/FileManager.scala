package by.exonit.redmine.client.managers

import by.exonit.redmine.client.{PagedList, ProjectFile, ProjectIdLike}
import monix.eval.Task

trait FileManager {
  def getFiles(projectId: ProjectIdLike): Task[PagedList[ProjectFile]]
  def createFile(projectId: ProjectIdLike, file: ProjectFile.New): Task[Unit]
}
