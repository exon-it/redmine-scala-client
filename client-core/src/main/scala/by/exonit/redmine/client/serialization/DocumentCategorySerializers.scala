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

import by.exonit.redmine.client.DocumentCategory
import org.json4s._

import scala.collection.immutable.Seq

object DocumentCategorySerializers {
  lazy val all: Seq[Serializer[_]] = Seq(
    documentCategoryDeserializer
  )

  def deserializeDocumentCategory(implicit formats: Formats): PartialFunction[JValue, DocumentCategory] = {
    case j: JObject =>
      DocumentCategory(
        (j \ "id").extract[BigInt],
        (j \ "name").extract[String],
        (j \ "is_default").toOption.exists(_.extract[Boolean])
      )
  }

  object documentCategoryDeserializer extends CustomSerializer[DocumentCategory](
    formats => deserializeDocumentCategory(formats) -> PartialFunction.empty
  )
}
