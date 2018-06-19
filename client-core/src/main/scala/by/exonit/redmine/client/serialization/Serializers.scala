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

package by.exonit.redmine.client.serialization

import org.json4s.Serializer

import scala.collection.immutable.Seq

object Serializers {
  lazy val all: Seq[Serializer[_]] =
    IssueSerializers.all ++
      IssueRelationSerializers.all ++
      CustomFieldSerializers.all ++
      ProjectSerializers.all ++
      PrioritySerializers.all ++
      ChangesetSerializers.all ++
      JournalSerializers.all ++
      AttachmentSerializers.all ++
      CategorySerializers.all ++
      GroupSerializers.all ++
      IssueStatusSerializers.all ++
      MembershipSerializers.all ++
      ModuleSerializers.all ++
      NewsSerializers.all ++
      RoleSerializers.all ++
      SavedQuerySerializers.all ++
      ActivitySerializers.all ++
      TimeEntrySerializers.all ++
      TrackerSerializers.all ++
      UserSerializers.all ++
      VersionSerializers.all ++
      WikiSerializers.all ++
      DocumentCategorySerializers.all ++
      SearchSerializers.all
}
