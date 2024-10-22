import zio.json.{DeriveJsonCodec, JsonCodec}
import zio.schema.{DeriveSchema, Schema}
import zio.schema.annotation.description

case class SiteInfo(
  @description("url") site: String,
  @description("name") name: String
)

object SiteInfo {
  implicit val jsonCodec: JsonCodec[SiteInfo] =
    DeriveJsonCodec.gen[SiteInfo]
  implicit val schema: Schema[SiteInfo] =
    DeriveSchema.gen[SiteInfo]
}

case class RequestInfo(urls: List[String])

object RequestInfo {
  implicit val jsonCodec: JsonCodec[RequestInfo] =
    DeriveJsonCodec.gen[RequestInfo]
  implicit val schema: Schema[RequestInfo] =
    DeriveSchema.gen[RequestInfo]
}