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

trait RedmineManager {
  def requestManager: RequestManager

  def issueManager: IssueManager

  def projectManager: ProjectManager

  def customFieldManager: CustomFieldManager

  def attachmentManger: AttachmentManager

  def userManager: UserManager

  def wikiManager: WikiManager

  def membershipManager: MembershipManager

  def timeEntryManager: TimeEntryManager

  def documentsManager: DocumentsManager

  def searchManager: SearchManager

  def fileManager: FileManager

  /**
   * Creates a [[by.exonit.redmine.client.managers.RedmineManager RedmineManager]]
   * for performing operations as another Redmine user
   * @note Only usable for manager authenticated as an administrator user
   * @param login Impersonated user login
   * @return Redmine manager for impersonated user
   */
  def getImpersonatedManager(login: String): RedmineManager

}
