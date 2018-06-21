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

package by.exonit.redmine.client.managers.impl

import java.io.{File, InputStream, OutputStream}

import by.exonit.redmine.client._
import by.exonit.redmine.client.managers.WebClient.RequestDSL
import by.exonit.redmine.client.managers.{AttachmentManager, RequestManager}

import cats.effect.IO

class AttachmentManagerImpl(requestManager: RequestManager) extends AttachmentManager {

  val uploadRequest: RequestDSL.Request[Unit] = for {
    _ <- requestManager.baseRequest
    _ <- RequestDSL.setMethod("POST")
    _ <- RequestDSL.addSegments("uploads.json")
  } yield ()

  def upload(file: File): IO[Upload] = IO.suspend {
    requestManager.postFileWithResponse[Upload](uploadRequest, file, "upload")
  }

  def upload(stream: InputStream): IO[Upload] = IO.suspend {
    requestManager.postStreamWithResponse[Upload](uploadRequest, () => stream, "upload")
  }

  def upload(bytes: Array[Byte]): IO[Upload] = IO.suspend {
    requestManager.postBytesWithResponse[Upload](uploadRequest, bytes, "upload")
  }

  def attachToIssue(
    issue      : IssueIdLike,
    upload     : Upload,
    filename   : String,
    contentType: String,
    description: Option[String]): IO[Unit] = IO.suspend {
    val issueUpload = Issue.Upload(upload.token, filename, description, contentType)
    attachToIssue(issue, issueUpload)
  }

  def attachToIssue(issue: IssueIdLike, uploads: Issue.Upload*): IO[Unit] = IO.suspend {
    val request = for {
      _ <- requestManager.baseRequest
      _ <- RequestDSL.addSegments("issues", s"${issue.id}.json")
    } yield ()
    val issueUpdate = new Issue.Update(uploads = Some(uploads.toSet))
    requestManager.putEntity(request, "issue", issueUpdate)
  }

  def getAttachment(id: AttachmentIdLike): IO[Attachment] = IO.suspend {
    val request = for {
      _ <- requestManager.baseRequest
      _ <- RequestDSL.addSegments("attachments", s"${id.id}.json")
    } yield ()
    requestManager.getEntity[Attachment](request, "attachment")
  }

  /**
    * Downloads attachment to byte array
    *
    * @param attachment Attachment to download
    * @return [[cats.effect.IO IO]] with downloaded attachment data in a byte array
    */
  def downloadAttachment(attachment: Attachment): IO[Array[Byte]] = IO.suspend {
    val request = for {
      _ <- RequestDSL.setUrl(attachment.contentUrl)
      _ <- requestManager.authenticateRequest()
    } yield ()
    requestManager.downloadToByteArray(request)
  }

  def downloadAttachmentStreaming(
    attachment: Attachment,
    outputStreamProvider: () => OutputStream
  ): IO[IO[Unit]] = IO.suspend {
    val request = for {
      _ <- RequestDSL.setUrl(attachment.contentUrl)
      _ <- requestManager.authenticateRequest()
    } yield ()
    requestManager.downloadToStream(request, outputStreamProvider)
  }

  /**
    * Deletes existing attachment
    *
    * @param id Attachment to delete
    * @return Completion [[cats.effect.IO IO]]
    */
  def deleteAttachment(id: AttachmentIdLike): IO[Unit] = IO.suspend {
    val request = for {
      _ <- requestManager.baseRequest
      _ <- RequestDSL.addSegments("attachments", s"${id.id}.json")
    } yield ()
    requestManager.deleteEntity(request)
  }
}
