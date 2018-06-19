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

import scala.collection.immutable._

/**
 * Activity ID trait for identifiable activity entity types
 */
trait ActivityIdLike extends Identifiable[BigInt]

/**
 * Activity ID type
 * @param id Activity ID
 */
case class ActivityId(id: BigInt) extends ActivityIdLike

/**
 * Activity link
 * Is returned when activity is a field of another entity
 * @param id Activity ID
 * @param name Activity name
 */
case class ActivityLink(id: BigInt, name: String) extends ActivityIdLike

/**
 * Activity
 * @param id Activity ID
 * @param name Activity name
 * @param customFields Custom field values
 */
case class Activity(id: BigInt, name: String, customFields: Option[Set[CustomField]])
  extends ActivityIdLike with OptionalCustomFieldSet
