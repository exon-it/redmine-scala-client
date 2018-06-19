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

package by.exonit.redmine.client

import scala.collection.immutable._
import org.joda.time.DateTime

trait JournalIdLike extends Identifiable[BigInt]

case class JournalId(id: BigInt) extends JournalIdLike

case class JournalDetail(
  name: String,
  property: String,
  oldValue: Option[String],
  newValue: Option[String])

case class Journal(
  id: BigInt,
  createdOn: DateTime,
  user: Option[UserLink],
  notes: Option[String],
  details: List[JournalDetail])
    extends JournalIdLike
