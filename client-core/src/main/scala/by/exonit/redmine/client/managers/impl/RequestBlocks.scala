package by.exonit.redmine.client.managers.impl

import by.exonit.redmine.client.Tokenized
import by.exonit.redmine.client.managers.WebClient.RequestDSL
import by.exonit.redmine.client.managers.WebClient.RequestDSL.Request
import cats.data.NonEmptyList

import scala.collection.immutable.Seq

object RequestBlocks {

  /** Adds includes query parameter for entity list queries
    * @param includes Include list
    * @tparam T Tokenized include enumeration type
    * @return Request DSL command to add "includes" query parameter
    */
  def includes[T <: Tokenized](includes: Iterable[T]): Request[Unit] = {
    val includeTokensOpt = NonEmptyList.fromList(includes.map(_.token).toList)
    val includeValueOpt = includeTokensOpt.map(list => list.reduceLeft(_ + "," + _))
    val addQueries = includeValueOpt.map {v => Seq("include" -> v)}.getOrElse(Seq.empty)
    RequestDSL.addQueries(addQueries: _*)
  }
}
