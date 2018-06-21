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

import by.exonit.redmine.client.{CustomFieldDefinition, PagedList}
import by.exonit.redmine.client.managers.{CustomFieldManager, RequestManager}
import by.exonit.redmine.client.managers.WebClient.RequestDSL
import cats.effect.IO

class CustomFieldManagerImpl(requestManager: RequestManager) extends CustomFieldManager {

  def getCustomFieldDefinitions(): IO[PagedList[CustomFieldDefinition]] = IO.suspend {
    val request = for {
      _ <- requestManager.baseRequest
      _ <- RequestDSL.addSegments("custom_fields.json")
    } yield ()
    requestManager.getEntityPagedList[CustomFieldDefinition](request, "custom_fields")
  }
}
