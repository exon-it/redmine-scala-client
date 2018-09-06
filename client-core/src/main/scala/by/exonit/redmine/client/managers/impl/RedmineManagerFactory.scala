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

import by.exonit.redmine.client.managers.WebClient.RequestDSL
import by.exonit.redmine.client.managers.WebClient.RequestDSL.AuthenticationMethod
import by.exonit.redmine.client.managers.{RedmineManager, WebClient}
import cats.effect.{IO, Timer}

import scala.collection.immutable._

object RedmineManagerFactory {
  def apply(implicit client: WebClient): RedmineManagerFactory = new RedmineManagerFactory(client)
}

/**
  * Factory for [[by.exonit.redmine.client.managers.RedmineManager RedmineManager]] instances
  */
class RedmineManagerFactory(val client: WebClient) {
  /**
    * Returns a [[by.exonit.redmine.client.managers.RedmineManager RedmineManager]] which does not authenticate
    * it's requests
    *
    * @param baseUrl Redmine REST API base URL
    * @return [[by.exonit.redmine.client.managers.RedmineManager RedmineManager]] instance
    */
  def createUnauthenticated(baseUrl: String)(implicit timer: Timer[IO]): RedmineManager = {
    val baseRequest = RequestDSL.setUrl(baseUrl)
    new RedmineManagerImpl(client, baseRequest, RequestDSL.noOp())
  }

  /**
    * Returns a [[by.exonit.redmine.client.managers.RedmineManager RedmineManager]]
    * which uses HTTP Basic authentication for requests
    *
    * @param baseUrl  Redmine REST API base URL
    * @param login    Authentication user
    * @param password Authentication password
    * @return [[by.exonit.redmine.client.managers.RedmineManager RedmineManager]] instance
    */
  def createWithUserAuth(
    baseUrl: String, login: String, password: IndexedSeq[Char])(implicit timer: Timer[IO]): RedmineManager = {
    val baseRequest = for {
      _ <- RequestDSL.setUrl(baseUrl)
    } yield ()
    val authenticator = RequestDSL.setAuth(Some(AuthenticationMethod.Basic(login, password)))
    new RedmineManagerImpl(client, baseRequest, authenticator)
  }

  /**
    * Returns a [[by.exonit.redmine.client.managers.RedmineManager RedmineManager]]
    * which uses API key authentication
    *
    * @param baseUrl Redmine REST API base URL
    * @param apiKey  API key
    * @return [[by.exonit.redmine.client.managers.RedmineManager RedmineManager]] instance
    */
  def createWithApiKey(
    baseUrl: String, apiKey: String)(implicit timer: Timer[IO]): RedmineManager = {
    val baseRequest = for {
      _ <- RequestDSL.setUrl(baseUrl)
    } yield ()
    val authenticator = RequestDSL.addQueries(WebClient.Constants.ApiKeyQueryParameterName -> apiKey)
    new RedmineManagerImpl(client, baseRequest, authenticator)
  }
}
