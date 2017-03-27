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

package by.exonit.redmine.client.serialization

import org.joda.time.{ReadableInstant, ReadablePartial}

object Implicits {

  implicit class RedmineInstantExtensions(instant: ReadableInstant) {
    def toRedmineFullDate: String =
      RedmineDateParser.FULL_DATE_FORMAT.print(instant)

    def toRedmineShortDate: String =
      RedmineDateParser.SHORT_DATE_FORMAT.print(instant)

    def toRedmine2ShortDate: String =
      RedmineDateParser.SHORT_DATE_FORMAT_V2.print(instant)

    def toRedmine2FullDate: String =
      RedmineDateParser.FULL_DATE_FORMAT_V2.print(instant)

    def toRedmine3FullDate: String =
      RedmineDateParser.FULL_DATE_FORMAT_V3.print(instant)
  }

  implicit class RedminePartialExtensions(partial: ReadablePartial) {
    def toRedmineFullDate: String =
      RedmineDateParser.FULL_DATE_FORMAT.print(partial)

    def toRedmineShortDate: String =
      RedmineDateParser.SHORT_DATE_FORMAT.print(partial)

    def toRedmine2ShortDate: String =
      RedmineDateParser.SHORT_DATE_FORMAT_V2.print(partial)

    def toRedmine2FullDate: String =
      RedmineDateParser.FULL_DATE_FORMAT_V2.print(partial)

    def toRedmine3FullDate: String =
      RedmineDateParser.FULL_DATE_FORMAT_V3.print(partial)
  }

}
