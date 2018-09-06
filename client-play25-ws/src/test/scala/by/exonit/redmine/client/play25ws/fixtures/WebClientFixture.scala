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

package by.exonit.redmine.client.play25ws.fixtures

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import by.exonit.redmine.client.play25ws.Play25WSWebClient
import org.scalatest.{BeforeAndAfterAll, Suite}
import play.api.libs.ws.ahc.{AhcWSClient, AhcWSClientConfig}

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.util.control.NonFatal

trait WebClientFixture extends BeforeAndAfterAll {
  this: Suite =>

  implicit val as: ActorSystem = ActorSystem("play25-ws-test-client")
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  val wsClient = AhcWSClient(AhcWSClientConfig(maxRequestRetry = 0))(materializer)
  val webClient: Play25WSWebClient = new Play25WSWebClient(wsClient)(materializer)

  override protected def afterAll(): Unit = {
    try {
      webClient.close()
    } catch {
      case NonFatal(_) =>
      case ex: Throwable => throw ex
    }
    try {
      val terminationFuture = as.terminate()
      Await.ready(terminationFuture, 10.seconds)
    } catch {
      case NonFatal(_) =>
      case ex: Throwable => throw ex
    }
    super.afterAll()
  }
}
