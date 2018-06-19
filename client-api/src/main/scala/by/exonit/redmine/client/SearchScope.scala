package by.exonit.redmine.client

import enumeratum.EnumEntry.Snakecase
import enumeratum._

import scala.collection.immutable

sealed abstract class SearchScope extends EnumEntry with Snakecase

object SearchScope extends Enum[SearchScope] {
  val values: immutable.IndexedSeq[SearchScope] = findValues

  case object All extends SearchScope

  case object MyProjects extends SearchScope

  case object Subprojects extends SearchScope
}