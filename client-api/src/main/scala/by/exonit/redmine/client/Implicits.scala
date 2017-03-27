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

package by.exonit.redmine.client

/**
 * Implicits for conversion from base to identifier types for various entities
 */
object Implicits {

  import scala.language.implicitConversions

  implicit def intToActivityId(id: Int): ActivityId = ActivityId(id)

  implicit def intToAttachmentId(id: Int): AttachmentId = AttachmentId(id)

  implicit def intToCategoryId(id: Int): CategoryId = CategoryId(id)

  implicit def intToCustomFieldId(id: Int): CustomFieldId = CustomFieldId(id)

  implicit def intToGroupId(id: Int): GroupId = GroupId(id)

  implicit def intToIssueId(id: Int): IssueId = IssueId(id)

  implicit def intToIssueRelationId(id: Int): IssueRelationId = IssueRelationId(id)

  implicit def intToIssueStatusId(id: Int): IssueStatusId = IssueStatusId(id)

  implicit def intToJournalId(id: Int): JournalId = JournalId(id)

  implicit def intToMembershipId(id: Int): MembershipId = MembershipId(id)

  implicit def intToModuleId(id: Int): ModuleId = ModuleId(id)

  implicit def intToNewsId(id: Int): NewsId = NewsId(id)

  implicit def intToPriorityId(id: Int): PriorityId = PriorityId(id)

  implicit def intToProjectId(id: Int): ProjectId = ProjectId(id)

  implicit def intToRoleId(id: Int): RoleId = RoleId(id)

  implicit def intToSavedQueryId(id: Int): SavedQueryId = SavedQueryId(id)

  implicit def intToTimeEntryId(id: Int): TimeEntryId = TimeEntryId(id)

  implicit def intToTrackerId(id: Int): TrackerId = TrackerId(id)

  implicit def intToUserId(id: Int): UserId = UserId(id)

  implicit def intToVersionId(id: Int): VersionId = VersionId(id)

  implicit def bigIntToActivityId(id: BigInt): ActivityId = ActivityId(id)

  implicit def bigIntToAttachmentId(id: BigInt): AttachmentId = AttachmentId(id)

  implicit def bigIntToCategoryId(id: BigInt): CategoryId = CategoryId(id)

  implicit def bigIntToCustomFieldId(id: BigInt): CustomFieldId = CustomFieldId(id)

  implicit def bigIntToGroupId(id: BigInt): GroupId = GroupId(id)

  implicit def bigIntToIssueId(id: BigInt): IssueId = IssueId(id)

  implicit def bigIntToIssueRelationId(id: BigInt): IssueRelationId = IssueRelationId(id)

  implicit def bigIntToIssueStatusId(id: BigInt): IssueStatusId = IssueStatusId(id)

  implicit def bigIntToJournalId(id: BigInt): JournalId = JournalId(id)

  implicit def bigIntToMembershipId(id: BigInt): MembershipId = MembershipId(id)

  implicit def bigIntToModuleId(id: BigInt): ModuleId = ModuleId(id)

  implicit def bigIntToNewsId(id: BigInt): NewsId = NewsId(id)

  implicit def bigIntToPriorityId(id: BigInt): PriorityId = PriorityId(id)

  implicit def bigIntToProjectId(id: BigInt): ProjectId = ProjectId(id)

  implicit def bigIntToRoleId(id: BigInt): RoleId = RoleId(id)

  implicit def bigIntToSavedQueryId(id: BigInt): SavedQueryId = SavedQueryId(id)

  implicit def bigIntToTimeEntryId(id: BigInt): TimeEntryId = TimeEntryId(id)

  implicit def bigIntToTrackerId(id: BigInt): TrackerId = TrackerId(id)

  implicit def bigIntToUserId(id: BigInt): UserId = UserId(id)

  implicit def bigIntToVersionId(id: BigInt): VersionId = VersionId(id)

  implicit def stringToWikiPageId(id: String): WikiPageId = WikiPageId(id)

}
