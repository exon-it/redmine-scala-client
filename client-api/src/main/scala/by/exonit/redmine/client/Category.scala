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

/**
 * Category ID trait for identifiable category entity types
 */
trait CategoryIdLike extends Identifiable[BigInt]

/**
 * Category ID entity type
 * @param id Category ID
 */
case class CategoryId(id: BigInt) extends CategoryIdLike

/**
 * Category link entity type
 * @param id Category ID
 * @param name Category name
 */
case class CategoryLink(id: BigInt, name: String) extends CategoryIdLike

/**
 * Category entity type
 * @param id ID
 * @param name Name
 * @param project Project
 * @param defaultAssignee Default assigned user
 */
case class Category(
  id: BigInt,
  name: String,
  project: Option[ProjectLink],
  defaultAssignee: Option[UserLink]
) extends CategoryIdLike

/**
 * Category companion object
 *
 * Contains entity types for create and update operations on categories
 */
object Category {

  /**
   * Entity type for creating categories
   * @param name Category name
   * @param defaultAssignee Default assigned user
   */
  case class New(
    name: String,
    defaultAssignee: Option[UserIdLike] = None
  )

  /**
   * Category update entity type
   */
  case class Update(
    name: Option[String] = None,
    project: Option[Option[ProjectIdLike]] = None,
    defaultAssignee: Option[Option[UserIdLike]] = None
  )

}
