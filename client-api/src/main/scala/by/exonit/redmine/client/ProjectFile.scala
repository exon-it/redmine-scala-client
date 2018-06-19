package by.exonit.redmine.client

import org.joda.time.DateTime

trait ProjectFileIdLike extends Identifiable[BigInt]

case class ProjectFile(
  id: BigInt,
  filename: String,
  size: BigInt,
  contentType: String,
  description: String,
  contentUrl: String,
  author: UserLink,
  createdOn: DateTime,
  version: Option[VersionLink],
  digest: String,
  downloads: BigInt
) extends ProjectFileIdLike

object ProjectFile {
  case class New(
    token: String,
    filename: Option[String] = None,
    description: Option[String] = None,
    version: Option[VersionIdLike] = None
  )
}

