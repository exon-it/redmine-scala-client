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

import by.exonit.redmine.client.{CustomField, CustomFieldDefinition, RoleLink, TrackerLink}
import org.json4s._
import org.json4s.JsonDSL._

import scala.collection.immutable
import scala.collection.immutable._

object CustomFieldSerializers {
  lazy val all: immutable.Seq[Serializer[_]] = immutable.Seq(
    customFieldSerializer, customFieldUpdateSerializer, customFieldDefinitionSerializer)

  def deserializeCustomField(implicit formats: Formats): PartialFunction[JValue, CustomField] = {
    case j: JObject =>
      val id = (j \ "id").extract[BigInt]
      val name = (j \ "name").extract[String]
      val isMultiple = (j \ "multiple").extractOpt[Boolean].getOrElse(false)
      if (isMultiple) {
        val values = (j \ "value").extract[List[String]]
        CustomField.MultiValue(id, name, values)
      } else {
        val value = (j \ "value").extractOpt[String]
        CustomField.SingleValue(id, name, value)
      }
  }

  object customFieldSerializer
    extends CustomSerializer[CustomField](
      formats => (
        deserializeCustomField(formats), PartialFunction.empty))

  def serializeCustomFieldUpdate(implicit formats: Formats): PartialFunction[Any, JValue] = {
    case CustomField.Update.SingleValue(id, value) => ("id" -> id) ~ ("value" -> value.map(Extraction.decompose).getOrElse(JNull))
    case CustomField.Update.MultiValue(id, values) => ("id" -> id) ~ ("value" -> values)
  }

  object customFieldUpdateSerializer
    extends CustomSerializer[CustomField.Update](
      formats =>
        (PartialFunction.empty, serializeCustomFieldUpdate(formats)))

  def deserializeCustomFieldDefinition(implicit formats: Formats): PartialFunction[JValue, CustomFieldDefinition] = {
    case j: JObject =>
      CustomFieldDefinition(
        (j \ "id").extract[BigInt],
        (j \ "name").extract[String],
        (j \ "customized_type").extract[String],
        (j \ "field_format").extract[String],
        (j \ "visible").extractOpt[Boolean].getOrElse(false),
        (j \ "regexp").extractOpt[String],
        (j \ "min_length").extractOpt[Int],
        (j \ "max_length").extractOpt[Int],
        (j \ "is_required").extractOpt[Boolean].getOrElse(false),
        (j \ "is_filter").extractOpt[Boolean].getOrElse(false),
        (j \ "searchable").extractOpt[Boolean].getOrElse(false),
        (j \ "multiple").extractOpt[Boolean].getOrElse(false),
        (j \ "default_value").extractOpt[String],
        (j \ "possible_values").toOption.map(j => (j \ "value").extract[Set[String]]),
        (j \ "trackers").toOption.map(_.extract[Set[TrackerLink]]),
        (j \ "roles").toOption.map(_.extract[Set[RoleLink]]))
  }

  object customFieldDefinitionSerializer extends CustomSerializer[CustomFieldDefinition](
    formats => deserializeCustomFieldDefinition(formats) -> PartialFunction.empty)

}
