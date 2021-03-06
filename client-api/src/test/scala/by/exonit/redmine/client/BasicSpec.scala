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

import org.scalatest.concurrent.ScalaFutures
import org.scalatest._
import org.scalatest.time.{Millis, Seconds, Span}

trait BasicSpec extends WordSpec with Assertions with Matchers with ScalaFutures with Inside
with OptionValues {
  override implicit def patienceConfig = PatienceConfig(timeout = Span(2, Seconds), interval = Span(15, Millis))
}
