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

import scala.collection.immutable

object JournalSerializers {
  lazy val all: immutable.Seq[Serializer[_]] = immutable.Seq(
    journalIdSerializer,
    journalSerializer,
    journalDetailSerializer)

  def deserializeJournalId: PartialFunction[JValue, JournalId] = {
    case JInt(id) => JournalId(id)
  }

  def serializeJournalId: PartialFunction[Any, JValue] = {
    case JournalId(id) => JInt(id)
  }

  object journalIdSerializer extends CustomSerializer[JournalId](_ =>
    deserializeJournalId -> serializeJournalId)

  object journalSerializer extends CustomSerializer[Journal](formats =>
    deserializeJournal(formats) -> PartialFunction.empty)

  def deserializeJournal(implicit formats: Formats): PartialFunction[JValue, Journal] =
    {
      case j: JObject =>
        val id = (j \ "id").extract[BigInt]
        val createdOn = RedmineDateParser.parse((j \ "created_on").extract[String])
        val user = (j \ "user").extractOpt[UserLink]
        val notes = (j \ "notes").extractOpt[String]
        val details = (j \ "details").extractOpt[immutable.List[JournalDetail]].getOrElse(immutable.List.empty)
        Journal(id, createdOn, user, notes, details)
    }

  object journalDetailSerializer extends CustomSerializer[JournalDetail](formats =>
    deserializeJournalDetail(formats) -> PartialFunction.empty)

  def deserializeJournalDetail(implicit formats: Formats): PartialFunction[JValue, JournalDetail] =
    {
      case j: JObject =>
        JournalDetail(
          (j \ "name").extract[String],
          (j \ "property").extract[String],
          (j \ "old_value").extractOpt[String],
          (j \ "new_value").extractOpt[String])
    }
}
