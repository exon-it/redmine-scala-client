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

import enumeratum.EnumEntry.Snakecase
import enumeratum._

import scala.collection.immutable._

/**
 * Group ID trait for identifiable group entity types
 */
trait GroupIdLike extends Identifiable[BigInt]

/**
 * Group ID entity type
 * @param id Group ID
 */
case class GroupId(id: BigInt) extends GroupIdLike

/**
 * Group link entity type
 * @param id Group ID
 * @param name Group Name
 */
case class GroupLink(id: BigInt, name: String) extends GroupIdLike

/**
 * Group entity type
 * @param id ID
 * @param name Name
 * @param users Users in group
 * @param memberships Project memberships
 * @param customFields Custom field values
 */
case class Group(
  id: BigInt,
  name: String,
  users: Option[Set[UserLink]],
  memberships: Option[Set[IdentityMembership]],
  customFields: Option[Set[CustomField]]
) extends GroupIdLike with OptionalCustomFieldSet

/**
 * Group companion object
 *
 * Contains entity types for create and update operations
 * and include flags for read operation
 */
object Group {

  /**
   * Entity type for creating a group
   * @param name Group name
   * @param users Group user list
   */
  case class New(
    name: String,
    users: Set[UserIdLike],
    customFields: Option[Set[CustomField.Update]] = None
  )

  /**
   * Group update entity type
   */
  case class Update(
    name: Option[String] = None,
    customFields: Option[Set[CustomField.Update]] = None
  )

  /**
   * Group read operation additional include code type
   */
  sealed abstract class Include extends EnumEntry with Snakecase

  /**
   * Predefined group read include tokens
   */
  object Include extends Enum[Include] {

    val values: IndexedSeq[Include] = findValues

    /**
     * Project memberships for group
     */
    case object Memberships extends Include

    /**
     * Group users
     */
    case object Users extends Include

  }
}
