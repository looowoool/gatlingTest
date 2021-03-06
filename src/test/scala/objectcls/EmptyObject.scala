import io.gatling.core.Predef._
import io.gatling.http.Predef._

class EmptyObject extends Simulation{

  protected val base_url = " "
  protected val encoding_head = "gzip, deflate"
  protected val contentType_head = "application/json"
  protected val argent_head = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/53.0.2785.143 Safari/537.36 MicroMessenger/7.0.9.501 NetType/WIFI MiniProgramEnv/Windows WindowsWechat"


  val httpConf = http
    .baseURL(base_url)
    .inferHtmlResources()
    .acceptEncodingHeader(encoding_head)
    .contentTypeHeader(contentType_head)
    .userAgentHeader(argent_head)
    //.silentResources //去除加载静态资源

  //eg: val headers_0 = Map("MP-User-Agent" -> "mp.pocket_home/1.14.3")

}
