package by.exonit.redmine.client.managers

import by.exonit.redmine.client.{DocumentCategory, PagedList}
import monix.eval.Task

/** Manager for "Documents" module
  */
trait DocumentsManager {
  /** Returns document categories list
    */
  def getDocumentCategories(): Task[PagedList[DocumentCategory]]
}
