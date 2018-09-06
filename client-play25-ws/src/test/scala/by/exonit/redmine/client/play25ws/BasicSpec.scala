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

package by.exonit.redmine.client.play25ws

import cats.effect._
import org.scalatest._
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.time.{Millis, Seconds, Span}

import scala.concurrent.ExecutionContext

trait BasicSpec extends WordSpec with Assertions with Matchers with ScalaFutures with Inside
with OptionValues {
  override implicit val patienceConfig: PatienceConfig = PatienceConfig(timeout = Span(2, Seconds), interval = Span(15, Millis))
  val jsonContentType = "application/json"

  implicit val ec: ExecutionContext = ExecutionContext.global

  implicit val ioTimer: Timer[IO] = IO.timer(ec)
}
