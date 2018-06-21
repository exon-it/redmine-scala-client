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
 * Redmine project manager
 */
trait ProjectManager {
  /**
   * Creates a project
   * @param project Project to create
   * @return Created project
   */
  def createProject(project: Project.New): IO[Project]

  /**
   * Returns project list
   * @note Only projects, visible to current user are returned
   * @param params Custom request parameters (filtering, etc.)
   * @return Project list
   */
  def getProjects(params: (String, String)*): IO[PagedList[Project]]

  /**
   * Returns project details by its number
   * @param id Project number
   * @param includes Project additional data includes
   * @return Project
   */
  def getProject(id: ProjectIdLike, includes: Project.Include*): IO[Project]

  /**
   * Returns project details by its identifier
   * @param key Project identifier
   * @param includes Project additional data includes
   * @return Project
   */
  def getProjectByKey(key: String, includes: Project.Include*): IO[Project]

  /**
   * Updates a project
   * @param id Project number
   * @param update Update data
   * @return Operation IO
   */
  def updateProject(id: ProjectIdLike, update: Project.Update): IO[Unit]

  /**
   * Removes a project
   * @param id Project number
   * @return Operation IO
   */
  def deleteProject(id: ProjectIdLike): IO[Unit]

  /**
   * Returns versions, available in a project
   * @note Parent project versions are also returned
   *       See `[[by.exonit.redmine.client.Version Version]].project` for project where version is defined
   * @param project Project number
   * @param params Custom request parameters (filtering, etc.)
   * @return Version list
   */
  def getVersions(project: ProjectIdLike, params: (String, String)*): IO[PagedList[Version]]

  /**
   * Returns version details by version ID
   * @param id Version ID
   * @return Version details
   */
  def getVersion(id: VersionIdLike): IO[Version]

  /**
   * Creates version in a project
   * @param project Project number
   * @param version New version
   * @return Created version
   */
  def createVersion(project: ProjectIdLike, version: Version.New): IO[Version]

  /**
   * Updates version
   * @param id Version ID
   * @param update Update date
   * @return Operation IO
   */
  def updateVersion(id: VersionIdLike, update: Version.Update): IO[Unit]

  /**
   * Removes version
   * @param id Version ID
   * @return Operation IO
   */
  def deleteVersion(id: VersionIdLike): IO[Unit]

  /** Get news across all projects
    */
  def getAllNews(): IO[PagedList[News]]

  /**
   * Returns project news
   * @param project Project number
   * @return News list
   */
  def getNews(project: ProjectIdLike): IO[PagedList[News]]
}
