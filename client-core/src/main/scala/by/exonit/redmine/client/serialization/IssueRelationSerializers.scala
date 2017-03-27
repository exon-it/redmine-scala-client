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

import by.exonit.redmine.client.{IssueId, IssueRelation}
import org.json4s._
import org.json4s.JsonDSL._

import scala.collection.immutable

object IssueRelationSerializers {
  lazy val all: immutable.Seq[Serializer[_]] = immutable.Seq(
    issueRelationSerializer,
    issueRelationTypeSerializer,
    newIssueRelationSerializer)

  object issueRelationSerializer extends CustomSerializer[IssueRelation](formats => (
    deserializeIssueRelation(formats),
    PartialFunction.empty))

  def deserializeIssueRelation(formats: => Formats): PartialFunction[JValue, IssueRelation] = {
    case j: JObject =>
      implicit val implicitFormats = formats
      val id = (j \ "id").extract[BigInt]
      val issueFrom = IssueId((j \ "issue_id").extract[BigInt])
      val issueTo = IssueId((j \ "issue_to_id").extract[BigInt])
      val relationType = (j \ "relation_type").extract[IssueRelation.RelationType]
      val delay = (j \ "delay").extractOpt[Int]
      IssueRelation(id, issueFrom, issueTo, relationType, delay)
  }

  def deserializeIssueRelationType(formats: => Formats): PartialFunction[JValue, IssueRelation.RelationType] = {
    case j: JString =>
      implicit val implicitFormats = formats
      IssueRelation.RelationType.apply(j.extract[String])
  }

  def serializeIssueRelationType(formats: => Formats): PartialFunction[Any, JValue] = {
    case rt: IssueRelation.RelationType => JString(rt.token)
  }

  object issueRelationTypeSerializer extends CustomSerializer[IssueRelation.RelationType](formats => (
    deserializeIssueRelationType(formats),
    serializeIssueRelationType(formats)))

  def serializeNewIssueRelation(implicit formats: Formats): PartialFunction[Any, JValue] = {
    case i@IssueRelation.New(issueTo) =>
      ("issue_to_id" -> issueTo.id) ~
        ("relation_type" -> i.relationType.toOpt.map(Extraction.decompose)) ~
        ("delay" -> i.delayDays.toOpt)
  }

  object newIssueRelationSerializer extends CustomSerializer[IssueRelation.New](
    formats => PartialFunction.empty -> serializeNewIssueRelation(formats))
}
