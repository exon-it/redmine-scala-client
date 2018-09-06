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

import cats.effect.IO

import scala.collection.immutable._

case class PagedList[T](
  items: List[T],
  total: BigInt,
  offset: BigInt,
  allItems: IO[List[T]],
  next: Option[IO[PagedList[T]]] = None,
  previous: Option[IO[PagedList[T]]] = None
)
