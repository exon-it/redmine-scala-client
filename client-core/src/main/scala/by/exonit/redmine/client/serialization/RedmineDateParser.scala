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

import org.joda.time.{DateTime, LocalDate}
import org.joda.time.format.{DateTimeFormat, DateTimeFormatter, ISODateTimeFormat}

object RedmineDateParser {

  val FullDateFormatV1: DateTimeFormatter = DateTimeFormat.forPattern("""yyyy/MM/dd HH:mm:ss Z""")

  val FullDateFormatV2: DateTimeFormatter = ISODateTimeFormat.dateTimeNoMillis()

  val FullDateFormatV3: DateTimeFormatter = ISODateTimeFormat.dateTime()

  private val ShortDatePatternV1 = """yyyy/MM/dd"""

  private val ShortDatePatternV2 = """yyyy-MM-dd"""

  val ShortDateFormatV1: DateTimeFormatter = DateTimeFormat.forPattern(ShortDatePatternV1)

  val ShortDateFormatV2: DateTimeFormatter = DateTimeFormat.forPattern(ShortDatePatternV2)

  private val ShortDateFormatMaxLength = Math.max(ShortDatePatternV1.length, ShortDatePatternV2.length)

  private val SlashSeparator = '/'

  private val V1DateMinStringLength = 5
  private val V1DateSlashPositionIndex = 4

  def parse(dateStr: String): DateTime = {
    if (dateStr.length > ShortDateFormatMaxLength) {
      parseLongFormat(dateStr)
    } else {
      parseShortFormat(dateStr).toDateTimeAtStartOfDay
    }
  }

  def parseLocalDate(str: String): LocalDate = parseShortFormat(str)

  private def parseShortFormat(dateStr: String): LocalDate = {

    val format = if (dateStr.length >= V1DateMinStringLength && dateStr.charAt(V1DateSlashPositionIndex) == SlashSeparator) {
      RedmineDateParser.ShortDateFormatV1
    } else {
      RedmineDateParser.ShortDateFormatV2
    }
    format.parseLocalDate(dateStr)
  }

  private def parseLongFormat(dateStr: String): DateTime = {
    if (dateStr.length >= V1DateMinStringLength && dateStr.charAt(V1DateSlashPositionIndex) == SlashSeparator) {
      RedmineDateParser.FullDateFormatV1.parseDateTime(dateStr)
    } else {
      val s0 = normalizeTimeZoneInfo(dateStr)
      val format = if (s0.indexOf('.') < 0) RedmineDateParser.FullDateFormatV2 else RedmineDateParser.FullDateFormatV3
      format.parseDateTime(s0)
    }
  }

  private def normalizeTimeZoneInfo(dateStr: String): String = {
    if (dateStr.endsWith("Z")) {
      dateStr.substring(0, dateStr.length - 1) + "-0000"
    } else {
      val inset = 6
      if (dateStr.length <= inset) {
        throw new IllegalArgumentException("Bad date value " + dateStr)
      }
      val s0 = dateStr.substring(0, dateStr.length - inset)
      val s1 = dateStr.substring(dateStr.length - inset, dateStr.length)
      s0 + "GMT" + s1
    }
  }
}
