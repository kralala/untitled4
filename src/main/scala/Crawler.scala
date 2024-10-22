import net.ruippeixotog.scalascraper.browser.JsoupBrowser
import zio._
import zio.http._

trait Crawler {
  def crawl(url: RequestInfo): ZIO[Client, Throwable, List[SiteInfo]]
}

case class CrawlerLive() extends Crawler {
  override def crawl(urls: RequestInfo): ZIO[Client, Throwable, List[SiteInfo]] = {
    ZIO.attempt(
      for {
        url <- urls.urls
        title = extractTitle(url)
      } yield SiteInfo(url, title)
    )
  }

  private def extractTitle(url: String): String = {
    JsoupBrowser().get(url).title
  }
}

object CrawlerLive {
  val layer = ZLayer.succeed(new CrawlerLive())
}