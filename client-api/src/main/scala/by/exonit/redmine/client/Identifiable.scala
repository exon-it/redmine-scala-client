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

package by.exonit.redmine.client

/**
 * Identifiable entity companion object
 *
 * Contains `unapply` method for pattern matching
 */
object Identifiable {
  /**
   * Unapply method for pattern matching
   * @param i Identifiable instance
   * @tparam T Identifier type
   * @return Optional ID
   */
  def unapply[T](i: Identifiable[T]): Option[T] = Option(i).map(_.id)
}

/**
 * Identifiable entity
 * @tparam T Identifier type
 */
trait Identifiable[T] {
  /**
   * Returns entity identifier
   * @return Entity identifier
   */
  def id: T
}
