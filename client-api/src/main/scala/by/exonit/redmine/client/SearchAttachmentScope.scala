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

import enumeratum._

import scala.collection.immutable

sealed abstract class SearchAttachmentScope(override val entryName: String) extends EnumEntry
object SearchAttachmentScope extends Enum[SearchAttachmentScope] {
  val values: immutable.IndexedSeq[SearchAttachmentScope] = findValues

  case object DoNotSearch extends SearchAttachmentScope("0")
  case object Files extends SearchAttachmentScope("only")
  case object FilesAndDescriptions extends SearchAttachmentScope("1")
}
