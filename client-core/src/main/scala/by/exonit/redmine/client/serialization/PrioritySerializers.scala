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

import scala.collection.immutable

object PrioritySerializers {
  lazy val all: immutable.Seq[Serializer[_]] = immutable.Seq(
    priorityIdSerializer, priorityLinkSerializer, prioritySerializer)

  def deserializePriorityId: PartialFunction[JValue, PriorityId] = {
    case JInt(id) => PriorityId(id)
  }

  def serializePriorityId: PartialFunction[Any, JValue] = {
    case PriorityId(id) => JInt(id)
  }

  object priorityIdSerializer extends CustomSerializer[PriorityId](
    _ => deserializePriorityId -> serializePriorityId)

  def deserializePriorityLink(implicit formats: Formats): PartialFunction[JValue, PriorityLink] = {
    case j: JObject =>
      PriorityLink((j \ "id").extract[BigInt], (j \ "name").extract[String])
  }

  def serializePriorityLink: PartialFunction[Any, JValue] = {
    case PriorityLink(id, name) => ("id" -> id) ~ ("name" -> name)
  }

  object priorityLinkSerializer extends CustomSerializer[PriorityLink](
    formats => deserializePriorityLink(formats) -> serializePriorityLink)

  def deserializePriority(implicit formats: Formats): PartialFunction[JValue, Priority] = {
    case j: JObject =>
      Priority(
        (j \ "id").extract[BigInt],
        (j \ "name").extract[String],
        (j \ "is_default").extractOpt[Boolean].getOrElse(false),
        (j \ "custom_fields").toOption.map(_.extract[Set[CustomField]]))
  }

  object prioritySerializer extends CustomSerializer[Priority](
    formats => deserializePriority(formats) -> PartialFunction.empty)

}
