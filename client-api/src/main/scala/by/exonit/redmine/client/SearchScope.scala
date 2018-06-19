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

import enumeratum.EnumEntry.Snakecase
import enumeratum._

import scala.collection.immutable

sealed abstract class SearchScope extends EnumEntry with Snakecase

object SearchScope extends Enum[SearchScope] {
  val values: immutable.IndexedSeq[SearchScope] = findValues

  case object All extends SearchScope

  case object MyProjects extends SearchScope

  case object Subprojects extends SearchScope
}