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

import by.exonit.redmine.client._
import by.exonit.redmine.client.managers.{RequestManager, TimeEntryManager}
import by.exonit.redmine.client.managers.WebClient.RequestDSL
import monix.eval.Task

class TimeEntryManagerImpl(requestManager: RequestManager) extends TimeEntryManager {

  def getTimeEntriesForIssue(issue: IssueIdLike, activity: Option[ActivityIdLike]): Task[PagedList[TimeEntry]] = {
    getTimeEntries(("issue_id" -> issue.id.toString) +: activity.map(a => "activity_id" -> a.id.toString).toSeq: _*)
  }

  def getTimeEntriesForProject(project: ProjectIdLike, activity: Option[ActivityIdLike]): Task[PagedList[TimeEntry]] = {
    getTimeEntries(("project_id" -> project.id.toString) +: activity.map(a => "activity_id" -> a.id.toString).toSeq: _*)
  }

  def getTimeEntries(params: (String, String)*): Task[PagedList[TimeEntry]] = {
    val request = for {
      _ <- requestManager.baseRequest
      _ <- RequestDSL.addSegments("time_entries.json")
      _ <- RequestDSL.addQueries(params: _*)
    } yield ()
    requestManager.getEntityPagedList[TimeEntry](request, "time_entries")
  }

  def getTimeEntry(id: TimeEntryIdLike): Task[TimeEntry] = {
    val request = for {
      _ <- requestManager.baseRequest
      _ <- RequestDSL.addSegments("time_entries", s"${id.id}.json")
    } yield ()
    requestManager.getEntity[TimeEntry](request, "time_entry")
  }

  def createTimeEntry(timeEntry: TimeEntry.New): Task[TimeEntry] = {
    val request = for {
      _ <- requestManager.baseRequest
      _ <- RequestDSL.addSegments("time_entries.json")
    } yield ()
    requestManager.postEntityWithResponse[TimeEntry.New, TimeEntry](request, "time_entry", timeEntry, "time_entry")
  }

  def updateTimeEntry(id: TimeEntryIdLike, update: TimeEntry.Update): Task[Unit] = {
    val request = for {
      _ <- requestManager.baseRequest
      _ <- RequestDSL.addSegments("time_entries", s"${id.id}.json")
    } yield ()
    requestManager.putEntity(request, "time_entry", update)
  }


  def deleteTimeEntry(id: TimeEntryIdLike): Task[Unit] = {
    val request = for {
      _ <- requestManager.baseRequest
      _ <- RequestDSL.addSegments("time_entries", s"${id.id}.json")
    } yield ()
    requestManager.deleteEntity(request)
  }

  def getTimeEntryActivities(): Task[PagedList[Activity]] = {
    val request = for {
      _ <- requestManager.baseRequest
      _ <- RequestDSL.addSegments("enumerations", "time_entry_activities.json")
    } yield ()
    requestManager.getEntityPagedList[Activity](request, "time_entry_activities")
  }

}
