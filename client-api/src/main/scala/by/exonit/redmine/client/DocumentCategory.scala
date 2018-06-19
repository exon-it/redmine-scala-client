package by.exonit.redmine.client

trait DocumentCategoryLike extends Identifiable[BigInt]

case class DocumentCategory(
  id: BigInt,
  name: String,
  isDefault: Boolean
) extends DocumentCategoryLike
