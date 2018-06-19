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

package by.exonit.redmine.client.managers.impl

import by.exonit.redmine.client.managers.WebClient.RequestDSL
import by.exonit.redmine.client.managers.WebClient.RequestDSL.Request
import cats.data.NonEmptyList
import enumeratum.EnumEntry

import scala.collection.immutable.Seq

object RequestBlocks {

  /** Adds includes query parameter for entity list queries
    * @param includes Include list
    * @tparam T Tokenized include enumeration type
    * @return Request DSL command to add "includes" query parameter
    */
  def include[T <: EnumEntry](includes: Iterable[T]): Request[Unit] = {
    val includeTokensOpt = NonEmptyList.fromList(includes.map(_.entryName).toList)
    val includeValueOpt = includeTokensOpt.map(list => list.reduceLeft(_ + "," + _))
    val addQueries = includeValueOpt.map {v => Seq("include" -> v)}.getOrElse(Seq.empty)
    RequestDSL.addQueries(addQueries: _*)
  }
}
