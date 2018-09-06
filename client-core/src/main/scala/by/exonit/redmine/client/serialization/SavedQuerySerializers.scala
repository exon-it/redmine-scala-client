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

package by.exonit.redmine.client.serialization

import by.exonit.redmine.client._
import org.json4s._

import scala.collection.immutable._

object SavedQuerySerializers {
  lazy val all: Seq[Serializer[_]] = Seq(
    savedQueryIdSerializer, savedQuerySerializer)

  def deserializeSavedQueryId: PartialFunction[JValue, SavedQueryId] = {
    case JInt(id) => SavedQueryId(id)
  }

  def serializeSavedQueryId: PartialFunction[Any, JValue] = {
    case SavedQueryId(id) => JInt(id)
  }

  object savedQueryIdSerializer extends CustomSerializer[SavedQueryId](
    _ => deserializeSavedQueryId -> serializeSavedQueryId)

  def deserializeSavedQuery(implicit formats: Formats): PartialFunction[JValue, SavedQuery] = {
    case j: JObject =>
      SavedQuery(
        (j \ "id").extract[BigInt],
        (j \ "name").extract[String],
        (j \ "project_id").extractOpt[ProjectId],
        (j \ "is_public").extractOpt[Boolean])
  }

  object savedQuerySerializer extends CustomSerializer[SavedQuery](
    formats => deserializeSavedQuery(formats) -> PartialFunction.empty)

}
