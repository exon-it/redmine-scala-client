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

import by.exonit.redmine.client.{Attachment, AttachmentId, UserLink}
import org.json4s._

import scala.collection.immutable

object AttachmentSerializers {
  lazy val all: immutable.Seq[Serializer[_]] = immutable.Seq(
    attachmentIdSerializer,
    attachmentSerializer)

  def deserializeAttachmentId: PartialFunction[JValue, AttachmentId] = {
    case JInt(id) => AttachmentId(id)
  }

  def serializeAttachmentId: PartialFunction[Any, JValue] = {
    case AttachmentId(id) => JInt(id)
  }

  object attachmentIdSerializer extends CustomSerializer[AttachmentId](_ =>
    deserializeAttachmentId -> serializeAttachmentId)

  object attachmentSerializer extends CustomSerializer[Attachment](formats => (
    deserializeAttachment(formats),
    PartialFunction.empty))

  def deserializeAttachment(formats: => Formats): PartialFunction[JValue, Attachment] =
    {
      case j: JObject =>
        implicit val implicitFormats = formats
        Attachment(
          (j \ "id").extract[BigInt],
          RedmineDateParser.parse((j \ "created_on").extract[String]),
          (j \ "filename").extract[String],
          (j \ "filesize").extract[BigInt],
          (j \ "content_type").extractOpt[String],
          (j \ "content_url").extract[String],
          (j \ "description").extractOpt[String],
          (j \ "author").extractOpt[UserLink])
    }
}
