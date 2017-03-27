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

import by.exonit.redmine.client.IssueRelation._
import scala.collection.immutable._

trait IssueRelationIdLike extends Identifiable[BigInt]

case class IssueRelationId(id: BigInt) extends IssueRelationIdLike

object IssueRelation {
  sealed abstract class RelationType(val token: String)

  object RelationType {
    case object Relates extends RelationType("relates")
    case object Precedes extends RelationType("precedes")
    case object Follows extends RelationType("follows")
    case object CopiedFrom extends RelationType("copied_from")
    case object CopiedTo extends RelationType("copied_to")
    case object Blocks extends RelationType("blocks")
    case object Blocked extends RelationType("blocked")
    case object Duplicates extends RelationType("duplicates")
    case object Duplicated extends RelationType("duplicated")

    case class Custom(override val token: String) extends RelationType(token)

    lazy val predefined: Seq[RelationType] = Seq(
      Relates,
      Precedes, Follows, CopiedFrom, CopiedTo,
      Blocks, Blocked, Duplicates, Duplicated)

    def apply(token: String): RelationType =
      predefined.find(_.token.equalsIgnoreCase(token)).getOrElse(Custom(token))
    def unapply(rt: RelationType): Option[String] = Some(rt.token)
  }

  case class New(issueTo: IssueIdLike) {
    val relationType = new FluentSettableField[RelationType, New](this)
    val delayDays = new FluentSettableField[BigInt, New](this)
  }
}

case class IssueRelation(
  id: BigInt,
  fromIssue: IssueId,
  toIssue: IssueId,
  relationType: RelationType,
  delay: Option[Int]) extends IssueRelationIdLike
