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

import org.joda.time.{DateTime, DateTimeZone, LocalDate}
import org.joda.time.format.{DateTimeFormat, DateTimeFormatter, ISODateTimeFormat}

object RedmineDateParser {

  val FULL_DATE_FORMAT: DateTimeFormatter = DateTimeFormat.forPattern("""yyyy/MM/dd HH:mm:ss Z""")

  val FULL_DATE_FORMAT_V2: DateTimeFormatter = ISODateTimeFormat.dateTimeNoMillis()

  val FULL_DATE_FORMAT_V3: DateTimeFormatter = ISODateTimeFormat.dateTime()

  private val SHORT_DATE_PATTERN_V1 = """yyyy/MM/dd"""

  private val SHORT_DATE_PATTERN_V2 = """yyyy-MM-dd"""

  val SHORT_DATE_FORMAT: DateTimeFormatter = DateTimeFormat.forPattern(SHORT_DATE_PATTERN_V1)

  val SHORT_DATE_FORMAT_V2: DateTimeFormatter = DateTimeFormat.forPattern(SHORT_DATE_PATTERN_V2)

  private val SHORT_DATE_FORMAT_MAX_LENGTH = Math.max(SHORT_DATE_PATTERN_V1.length, SHORT_DATE_PATTERN_V2.length)

  def parse(dateStr: String): DateTime = {
    if (dateStr.length > SHORT_DATE_FORMAT_MAX_LENGTH) {
      parseLongFormat(dateStr)
    } else {
      parseShortFormat(dateStr).toDateTimeAtStartOfDay
    }
  }

  def parseLocalDate(str: String): LocalDate = parseShortFormat(str)

  private val SLASH_SEPARATOR = '/'

  private def parseShortFormat(dateStr: String): LocalDate = {
    val format = if (dateStr.length >= 5 && dateStr.charAt(4) == SLASH_SEPARATOR) {
      RedmineDateParser.SHORT_DATE_FORMAT
    } else {
      RedmineDateParser.SHORT_DATE_FORMAT_V2
    }
    format.parseLocalDate(dateStr)
  }

  private def parseLongFormat(dateStr: String): DateTime = {
    if (dateStr.length >= 5 && dateStr.charAt(4) == SLASH_SEPARATOR) {
      RedmineDateParser.FULL_DATE_FORMAT.parseDateTime(dateStr)
    } else {
      val s0 = normalizeTimeZoneInfo(dateStr)
      val format = if (s0.indexOf('.') < 0) RedmineDateParser.FULL_DATE_FORMAT_V2 else RedmineDateParser.FULL_DATE_FORMAT_V3
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
