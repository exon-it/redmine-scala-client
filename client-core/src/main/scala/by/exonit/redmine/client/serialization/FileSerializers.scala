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
import org.json4s.JsonDSL._

import scala.collection.immutable._

object FileSerializers {
  lazy val all = Seq(
    fileSerializer,
    newFileSerializer
  )

  def deserializeFile(implicit formats: Formats): PartialFunction[JValue, ProjectFile] = {
    case j: JObject =>
      WikiPageDetails((j \ "title").extract[String], (j \ "version").extract[BigInt],
        RedmineDateParser.parse((j \ "created_on").extract[String]),
        RedmineDateParser.parse((j \ "updated_on").extract[String]), (j \ "parent" \ "title").extractOpt[WikiPageId],
        (j \ "text").extractOpt[String].getOrElse(""), (j \ "author").extractOpt[UserLink],
        (j \ "comments").extractOpt[String],
        (j \ "attachments").toOption.map(_.extract[Set[Attachment]])
      )
      ProjectFile(
        (j \ "id").extract[BigInt],
        (j \ "filename").extract[String],
        (j \ "filesize").extract[BigInt],
        (j \ "content_type").extract[String],
        (j \ "description").extract[String],
        (j \ "content_url").extract[String],
        (j \ "author").extract[UserLink],
        RedmineDateParser.parse((j \ "created_on").extract[String]),
        (j \ "version").extractOpt[VersionLink],
        (j \ "digest").extract[String],
        (j \ "downloads").extract[BigInt]
      )
  }

  object fileSerializer extends CustomSerializer[ProjectFile](
    formats => deserializeFile(formats) -> PartialFunction.empty)

  def serializeNewFile: PartialFunction[Any, JValue] = {
    case u: ProjectFile.New =>
      ("token" -> u.token) ~
        ("version_id" -> u.version.map(_.id)) ~
        ("filename" -> u.filename) ~
        ("description" -> u.description)
  }

  object newFileSerializer extends CustomSerializer[ProjectFile.New](
    _ => PartialFunction.empty -> serializeNewFile)
}
