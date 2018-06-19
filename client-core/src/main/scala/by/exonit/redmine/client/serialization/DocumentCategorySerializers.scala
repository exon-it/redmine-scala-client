package by.exonit.redmine.client.serialization

import by.exonit.redmine.client.DocumentCategory
import org.json4s._

import scala.collection.immutable.Seq

object DocumentCategorySerializers {
  lazy val all: Seq[Serializer[_]] = Seq(
    documentCategoryDeserializer
  )

  def deserializeDocumentCategory(implicit formats: Formats): PartialFunction[JValue, DocumentCategory] = {
    case j: JObject =>
      DocumentCategory(
        (j \ "id").extract[BigInt],
        (j \ "name").extract[String],
        (j \ "is_default").toOption.exists(_.extract[Boolean])
      )
  }

  object documentCategoryDeserializer extends CustomSerializer[DocumentCategory](
    formats => deserializeDocumentCategory(formats) -> PartialFunction.empty
  )
}
