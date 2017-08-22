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

import scala.collection.immutable._

trait CustomFieldIdLike extends Identifiable[BigInt]

case class CustomFieldId(id: BigInt) extends CustomFieldIdLike

sealed trait CustomField extends CustomFieldIdLike {
  def name: String
}

object CustomField {

  sealed trait Update extends CustomFieldIdLike

  case class SingleValue(id: BigInt, name: String, value: Option[String]) extends CustomField

  case class MultiValue(id: BigInt, name: String, values: List[String]) extends CustomField

  object Update {

    case class SingleValue(id: BigInt, value: Option[String]) extends Update

    case class MultiValue(id: BigInt, values: List[String]) extends Update

  }

}

case class CustomFieldPossibleValue(value: String, label: Option[String])

case class CustomFieldDefinition(
  id: BigInt,
  name: String,
  customizedType: String,
  fieldFormat: String,
  isVisible: Boolean,
  regexp: Option[String],
  minLength: Option[Int],
  maxLength: Option[Int],
  isRequired: Boolean,
  isFilter: Boolean,
  isSearchable: Boolean,
  isMultiple: Boolean,
  defaultValue: Option[String],
  possibleValues: Option[Set[CustomFieldPossibleValue]],
  trackers: Option[Set[TrackerLink]],
  roles: Option[Set[RoleLink]]) extends CustomFieldIdLike

trait OptionalCustomFieldSet {

  def customFields: Option[Set[CustomField]]
  /**
   * Finds custom field by it's ID
   * @param id Custom field ID
   * @return Custom field
   */
  def getCustomFieldById(id: CustomFieldIdLike): Option[CustomField] = {
    customFields.flatMap(_.find {cf => cf.id == id.id})
  }

  /**
   * Finds custom field by it's name
   * @note uses default string equality comparison
   * @param name Custom field name
   * @return Custom field
   */
  def getCustomFieldByName(name: String): Option[CustomField] = {
    customFields.flatMap(_.find {cf => cf.name == name})
  }

}
