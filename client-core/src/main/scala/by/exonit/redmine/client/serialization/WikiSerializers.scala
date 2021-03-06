/*
 * Copyright 2017 Exon IT
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

package by.exonit.redmine.client.serialization

import by.exonit.redmine.client._
import org.json4s._
import org.json4s.JsonDSL._

import scala.collection.immutable._

object WikiSerializers {
  import Implicits._

  lazy val all: Seq[Serializer[_]] = Seq(wikiPageIdSerializer, wikiPageSerializer, wikiPageDetailsSerializer,
    newWikiPageSerializer, wikiPageUpdateSerializer)

  def deserializeWikiPageId: PartialFunction[JValue, WikiPageId] = {
    case JString(id) => WikiPageId(id)
  }

  def serializeWikiPageId: PartialFunction[Any, JValue] = {
    case WikiPageId(id) => JString(id)
  }

  object wikiPageIdSerializer extends CustomSerializer[WikiPageId](
    _ => deserializeWikiPageId -> serializeWikiPageId)

  def deserializeWikiPage(implicit formats: Formats): PartialFunction[JValue, WikiPage] = {
    case j: JObject =>
      WikiPage(
        (j \ "title").extract[String], (j \ "version").extract[BigInt],
        RedmineDateParser.parse((j \ "created_on").extract[String]),
        RedmineDateParser.parse((j \ "updated_on").extract[String]),
        (j \ "parent" \ "title").extractOpt[WikiPageId]
      )
  }

  object wikiPageSerializer extends CustomSerializer[WikiPage](
    formats => deserializeWikiPage(formats) -> PartialFunction.empty)

  def deserializeWikiPageDetails(implicit formats: Formats): PartialFunction[JValue, WikiPageDetails] = {
    case j: JObject =>
      WikiPageDetails((j \ "title").extract[String], (j \ "version").extract[BigInt],
        RedmineDateParser.parse((j \ "created_on").extract[String]),
        RedmineDateParser.parse((j \ "updated_on").extract[String]), (j \ "parent" \ "title").extractOpt[WikiPageId],
        (j \ "text").extractOpt[String].getOrElse(""), (j \ "author").extractOpt[UserLink],
        (j \ "comments").extractOpt[String])
  }

  object wikiPageDetailsSerializer extends CustomSerializer[WikiPageDetails](
    formats => deserializeWikiPageDetails(formats) -> PartialFunction.empty)

  def serializeNewWikiPage: PartialFunction[Any, JValue] = {
    case u: WikiPage.New =>
      ("text" -> u.text) ~
        ("comments" -> u.comments) ~
        ("parent_title" -> u.parent.map(_.id))
  }

  object newWikiPageSerializer extends CustomSerializer[WikiPage.New](
    _ => PartialFunction.empty -> serializeNewWikiPage)

  def serializeWikiPageUpdate: PartialFunction[Any, JValue] = {
    case u: WikiPage.Update =>
      ("text" -> u.text) ~
        ("comments" -> u.comments) ~
        ("parent_title" -> u.parent.map(_.map(_.id).orJNull).orJNothing)
  }

  object wikiPageUpdateSerializer extends CustomSerializer[WikiPage.Update](
    _ => PartialFunction.empty -> serializeWikiPageUpdate)

}
