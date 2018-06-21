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

package by.exonit.redmine.client.serialization

import org.joda.time.{ReadableInstant, ReadablePartial}
import org.json4s.JValue
import org.json4s.JsonAST.{JNothing, JNull}

object Implicits {
  implicit class ExtendedOption[+A](o: Option[A]) {
    def orJNull(implicit ev: A => JValue): JValue = o.map(ev).getOrElse(JNull)
    def orJNothing(implicit ev: A => JValue): JValue = o.map(ev).getOrElse(JNothing)
  }

  implicit class RedmineInstantExtensions(instant: ReadableInstant) {
    def toRedmineFullDate: String =
      RedmineDateParser.FullDateFormatV1.print(instant)

    def toRedmineShortDate: String =
      RedmineDateParser.ShortDateFormatV1.print(instant)

    def toRedmine2ShortDate: String =
      RedmineDateParser.ShortDateFormatV2.print(instant)

    def toRedmine2FullDate: String =
      RedmineDateParser.FullDateFormatV2.print(instant)

    def toRedmine3FullDate: String =
      RedmineDateParser.FullDateFormatV3.print(instant)
  }

  implicit class RedminePartialExtensions(partial: ReadablePartial) {
    def toRedmineFullDate: String =
      RedmineDateParser.FullDateFormatV1.print(partial)

    def toRedmineShortDate: String =
      RedmineDateParser.ShortDateFormatV1.print(partial)

    def toRedmine2ShortDate: String =
      RedmineDateParser.ShortDateFormatV2.print(partial)

    def toRedmine2FullDate: String =
      RedmineDateParser.FullDateFormatV2.print(partial)

    def toRedmine3FullDate: String =
      RedmineDateParser.FullDateFormatV3.print(partial)
  }

}
