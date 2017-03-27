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

import scala.collection.immutable._

object NewsSerializers {
  lazy val all: Seq[Serializer[_]] = Seq(
    newsIdSerializer, newsSerializer)

  def deserializeNewsId(implicit formats: Formats): PartialFunction[JValue, NewsId] = {
    case JInt(id) => NewsId(id)
  }

  def serializeNewsId(implicit formats: Formats): PartialFunction[Any, JValue] = {
    case NewsId(id) => JInt(id)
  }

  object newsIdSerializer extends CustomSerializer[NewsId](
    formats => deserializeNewsId(formats) -> serializeNewsId(formats))

  def deserializeNews(implicit formats: Formats): PartialFunction[JValue, News] = {
    case j: JObject =>
      News(
        (j \ "id").extract[BigInt],
        RedmineDateParser.parse((j \ "created_on").extract[String]),
        (j \ "project").extractOpt[ProjectLink],
        (j \ "author").extractOpt[UserLink],
        (j \ "title").extract[String],
        (j \ "summary").extractOpt[String],
        (j \ "description").extract[String],
        (j \ "link").extractOpt[String])
  }

  object newsSerializer extends CustomSerializer[News](
    formats => deserializeNews(formats) -> PartialFunction.empty)

}
