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

object CategorySerializers {
  lazy val all: Seq[Serializer[_]] = Seq(
    categoryIdSerializer,
    categoryLinkSerializer,
    categorySerializer,
    newCategorySerializer,
    categoryUpdateSerializer)

  def serializeCategoryId: PartialFunction[Any, JValue] = {
    case CategoryId(id) => JInt(id)
  }

  def deserializeCategoryId: PartialFunction[JValue, CategoryId] = {
    case JInt(id) => CategoryId(id)
  }

  def deserializeCategoryLink(implicit formats: Formats): PartialFunction[JValue, CategoryLink] = {
    case j: JObject =>
      CategoryLink(
        (j \ "id").extract[BigInt],
        (j \ "name").extract[String])
  }

  def deserializeCategory(implicit formats: Formats): PartialFunction[Any, Category] = {
    case j: JObject =>
      Category(
        (j \ "id").extract[BigInt],
        (j \ "name").extract[String],
        (j \ "project").extractOpt[ProjectLink],
        (j \ "assigned_to").extractOpt[UserLink]
      )
  }

  def serializeNewCategory(implicit formats: Formats): PartialFunction[Any, JValue] = {
    case Category.New(name, assignee) =>
      ("name" -> name) ~ ("assigned_to_id" -> assignee.map(Extraction.decompose))
  }

  def serializeCategoryUpdate(implicit formats: Formats): PartialFunction[Any, JValue] = {
    case u: Category.Update =>
      ("name" -> u.name.toOpt) ~
        ("project_id" -> u.project.toOpt.map(_.map(Extraction.decompose).getOrElse(JNull)).getOrElse(JNothing)) ~
        ("assigned_to_id" -> u.defaultAssignee.toOpt.map(_.map(Extraction.decompose).getOrElse(JNull)).getOrElse(JNothing))
  }

  object categoryIdSerializer extends CustomSerializer[CategoryId](_ =>
    deserializeCategoryId -> serializeCategoryId)

  object categoryLinkSerializer extends CustomSerializer[CategoryLink](formats => (
    deserializeCategoryLink(formats),
    PartialFunction.empty))

  object categorySerializer extends CustomSerializer[Category](formats => (
    deserializeCategory(formats),
    PartialFunction.empty))

  object newCategorySerializer extends CustomSerializer[Category.New](formats => (
    PartialFunction.empty,
    serializeNewCategory(formats)))

  object categoryUpdateSerializer extends CustomSerializer[Category.Update](formats => (
    PartialFunction.empty,
    serializeCategoryUpdate(formats)))

}
