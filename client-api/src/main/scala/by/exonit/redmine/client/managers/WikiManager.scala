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

import by.exonit.redmine.client._
import cats.effect.IO

/**
 * Redmine project wiki module manager
 */
trait WikiManager {
  /**
   * Returns basic data of all wiki pages in a specified project
   * @param project Project
   * @return Wiki page list
   */
  def getPages(project: ProjectIdLike): IO[PagedList[WikiPage]]

  /**
   * Returns detail of a wiki page in specified project
   * @param project Project
   * @param page Wiki page identifier
   * @return Wiki page details
   */
  def getPage(project: ProjectIdLike, page: WikiPageIdLike): IO[WikiPageDetails]

  /** Returns details of a wiki page in specified project with optional includes
    * @param project Project ID
    * @param page Page identifier
    * @param includes Included details
    */
  def getPage(project: ProjectIdLike, page: WikiPageIdLike, includes: WikiPage.Include*): IO[WikiPageDetails]

  /** Returns details of a wiki page version in specified project with optional includes
    * @param project Project ID
    * @param page Page identifier
    * @param version Page version number
    * @param includes Included details
    */
  def getPageVersion(project: ProjectIdLike, page: WikiPageIdLike, version: Int, includes: WikiPage.Include*): IO[WikiPageDetails]

  /**
   * Creates a wiki page in specified project
   *
   * @note Create and update methods are the same for Redmine REST API
   * @param project Project
   * @param page Page to create
   * @return Created wiki page details
   */
  def createPage(project: ProjectIdLike, page: WikiPage.New): IO[WikiPageDetails]

  /**
   * Updates wiki page in specified project
   * @param project Project
   * @param id Wiki page identifier
   * @param page Page update
   * @return Operation result IO
   */
  def updatePage(project: ProjectIdLike, id: WikiPageIdLike, page: WikiPage.Update): IO[Unit]

  /**
   * Deletes wiki page from specified project
   * @param project Project
   * @param id Wiki page identifier
   * @return Operation result IO
   */
  def deletePage(project: ProjectIdLike, id: WikiPageIdLike): IO[Unit]
}
