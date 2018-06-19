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

object TrackerSerializers {
  lazy val all: Seq[Serializer[_]] = Seq(
    trackerIdSerializer, trackerLinkSerializer, trackerSerializer)

  def deserializeTrackerId: PartialFunction[JValue, TrackerId] = {
    case JInt(id) => TrackerId(id)
  }

  def serializeTrackerId: PartialFunction[Any, JValue] = {
    case TrackerId(id) => JInt(id)
  }

  object trackerIdSerializer extends CustomSerializer[TrackerId](
    _ => deserializeTrackerId -> serializeTrackerId)

  def deserializeTrackerLink(implicit formats: Formats): PartialFunction[JValue, TrackerLink] = {
    case j: JObject =>
      TrackerLink(
        (j \ "id").extract[BigInt],
        (j \ "name").extract[String])
  }

  object trackerLinkSerializer extends CustomSerializer[TrackerLink](
    formats => deserializeTrackerLink(formats) -> PartialFunction.empty)

  def deserializeTracker(implicit formats: Formats): PartialFunction[JValue, Tracker] = {
    case j: JObject =>
      Tracker(
        (j \ "id").extract[BigInt],
        (j \ "name").extract[String],
        (j \ "default_status").extractOpt[IssueStatusLink]
      )
  }

  object trackerSerializer extends CustomSerializer[Tracker](
    formats => deserializeTracker(formats) -> PartialFunction.empty)

}
