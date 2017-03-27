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

object IssueStatusSerializers {
  lazy val all: Seq[Serializer[_]] = Seq(
    issueStatusIdSerializer,
    issueStatusLinkSerializer,
    issueStatusSerializer)

  def serializeIssueStatusId(implicit formats: Formats): PartialFunction[Any, JValue] = {
    case IssueStatusId(id) => JInt(id)
  }

  def deserializeIssueStatusId(implicit formats: Formats): PartialFunction[JValue, IssueStatusId] = {
    case JInt(id) => IssueStatusId(id)
  }

  def deserializeIssueStatusLink(implicit formats: Formats): PartialFunction[JValue, IssueStatusLink] = {
    case j: JObject =>
      IssueStatusLink(
        (j \ "id").extract[BigInt],
        (j \ "name").extract[String])
  }

  def deserializeIssueStatus(implicit formats: Formats): PartialFunction[JValue, IssueStatus] = {
    case j: JObject =>
      IssueStatus(
        (j \ "id").extract[BigInt],
        (j \ "name").extract[String],
        (j \ "is_default").toOption.exists(_.extract[Boolean]),
        (j \ "is_closed").toOption.exists(_.extract[Boolean]))
  }

  object issueStatusIdSerializer extends CustomSerializer[IssueStatusId](formats => (
    deserializeIssueStatusId(formats),
    serializeIssueStatusId(formats)))

  object issueStatusLinkSerializer extends CustomSerializer[IssueStatusLink](formats => (
    deserializeIssueStatusLink(formats),
    PartialFunction.empty))

  object issueStatusSerializer extends CustomSerializer[IssueStatus](formats => (
    deserializeIssueStatus(formats),
    PartialFunction.empty))

}
