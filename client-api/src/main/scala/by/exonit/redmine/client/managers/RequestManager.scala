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

package by.exonit.redmine.client.managers

import java.io.{File, InputStream, OutputStream}

import by.exonit.redmine.client.PagedList
import by.exonit.redmine.client.managers.WebClient.RequestDSL.Request
import monix.eval.Task
import org.json4s.Formats

trait RequestManager {

  /** Unauthenticated request to Redmine root */
  def baseRequest: Request[Unit]

  /**
    * HTTP client, used to send requests
    *
    * @return HTTP client
    */
  def client: WebClient

  /**
    * JSON serialization/deserialization formats
    *
    * @return JSON serialization/deserialization formats
    */
  def formats: Formats

  /**
    * Sets JSON serialization/deserialization formats
    *
    * @param formats New serialization/deserialization formats
    */
  def formats_=(formats: Formats): Unit

  /**
    * Adds Redmine REST API authentication data to the request
    *
    * @return Request with authentication data
    */
  def authenticateRequest(): Request[Unit]

  def getEntityPagedList[T](request: Request[Unit], listName: String)(implicit mf: Manifest[T]): Task[PagedList[T]]

  def getEntityPagedList[T](request: Request[Unit], listName: String, offset: BigInt, limit: BigInt)(implicit mf: Manifest[T]): Task[PagedList[T]]

  def getEntity[T](request: Request[Unit], entityName: String)(implicit mf: Manifest[T]): Task[T]

  def postEntity[T](request: Request[Unit], entityName: String, entity: T)(implicit mf: Manifest[T]): Task[Unit]

  def postEntityWithResponse[T, TResponse](
    request: Request[Unit],
    entityName: String,
    entity: T,
    responseEntityName: String)
    (implicit mf1: Manifest[T], mf2: Manifest[TResponse]): Task[TResponse]

  def postFileWithResponse[TResponse](request: Request[Unit], file: File, responseEntityName: String)
    (implicit mf: Manifest[TResponse]): Task[TResponse]

  def postStreamWithResponse[TResponse](request: Request[Unit], streamProvider: () => InputStream, responseEntityName: String)
    (implicit mf: Manifest[TResponse]): Task[TResponse]

  def postBytesWithResponse[TResponse](request: Request[Unit], bytes: Array[Byte], responseEntityName: String)
    (implicit mf: Manifest[TResponse]): Task[TResponse]

  def putEntity[T](request: Request[Unit], entityName: String, entity: T)
    (implicit mf: Manifest[T]): Task[Unit]

  def putEntityWithResponse[T, TResponse](
    request: Request[Unit],
    entityName: String,
    entity: T,
    responseEntityName: String)
    (implicit mf1: Manifest[T], mf2: Manifest[TResponse]): Task[TResponse]

  def deleteEntity(request: Request[Unit]): Task[Unit]

  def downloadToByteArray(request: Request[Unit]): Task[Array[Byte]]

  def downloadToStream(request: Request[Unit], outputStreamProvider: () => OutputStream): Task[Task[Unit]]

  def copy(
    copyClient: WebClient = client,
    copyBaseRequest: Request[Unit] = baseRequest,
    copyFormats: Formats = formats): RequestManager
}
