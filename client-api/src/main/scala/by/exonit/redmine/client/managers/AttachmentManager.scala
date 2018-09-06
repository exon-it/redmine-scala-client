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

package by.exonit.redmine.client.managers

import java.io.{File, InputStream, OutputStream}

import by.exonit.redmine.client.{Attachment, AttachmentIdLike, IssueIdLike, Upload}
import cats.effect.IO

trait AttachmentManager {
  /**
   * Upload a file to Redmine
   * @param file File to upload
   * @return Upload data, containing attachment token
   */
  def upload(file: File): IO[Upload]

  /**
   * Upload data from a stream to Redmine
   * @param stream Stream to read data from
   * @return Upload data, containing attachment token
   */
  def upload(stream: InputStream): IO[Upload]

  /**
   * Uploads byte array to Redmine
   * @param bytes Data array
   * @return Upload data, containing attachment token
   */
  def upload(bytes: Array[Byte]): IO[Upload]

  /**
   * Attaches an upload to issue
   * @param issue Issue to attach an upload
   * @param upload Upload
   * @param filename Attachment file name
   * @param contentType Attachment content type
   * @param description Optional attachment description
   * @return IO for completed operation
   */
  def attachToIssue(issue: IssueIdLike,
                    upload: Upload,
                    filename: String,
                    contentType: String,
                    description: Option[String] = None): IO[Unit]

  /**
   * Returns attachment data by its ID
   * @param id Attachment ID
   * @return Attachment data
   */
  def getAttachment(id: AttachmentIdLike): IO[Attachment]

  /**
   * Downloads attachment to byte array
   * @param attachment Attachment to download
   * @return [[cats.effect.IO IO]] with downloaded attachment data in a byte array
   */
  def downloadAttachment(attachment: Attachment): IO[Array[Byte]]

  /**
   * Downloads attachment and writes it to provided [[java.io.OutputStream output stream]]
   * @param attachment Attachment to download
    *
    * Returned [[cats.effect.IO IO]] completes on data start, inner one completes on data end
   * @return Completion [[cats.effect.IO IO]]
   */
  def downloadAttachmentStreaming(attachment: Attachment, outputStreamProvider: () => OutputStream): IO[IO[Unit]]

  /**
    * Deletes existing attachment
    * @param id Attachment to delete
    * @return Completion [[cats.effect.IO IO]]
    */
  def deleteAttachment(id: AttachmentIdLike): IO[Unit]
}
