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

class IdentifiableSpec extends BasicSpec {
  "Identifiable" when {
    "unapplied" should {
      "return some id when called with an identifiable" in {
        val id = new Identifiable[Int] {
          def id: Int = 42
        }
        Identifiable.unapply(id).value shouldBe 42
      }

      "return None when called with null" in {
        Identifiable.unapply(null) shouldBe None
      }
    }
  }
}
