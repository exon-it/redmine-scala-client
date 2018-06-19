package by.exonit.redmine.client

import enumeratum._

import scala.collection.immutable

sealed abstract class SearchAttachmentScope(override val entryName: String) extends EnumEntry
object SearchAttachmentScope extends Enum[SearchAttachmentScope] {
  val values: immutable.IndexedSeq[SearchAttachmentScope] = findValues

  case object DoNotSearch extends SearchAttachmentScope("0")
  case object Files extends SearchAttachmentScope("only")
  case object FilesAndDescriptions extends SearchAttachmentScope("1")
}
