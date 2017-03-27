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

import by.exonit.redmine.client.managers.WebClient.RequestDSL
import by.exonit.redmine.client.managers._
import by.exonit.redmine.client.managers.WebClient.RequestDSL.Request

class RedmineManagerImpl(client: WebClient, baseRequest: Request[Unit], authenticator: Request[Unit]) extends RedmineManager {
  lazy val requestManager    : RequestManager     = new RequestManagerImpl(client, baseRequest, requestAuthenticator = authenticator)
  lazy val issueManager      : IssueManager       = new IssueManagerImpl(requestManager)
  lazy val projectManager    : ProjectManager     = new ProjectManagerImpl(requestManager)
  lazy val attachmentManger  : AttachmentManager  = new AttachmentManagerImpl(requestManager)
  lazy val wikiManager       : WikiManager        = new WikiManagerImpl(requestManager)
  lazy val userManager       : UserManager        = new UserManagerImpl(requestManager)
  lazy val customFieldManager: CustomFieldManager = new CustomFieldManagerImpl(requestManager)
  lazy val membershipManager : MembershipManager  = new MembershipManagerImpl(requestManager)
  lazy val timeEntryManager  : TimeEntryManager   = new TimeEntryManagerImpl(requestManager)

  /**
   * Creates a [[by.exonit.redmine.client.managers.RedmineManager RedmineManager]]
   * for performing operations as another Redmine user
   * @note Only usable for manager authenticated as an administrator user
   * @param login Impersonated user login
   * @return Redmine manager for impersonated user
   */
  def getImpersonatedManager(login: String): RedmineManager = {
    val impersonatedBaseRequest = for {
      _ <- requestManager.baseRequest
      _ <- RequestDSL.setHeaders("X-Redmine-Switch-User" -> login)
    } yield ()
    new RedmineManagerImpl(client, impersonatedBaseRequest, authenticator)
  }
}
