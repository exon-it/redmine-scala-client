package by.exonit.redmine.client.serialization

import by.exonit.redmine.client._
import org.json4s._
import org.json4s.JsonDSL._

import scala.collection.immutable._

object FileSerializers {
  lazy val all = Seq(
    fileSerializer,
    newFileSerializer
  )

  def deserializeFile(implicit formats: Formats): PartialFunction[JValue, ProjectFile] = {
    case j: JObject =>
      WikiPageDetails((j \ "title").extract[String], (j \ "version").extract[BigInt],
        RedmineDateParser.parse((j \ "created_on").extract[String]),
        RedmineDateParser.parse((j \ "updated_on").extract[String]), (j \ "parent" \ "title").extractOpt[WikiPageId],
        (j \ "text").extractOpt[String].getOrElse(""), (j \ "author").extractOpt[UserLink],
        (j \ "comments").extractOpt[String],
        (j \ "attachments").toOption.map(_.extract[Set[Attachment]])
      )
      ProjectFile(
        (j \ "id").extract[BigInt],
        (j \ "filename").extract[String],
        (j \ "filesize").extract[BigInt],
        (j \ "content_type").extract[String],
        (j \ "description").extract[String],
        (j \ "content_url").extract[String],
        (j \ "author").extract[UserLink],
        RedmineDateParser.parse((j \ "created_on").extract[String]),
        (j \ "version").extractOpt[VersionLink],
        (j \ "digest").extract[String],
        (j \ "downloads").extract[BigInt]
      )
  }

  object fileSerializer extends CustomSerializer[ProjectFile](
    formats => deserializeFile(formats) -> PartialFunction.empty)

  def serializeNewFile(implicit formats: Formats): PartialFunction[Any, JValue] = {
    case u: ProjectFile.New =>
      ("token" -> u.token) ~
        ("version_id" -> u.version.map(_.id)) ~
        ("filename" -> u.filename) ~
        ("description" -> u.description)
  }

  object newFileSerializer extends CustomSerializer[ProjectFile.New](
    formats => PartialFunction.empty -> serializeNewFile(formats))
}
