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

package by.exonit.redmine.client

sealed abstract class SearchObjectType(val token: String)

object SearchObjectType {

  case object Issues extends SearchObjectType("issues")

  case object News extends SearchObjectType("news")

  case object Documents extends SearchObjectType("documents")

  case object Changesets extends SearchObjectType("changesets")

  case object WikiPages extends SearchObjectType("wiki_pages")

  case object Messages extends SearchObjectType("messages")

  case object Projects extends SearchObjectType("projects")

  case class Custom(override val token: String) extends SearchObjectType(token)

  lazy val predefined: Set[SearchObjectType] = Set(
    Issues, News, Documents, Changesets, WikiPages, Messages, Projects
  )

  def apply(token: String): SearchObjectType =
    predefined.find(_.token.equalsIgnoreCase(token)).getOrElse(Custom(token))

  def unapply(rt: SearchObjectType): Option[String] = Some(rt.token)
}
