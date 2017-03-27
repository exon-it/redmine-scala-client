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

import org.joda.time.DateTime

/**
 * Attachment ID trait for identifiable attachment entity types
 */
trait AttachmentIdLike extends Identifiable[BigInt]

/**
 * Attachment ID entity type
 * @param id Attachment ID
 */
case class AttachmentId(id: BigInt) extends AttachmentIdLike

/**
 * Attachment entity type
 * @param id ID
 * @param createdOn Created-on timestamp
 * @param fileName File name
 * @param fileSize File size
 * @param contentType MIME content type
 * @param contentUrl Download url
 * @param description Description
 * @param author Author
 */
case class Attachment(id: BigInt,
                      createdOn: DateTime,
                      fileName: String,
                      fileSize: BigInt,
                      contentType: Option[String],
                      contentUrl: String,
                      description: Option[String],
                      author: Option[UserLink])
  extends AttachmentIdLike
