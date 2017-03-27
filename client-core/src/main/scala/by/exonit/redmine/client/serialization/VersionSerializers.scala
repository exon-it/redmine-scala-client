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

import scala.collection.immutable.Seq

object VersionSerializers {
  import Implicits._

  lazy val all: Seq[Serializer[_]] = Seq(
    versionIdSerializer, versionStatusSerializer, versionSharingSerializer, versionLinkSerializer, versionSerializer,
    newVersionSerializer, versionUpdateSerializer)

  def deserializeVersionId(implicit formats: Formats): PartialFunction[JValue, VersionId] = {
    case JInt(id) => VersionId(id)
  }

  def serializeVersionId(implicit formats: Formats): PartialFunction[Any, JValue] = {
    case VersionId(id) => JInt(id)
  }

  object versionIdSerializer extends CustomSerializer[VersionId](
    formats => deserializeVersionId(formats) -> serializeVersionId(formats))


  def deserializeVersionStatus(implicit formats: Formats): PartialFunction[JValue, Version.Status] = {
    case JString(s) => Version.Status(s)
  }

  def serializeVersionStatus(implicit formats: Formats): PartialFunction[Any, JValue] = {
    case Version.Status(id) => JString(id)
  }

  object versionStatusSerializer extends CustomSerializer[Version.Status](
    formats => deserializeVersionStatus(formats) -> serializeVersionStatus(formats))

  def deserializeVersionSharing(implicit formats: Formats): PartialFunction[JValue, Version.Sharing] = {
    case JString(s) => Version.Sharing(s)
  }

  def serializeVersionSharing(implicit formats: Formats): PartialFunction[Any, JValue] = {
    case Version.Sharing(id) => JString(id)
  }

  object versionSharingSerializer extends CustomSerializer[Version.Sharing](
    formats => deserializeVersionSharing(formats) -> serializeVersionSharing(formats))

  def deserializeVersionLink(implicit formats: Formats): PartialFunction[JValue, VersionLink] = {
    case j: JObject =>
      VersionLink(
        (j \ "id").extract[BigInt],
        (j \ "name").extract[String])
  }

  object versionLinkSerializer extends CustomSerializer[VersionLink](
    formats => deserializeVersionLink(formats) -> PartialFunction.empty)

  def deserializeVersion(implicit formats: Formats): PartialFunction[JValue, Version] = {
    case j: JObject =>
      Version(
        (j \ "id").extract[BigInt],
        (j \ "project").extract[ProjectLink],
        (j \ "name").extract[String],
        (j \ "description").extractOpt[String],
        (j \ "status").extract[Version.Status],
        (j \ "sharing").extract[Version.Sharing],
        (j \ "due_date").extractOpt[String].map(RedmineDateParser.parse).map(_.toLocalDate),
        RedmineDateParser.parse((j \ "created_on").extract[String]),
        RedmineDateParser.parse((j \ "updated_on").extract[String]),
        (j \ "custom_fields").extractOpt[Set[CustomField]])
  }

  object versionSerializer extends CustomSerializer[Version](
    formats => deserializeVersion(formats) -> PartialFunction.empty)

  def serializeNewVersion(implicit formats: Formats): PartialFunction[Any, JValue] = {
    case v@Version.New(name) =>
      ("name" -> name) ~
        ("status" -> v.status.toOpt.map(Extraction.decompose(_))) ~
        ("sharing" -> v.sharing.toOpt.map(Extraction.decompose(_))) ~
        ("due_date" -> v.dueDate.toOpt.map(_.toRedmine2ShortDate)) ~
        ("description" -> v.description.toOpt) ~
        ("custom_fields" -> v.customFields.toOpt.map(_.map(Extraction.decompose)))
  }

  object newVersionSerializer extends CustomSerializer[Version.New](
    formats => PartialFunction.empty -> serializeNewVersion(formats))

  def serializeVersionUpdate(implicit formats: Formats): PartialFunction[Any, JValue] = {
    case u: Version.Update =>
      ("name" -> u.name.toOpt) ~
        ("status" -> u.status.toOpt.map(Extraction.decompose(_))) ~
        ("sharing" -> u.sharing.toOpt.map(Extraction.decompose(_))) ~
        ("due_date" -> u.dueDate.toOpt.map(_.map(_.toRedmine2ShortDate).map(Extraction.decompose).getOrElse(JNull)).getOrElse(JNothing)) ~
        ("description" -> u.description.toOpt.map(_.map(Extraction.decompose).getOrElse(JNull)).getOrElse(JNothing)) ~
        ("custom_fields" -> u.customFields.toOpt.map(_.map(Extraction.decompose)))
  }

  object versionUpdateSerializer extends CustomSerializer[Version.Update](
    formats => PartialFunction.empty -> serializeVersionUpdate(formats))

}
