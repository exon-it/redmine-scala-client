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

import by.exonit.redmine.client.{Changeset, UserLink}
import org.json4s._

import scala.collection.immutable

object ChangesetSerializers {
  lazy val all: immutable.Seq[Serializer[_]] = immutable.Seq(changesetSerializer)

  object changesetSerializer extends CustomSerializer[Changeset](formats => (
    deserializeChangeset(formats),
    PartialFunction.empty))

  def deserializeChangeset(formats: => Formats): PartialFunction[JValue, Changeset] =
    {
      case j: JObject =>
        implicit val implicitFormats = formats
        val revision = (j \ "revision").extract[String]
        val committedOn = RedmineDateParser.parse((j \ "committed_on").extract[String])
        val user = (j \ "user").extractOpt[UserLink]
        val comments = (j \ "comments").extractOpt[String]
        Changeset(revision, committedOn, user, comments)
    }
}
