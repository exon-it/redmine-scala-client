package by.exonit.redmine.client

import org.joda.time.DateTime

trait SearchResultIdLike extends Identifiable[BigInt]

case class SearchResult(
  id: BigInt,
  title: String,
  resultType: String,
  url: String,
  description: String,
  dateTime: DateTime
) extends SearchResultIdLike