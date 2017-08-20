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

class FluentSettableFieldSpec extends BasicSpec {
  "Fluent Settable Field" when {

    "created" should {
      "report value as unset" in {
        val field = new FluentSettableField[Unit, this.type](this)
        field.isSet shouldBe false
      }

      "return None as optional value" in {
        val field = new FluentSettableField[Unit, this.type](this)
        field.toOpt shouldBe None
      }
    }

    "set" should {
      "return provided return instance" in {
        val field = new FluentSettableField[Unit, this.type](this)
        field.set(()) shouldBe this
      }
    }

    "set to some value" should {
      "return the value" in {
        val field = new FluentSettableField[Int, this.type](this)
        field.set(42)
        field.get shouldBe 42
      }

      "report value as set" in {
        val field = new FluentSettableField[Int, this.type](this)
        field.set(42)
        field shouldBe 'set
      }

      "return optional with the value" in {
        val field = new FluentSettableField[Int, this.type](this)
        field.set(42)
        field.toOpt.value shouldBe 42
      }

      "invoke once function passed to foreach" in {
        val field = new FluentSettableField[Int, this.type](this)
        field.set(42)

        var callCount = 0
        field.foreach {_ => callCount += 1}
        callCount shouldBe 1
      }

      "invoke function passed to foreach with field value" in {
        val field = new FluentSettableField[Int, this.type](this)
        field.set(42)

        var value = 0
        field.foreach {f => value = f}
        value shouldBe 42
      }

      "allow unsetting a field" in {
        val field = new FluentSettableField[Unit, this.type](this)
        field.set(())
        field shouldBe 'set
        field.unset()
        field should not be 'set
      }
    }

    "unset" should {
      "throw NoSuchElementException on getting value" in {
        val field = new FluentSettableField[Unit, this.type](this)
        intercept[NoSuchElementException] {
          field.get
        }
      }

      "return None as optional value" in {
        val field = new FluentSettableField[Unit, this.type](this)
        field.toOpt shouldBe None
      }

      "not invoke function passed to foreach" in {
        val field = new FluentSettableField[Int, this.type](this)
        var callCount = 0
        field.foreach {_ => callCount += 1}
        callCount shouldBe 0
      }
    }

    "set using optional value" should {
      "return provided return instance" in {
        val field = new FluentSettableField[Unit, this.type](this)
        field.setOpt(Some(())) shouldBe this
      }

      "be set after using optional Some" in {
        val field = new FluentSettableField[Int, this.type](this)
        field.setOpt(Some(42))
        field shouldBe 'set
        field.get shouldBe 42
      }
      "be unset after using optional None" in {
        val field = new FluentSettableField[Unit, this.type](this)
        field.setOpt(None)
        field should not be 'set
      }
    }
  }
}
