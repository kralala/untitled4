import zio._
import zio.http._
import zio.json.EncoderOps
import zio.schema.codec.JsonCodec.zioJsonBinaryCodec

trait RestServer {
  val runServer: ZIO[Any, Throwable, ExitCode]
  def getInfosByURLs(urls: RequestInfo): ZIO[Client, Throwable, List[SiteInfo]]
}

case class RestServerLive(crawler: Crawler) extends RestServer {

  override def getInfosByURLs(urls: RequestInfo):
    ZIO[Client, Throwable, List[SiteInfo]] = for {
      infos <- crawler.crawl(urls)
    } yield infos

  val port = 9000
  val app = Routes(
    Method.POST / "titles" -> handler { (req: Request) =>
      for {
        urls <- req.body.to[RequestInfo].orElseFail(Response.badRequest)
        infos <- getInfosByURLs(urls)
          .mapBoth(
            _     => Response.internalServerError("Error"),
            infos => Response.json(infos.toJson)
          )
      } yield infos
    },
    Method.GET / "health" -> handler {
      Response.text("health check")
    }
  )
  val runServer: ZIO[Any, Throwable, ExitCode] = for {
    _ <- Server.serve(app.sandbox).provide(Server.defaultWithPort(port), Client.default)
  } yield ExitCode.success

}

object RestServerLive {
  val layer = ZLayer.fromFunction(RestServerLive(_))
}

object InfosApp extends ZIOAppDefault {
  override val run = ZIO.serviceWithZIO[RestServer](_.runServer)
    .provide(
      RestServerLive.layer,
      CrawlerLive.layer
    )
}
