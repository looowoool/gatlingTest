package object

import scala.concurrent.duration._

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.jdbc.Predef._

class LoginObject extends EmptyObject {

	val data = csv("D:\\gatling\\src\\test\\resources\\data\\login_data.csv").circular

	val loginObject = feed(data)
		.exec(http("login")
		  .post("/mini/login")
			.formParam("username", "${login_name}")
			.formParam("pwd", "${password}")
			.check(status.is(status_ok))
		).pause(2) //时间2s
	  .exec(session =>{
			val header_value = session("Set-Cookie").as[String] //获取session中参数和对应值
			val session_id = header_value.split("/")(1) //获取关联值处理
			session.set("Get_session_id", session_id) //对session中添加一个新参数，并给新参数赋值
		})//打印session信息
		.exec { session =>
			println(session)
			session
		}



	val scn = scenario("loginObject")
		.exec(http("loginObject_0")
			.post("/mini/login")
			.headers(headers_0)
			.body(RawFileBody("object/loginobject/0000_request.json"))
			.check(bodyBytes.is(RawFileBody("object/loginobject/0000_response.json"))))

	setUp(scn.inject(atOnceUsers(1))).protocols(httpProtocol)
}