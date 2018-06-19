package by.exonit.redmine.client.serialization

import by.exonit.redmine.client._
import org.json4s._

import scala.collection.immutable._

object SearchSerializers {
  lazy val all = Seq(
    searchResultSerializer
  )

  def deserializeSearchResult(implicit formats: Formats): PartialFunction[JValue, SearchResult] = {
    case j: JObject =>
      SearchResult(
        (j \ "id").extract[BigInt],
        (j \ "title").extract[String],
        (j \ "type").extract[String],
        (j \ "url").extract[String],
        (j \ "description").extract[String],
        RedmineDateParser.parse((j \ "datetime").extract[String])
      )
  }

  object searchResultSerializer extends CustomSerializer[SearchResult](
    formats => deserializeSearchResult(formats) -> PartialFunction.empty)

}
