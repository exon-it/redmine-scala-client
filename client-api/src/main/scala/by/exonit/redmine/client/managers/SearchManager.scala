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

package by.exonit.redmine.client.managers

import by.exonit.redmine.client._
import cats.effect.IO

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
  ): IO[PagedList[SearchResult]]
}
