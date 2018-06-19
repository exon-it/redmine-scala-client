package by.exonit.redmine.client.managers.impl

import by.exonit.redmine.client._
import by.exonit.redmine.client.managers.WebClient.RequestDSL
import by.exonit.redmine.client.managers.{RequestManager, SearchManager}
import monix.eval.Task
import scala.collection.immutable._

class SearchManagerImpl(requestManager: RequestManager) extends SearchManager {
  override def search(
    question: String,
    projectId: Option[ProjectIdLike],
    scope: Option[SearchScope],
    objectTypes: Option[Set[SearchObjectType]],
    allWords: Option[Boolean],
    titlesOnly: Option[Boolean],
    openIssuesOnly: Option[Boolean],
    attachmentScope: Option[SearchAttachmentScope]
  ): Task[PagedList[SearchResult]] = {
    def boolToFlag(bool: Boolean): String = if (bool) "1" else "0"

    val baseRequest = projectId match {
      case Some(id) =>
        for {
          _ <- requestManager.baseRequest
          _ <- RequestDSL.addSegments("projects", id.toString)
        } yield ()
      case None => requestManager.baseRequest
    }
    val additionalQueries = Seq(
      scope.map(ss => Seq("scope" -> ss.entryName)),
      objectTypes.map(_.to[Seq].map(sot => sot.token -> "1")),
      allWords.map(aw => Seq("all_words" -> boolToFlag(aw))),
      titlesOnly.map(to => Seq("titles_only" -> boolToFlag(to))),
      openIssuesOnly.map(oio => Seq("open_issues" -> boolToFlag(oio))),
      attachmentScope.map(a => Seq("attachments" -> a.entryName))
    ).flatten.flatten
    val request = for {
      _ <- baseRequest
      _ <- RequestDSL.addSegments("search.json")
      _ <- RequestDSL.addQueries("q" -> question)
      _ <- RequestDSL.addQueries(additionalQueries: _*)
    } yield ()
    requestManager.getEntityPagedList[SearchResult](request, "results")
  }
}
