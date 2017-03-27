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

import org.joda.time.DateTime

/**
 * Changeset entity type
 * @param revision VCS revision
 * @param committedOn Committed-on timestamp
 * @param user Redmine user associated with committer
 * @param comments Revision comments
 */
case class Changeset(
  revision: String,
  committedOn: DateTime,
  user: Option[UserLink],
  comments: Option[String])
