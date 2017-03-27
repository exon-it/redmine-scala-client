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

package by.exonit.redmine.client.managers.impl

import java.io.{ByteArrayInputStream, File, InputStream, OutputStream}

import by.exonit.redmine.client.{PagedList, ResponseStatusException}
import by.exonit.redmine.client.managers.{RequestManager, WebClient}
import by.exonit.redmine.client.managers.WebClient.{RequestDSL, ResponseDSL, StreamingResponseDSL}
import by.exonit.redmine.client.managers.WebClient.RequestDSL.Request
import by.exonit.redmine.client.managers.WebClient.ResponseDSL.Response
import by.exonit.redmine.client.managers.WebClient.StreamingResponseDSL.StreamingResponse
import by.exonit.redmine.client.serialization.Serializers
import monix.eval.Task
import org.json4s.{Extraction, Formats, JValue, NoTypeHints, _}
import org.json4s.jackson.Serialization
import org.json4s.jackson.JsonMethods._

import scala.collection.immutable._

object RequestManagerImpl {
  lazy val defaultFormats: Formats = Serialization.formats(NoTypeHints).withBigDecimal.skippingEmptyValues ++
    Serializers.all
}

class RequestManagerImpl(
  val client: WebClient,
  val baseRequest: Request[Unit],
  baseFormats: Formats = RequestManagerImpl.defaultFormats,
  requestAuthenticator: => Request[Unit]
)
  extends RequestManager {

  protected var _formats: Formats = baseFormats

  def formats: Formats = _formats

  def formats_=(f: Formats): Unit = {
    _formats = f
  }

  def getEntityPagedList[T](request: Request[Unit], listName: String)
    (implicit mf: Manifest[T]): Task[PagedList[T]] =
    getEntityPagedList(request, listName, BigInt(0), Constants.DefaultObjectsPerPage)

  def getEntityPagedList[T](request: Request[Unit], listName: String, offset: BigInt, limit: BigInt)
    (implicit mf: Manifest[T]): Task[PagedList[T]] = Task.defer {
    implicit val implicitFormats = formats

    def getPartialResult(offset: BigInt, limit: BigInt): Task[PagedList[T]] = Task.defer {
      val subRequest = for {
        _ <- request
        _ <- RequestDSL.setMethod("GET")
        _ <- RequestDSL.addQueries(
          "limit" -> limit.toString,
          "offset" -> offset.toString)
        _ <- authenticateRequest()
      } yield ()
      clientRequestAsJSON(subRequest) map {response =>
        val items = (response \ listName).extract[List[T]]
        val total = (response \ "total_count").extractOrElse[BigInt](BigInt(0))
        val previous = if (offset > 0) {
          Some(getPartialResult(BigInt(0).max(offset - limit), limit))
        } else None
        val next = if (total - offset - limit > 0) {
          Some(getPartialResult(offset + limit, limit))
        } else None
        val allItemsTask = if (offset == 0 && items.size >= total) {
          // Short-circuit if all items are already loaded
          Task.now(items)
        } else {
          // If not short-circuited - have to build item list from the first page
          getPartialResult(0, limit) flatMap {pr =>
            pr.next match {
              case Some(task) => task map {pr2 => pr.items ::: pr2.items}
              case None => Task.now(pr.items)
            }
          }
        }
        PagedList(items, total, offset, allItemsTask, next, previous)
      }
    }

    getPartialResult(offset, limit)
  }

  def getEntity[T](request: Request[Unit], entityName: String)(implicit mf: Manifest[T]): Task[T] = {
    implicit val implicitFormats = formats
    val subRequest = for {
      _ <- request
      _ <- RequestDSL.setMethod("GET")
      _ <- authenticateRequest()
    } yield ()

    clientRequestAsJSON(subRequest) map {j => (j \ entityName).extract[T]}
  }

  def postEntity[T](request: Request[Unit], entityName: String, entity: T)
    (implicit mf: Manifest[T]): Task[Unit] = {
    val subRequest = for {
      _ <- request
      _ <- RequestDSL.setMethod("POST")
      _ <- RequestDSL.setContentType(Constants.JsonContentType, Constants.Charset)
      bodyString = extractJsonEntityToString(entityName, entity)
      bodyBytes = bodyString.getBytes(Constants.Charset)
      _ <- RequestDSL.setBody(RequestDSL.Body.InMemoryByteBody(bodyBytes))
      _ <- authenticateRequest()
    } yield ()
    clientRequest(subRequest)
  }

  def postEntityWithResponse[T, TResponse](
    request: Request[Unit],
    entityName: String,
    entity: T,
    responseEntityName: String
  )
    (implicit mf1: Manifest[T], mf2: Manifest[TResponse]): Task[TResponse] = {
    implicit val implicitFormats = formats
    val subRequest = for {
      _ <- request
      _ <- RequestDSL.setMethod("POST")
      _ <- RequestDSL.setContentType(Constants.JsonContentType, Constants.Charset)
      bodyString = extractJsonEntityToString(entityName, entity)
      bodyBytes = bodyString.getBytes(Constants.Charset)
      _ <- RequestDSL.setBody(RequestDSL.Body.InMemoryByteBody(bodyBytes))
      _ <- authenticateRequest()
    } yield ()
    clientRequestAsJSON(subRequest) map {j => (j \ responseEntityName).extract[TResponse]}
  }

  def postFileWithResponse[TResponse](request: Request[Unit], file: File, responseEntityName: String)
    (implicit mf: Manifest[TResponse]): Task[TResponse] = {
    implicit val implicitFormats = formats
    val subRequest = for {
      _ <- request
      _ <- RequestDSL.setMethod("POST")
      _ <- RequestDSL.setContentType(Constants.UploadContentType)
      _ <- RequestDSL.setBody(RequestDSL.Body.FileBody(file))
      _ <- authenticateRequest()
    } yield ()
    clientRequestAsJSON(subRequest) map {j => (j \ responseEntityName).extract[TResponse]}
  }

  def postStreamWithResponse[TResponse](request: Request[Unit], stream: () => InputStream, responseEntityName: String)
    (implicit mf: Manifest[TResponse]): Task[TResponse] = {
    implicit val implicitFormats = formats
    val subRequest = for {
      _ <- request
      _ <- RequestDSL.setMethod("POST")
      _ <- RequestDSL.setContentType(Constants.UploadContentType)
      _ <- RequestDSL.setBody(RequestDSL.Body.StreamedBody(stream))
      _ <- authenticateRequest()
    } yield ()
    clientRequestAsJSON(subRequest) map {j => (j \ responseEntityName).extract[TResponse]}
  }

  def postBytesWithResponse[TResponse](request: Request[Unit], bytes: Array[Byte], responseEntityName: String)
    (implicit mf: Manifest[TResponse]): Task[TResponse] = {
    implicit val implicitFormats = formats
    val subRequest = for {
      _ <- request
      _ <- RequestDSL.setMethod("POST")
      _ <- RequestDSL.setContentType(Constants.UploadContentType)
      _ <- RequestDSL.setBody(RequestDSL.Body.InMemoryByteBody(bytes))
      _ <- authenticateRequest()
    } yield ()
    clientRequestAsJSON(subRequest) map {j => (j \ responseEntityName).extract[TResponse]}
  }

  def putEntity[T](request: Request[Unit], entityName: String, entity: T)
    (implicit mf: Manifest[T]): Task[Unit] = {
    implicit val implicitFormats = formats
    val subRequest = for {
      _ <- request
      _ <- RequestDSL.setMethod("PUT")
      _ <- RequestDSL.setContentType(Constants.JsonContentType, Constants.Charset)
      bodyString = extractJsonEntityToString(entityName, entity)
      bodyBytes = bodyString.getBytes(Constants.Charset)
      _ <- RequestDSL.setBody(RequestDSL.Body.InMemoryByteBody(bodyBytes))
      _ <- authenticateRequest()
    } yield ()
    clientRequest(subRequest)
  }

  def putEntityWithResponse[T, TResponse](
    request: Request[Unit],
    entityName: String,
    entity: T,
    responseEntityName: String
  )
    (implicit mf1: Manifest[T], mf2: Manifest[TResponse]): Task[TResponse] = {
    implicit val implicitFormats = formats
    val subRequest = for {
      _ <- request
      _ <- RequestDSL.setMethod("PUT")
      _ <- RequestDSL.setContentType(Constants.JsonContentType, Constants.Charset)
      bodyString = extractJsonEntityToString(entityName, entity)
      bodyBytes = bodyString.getBytes(Constants.Charset)
      _ <- RequestDSL.setBody(RequestDSL.Body.InMemoryByteBody(bodyBytes))
      _ <- authenticateRequest()
    } yield ()
    clientRequestAsJSON(subRequest) map {j => (j \ responseEntityName).extract[TResponse]}
  }

  def deleteEntity(request: Request[Unit]): Task[Unit] = {
    val subRequest = for {
      _ <- request
      _ <- RequestDSL.setMethod("DELETE")
      _ <- authenticateRequest()
    } yield ()
    clientRequest(subRequest)
  }

  def downloadToByteArray(request: Request[Unit]): Task[Array[Byte]] = {
    val responseHandler = for {
      _ <- checkResponseOk()
      body <- ResponseDSL.getBodyAsBytes
    } yield body
    client.execute(request, responseHandler)
  }

  def downloadToStream(request: Request[Unit], outputStreamProvider: () => OutputStream): Task[Task[Unit]] = {
    val responseHandler = for {
      _ <- checkStreamingResponseOk()
      body <- StreamingResponseDSL.getBodyStream(outputStreamProvider)
    } yield body
    client.executeStreaming(request, responseHandler)
  }

  /**
    * Adds Redmine REST API authentication data to the request
    *
    * @return Request with authentication data
    */
  override def authenticateRequest(): Request[Unit] = requestAuthenticator

  protected def extractJsonEntityToString[T](entityName: String, entity: T): String = {
    implicit val implicitFormats = formats
    compact(JObject(entityName -> Extraction.decompose(entity)))
  }

  protected def clientRequestAsJSON(req: Request[Unit]): Task[JValue] = {
    val responseHandler = for {
      _ <- checkResponseOk()
      body <- ResponseDSL.getBodyAsBytes
      bodyStream = new ByteArrayInputStream(body)
      json = parse(bodyStream)
    } yield json
    client.execute(req, responseHandler)
  }

  protected def clientRequest(req: Request[Unit]): Task[Unit] = {
    val responseHandler = for {
      _ <- checkResponseOk()
    } yield ()
    client.execute(req, responseHandler)
  }

  protected def clientRequestAsString(req: Request[Unit]): Task[String] = {
    val responseHandler = for {
      _ <- checkResponseOk()
      body <- ResponseDSL.getBodyAsString
    } yield body
    client.execute(req, responseHandler)
  }

  protected def checkResponseOk(): Response[Unit] =
    for {
      status <- ResponseDSL.getStatusCode
      statusText <- ResponseDSL.getStatusText
    } yield {
      if (status != WebClient.Constants.StatusOk) throw ResponseStatusException(status, Some(statusText))
    }

  protected def checkStreamingResponseOk(): StreamingResponse[Unit] =
    for {
      status <- StreamingResponseDSL.getStatusCode
    } yield {
      if (status != WebClient.Constants.StatusOk) throw ResponseStatusException(status, None)
    }

  def copy(
    copyClient: WebClient,
    copyBaseRequest: Request[Unit],
    copyFormats: Formats
  ): RequestManager =
    new RequestManagerImpl(copyClient, copyBaseRequest, copyFormats, requestAuthenticator)
}
