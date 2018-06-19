package by.exonit.redmine.client.managers

import by.exonit.redmine.client._
import monix.eval.Task

import scala.collection.immutable._

trait SearchManager {
  def search(
    question: String,
    projectId: Option[ProjectIdLike] = None,
    scope: Option[SearchScope] = None,
    objectTypes: Option[Set[SearchObjectType]] = None,
    allWords: Option[Boolean] = None,
    titlesOnly: Option[Boolean] = None,
    openIssuesOnly: Option[Boolean] = None,
    attachmentScope: Option[SearchAttachmentScope] = None
  ): Task[PagedList[SearchResult]]
}
