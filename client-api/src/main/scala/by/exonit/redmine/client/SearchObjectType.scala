package by.exonit.redmine.client

sealed abstract class SearchObjectType(val token: String) extends Tokenized
object SearchObjectType {


  case object Issues extends SearchObjectType("issues")
  case object News extends SearchObjectType("news")
  case object Documents extends SearchObjectType("documents")
  case object Changesets extends SearchObjectType("changesets")
  case object WikiPages extends SearchObjectType("wiki_pages")
  case object Messages extends SearchObjectType("messages")
  case object Projects extends SearchObjectType("projects")
  case class Custom(override val token: String) extends SearchObjectType(token)

  lazy val predefined: Set[SearchObjectType] = Set(
    Issues, News, Documents, Changesets, WikiPages, Messages, Projects
    )

  def apply(token: String): SearchObjectType =
    predefined.find(_.token.equalsIgnoreCase(token)).getOrElse(Custom(token))
  def unapply(rt: SearchObjectType): Option[String] = Some(rt.token)
}
