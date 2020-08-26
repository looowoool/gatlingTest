package objectcls

import io.gatling.core.Predef._
import io.gatling.http.Predef._

object LoginObject{

		val data = csv("data/login_data.csv").circular
		val login = feed(data)
			.exec(http("login")
				.post("/mini/login")
//Form方式
//				.formParam("username", "${login_name}")
//				.formParam("pwd", "${password}")
				.body(StringBody("{\"account\":\"${username}\",\"pass\",\"${password}\"}")).asJSON
				.check(status.is(200))
				.check(jsonPath("$.code").is("0"))//断言-业务响应码是否为0
				.check(jsonPath("$.data").exists.saveAs("resp_login")) // 保存响应的data内容至session中
				  .check(jsonPath("$.Set-Cookie").exists.saveAs("qunhe-jwt"))
				.check(bodyString.saveAs("bodyString"))
			).pause(2) //时间2s
			.exec(addCookie(Cookie("qunhe-jwt", "${qunhe-jwt}")))  // 为后续的请求设置cookie，key为ticket，value为从响应中获取的ticket值
			// 打印session信息
			.exec { session =>
				println("session:" + session)
//				println("bodyString : " + session("bodyString").as[String])
				session
			}

//		val scn = scenario("loginObject")
//			.exec(http("loginObject_0")
//				.post("/mini/login")
//				.headers(headers_0)
//				.body(RawFileBody("object/loginobject/0000_request.json"))
//				.check(bodyBytes.is(RawFileBody("object/loginobject/0000_response.json"))))
//
//		setUp(scn.inject(atOnceUsers(1))).protocols(httpProtocol)

	}
