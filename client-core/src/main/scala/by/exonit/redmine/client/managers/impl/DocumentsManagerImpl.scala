package by.exonit.redmine.client.managers.impl

import by.exonit.redmine.client.managers.WebClient.RequestDSL
import by.exonit.redmine.client.managers.{DocumentsManager, RequestManager}
import by.exonit.redmine.client.{DocumentCategory, PagedList}
import monix.eval.Task

class DocumentsManagerImpl(requestManager: RequestManager) extends DocumentsManager {
  def getDocumentCategories(): Task[PagedList[DocumentCategory]] = {
    val request = for {
      _ <- requestManager.baseRequest
      _ <- RequestDSL.addSegments("enumerations", "document_categories.json")
    } yield ()
    requestManager.getEntityPagedList[DocumentCategory](request, "document_categories")
  }
}
