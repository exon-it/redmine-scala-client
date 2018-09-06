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
import by.exonit.redmine.client.managers.{RequestManager, SearchManager}
import cats.effect.IO
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
  ): IO[PagedList[SearchResult]] = IO.suspend {
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
