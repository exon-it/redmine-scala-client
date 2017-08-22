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

import by.exonit.redmine.client._
import monix.eval.Task

/**
  * Created by keritaf on 04.12.16.
  */
//noinspection AccessorLikeMethodIsEmptyParen
trait TimeEntryManager {
  def getTimeEntries(params: (String, String)*): Task[PagedList[TimeEntry]]

  def getTimeEntriesForProject(project: ProjectIdLike, activity: Option[ActivityIdLike] = None): Task[PagedList[TimeEntry]]

  def getTimeEntriesForIssue(issue: IssueIdLike, activity: Option[ActivityIdLike] = None): Task[PagedList[TimeEntry]]

  def getTimeEntry(id: TimeEntryIdLike): Task[TimeEntry]

  def createTimeEntry(newTimeEntry: TimeEntry.New): Task[TimeEntry]

  def updateTimeEntry(id: TimeEntryIdLike, update: TimeEntry.Update): Task[Unit]

  def deleteTimeEntry(id: TimeEntryIdLike): Task[Unit]

  def getTimeEntryActivities(): Task[PagedList[Activity]]
}
