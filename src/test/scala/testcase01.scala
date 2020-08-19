package basic

import io.gatling.core.scenario.Simulation
import io.gatling.core.Predef._
import io.gatling.http.Predef._
class testcase01  extends Simulation{

  private val public_url = "http://ip地址"  //后台接口地址

  /**发送home请求
   * Get请求
   * response返回值为html
   */
  object Home{
    val home = exec(http("Home_request")    //设置请求名称，可随意定义
      .get("http://ip地址")                 //前端请求地址
      .check(status.is(200))          //判断http status
      .check(bodyString.saveAs("Get_bodys"))  //获取response中body所有值，并存入session中Get_bodys参数
      .check(header("ETag").saveAs("Get_header"))  //获取response的header中某个参数值，注：一般用于获取cookies的值
      .check(regex("<meta http-equiv=content-type content=\"(.*)\">").saveAs("Get_parm"))  //采用regex获取html中某个值
    )
      .pause(2)  //设置思考时间2s
      //修改session中信息
      .exec(session =>{
        val header_value = session("Get_header").as[String]   //获取session中参数和对应值
        val session_id =  header_value.split("/")(1)  //获取关联值处理
        session.set("Get_session_id",session_id)      //对session中添加一个新参数，并给新参数赋值
      }
      )
      //打印session信息
      .exec{ session => println(session)
        session
      }
  }

  /** 发送登录请求
   * POST请求
   * 发送Form方式
   *  response返回值为json
   */
  object Login{
    //feed有4种迭代取值方式：
    // 1、queue（顺序取值，直到取完，所以要注意并发虚拟用户数与参数值一致，否则报异常），缺点：不适合持续迭代，只适合一次性并发场景
    // 2、random（随机取值，会重复取值），缺点：取值存在重复，不适合唯一性取值的场景
    // 3、shuffle 网上资料有这个参数，但是在脚本编写时，无法引用，表示不知如何引用到
    // 4、circular（参数一旦用完，从头开始）
    val feeder = csv("D:\\gatling\\src\\test\\resources\\data\\Test_data.csv").circular
    val Content_type = Map("Content-Type" -> "application/x-www-form-urlencoded")   //定义header
    val login = feed(feeder)
      .exec(http("Login_request")
        .post( public_url + "/user/login")
        .headers(Content_type)
        .formParam("username","${login_name}")
        .formParam("pwd","${password}")
        .check(status.is(200))
        .check(jsonPath("$..token").exists)     //判断json中是否存在key
        .check(jsonPath("$..token").saveAs("Get_token"))  // 方法1：获取token值，并将值存入session中Get_token参数
        //.check(regex("\"token\":\"(.*)\"").saveAs("Get_token"))  // 方法2：获取token值，并将值存入session中Get_token参数
        .check(jsonPath("$..*").saveAs("Get_body"))  //方法1：获取response中body所有值，并存入session中Get_body参数
        // .check(bodyString.saveAs("Get_body"))  //方法2：获取response中body所有值，此方法获取的结果与jsonPath("$..*")类似
        .check(header("Date").saveAs("Get_header"))  //获取response的header中某个参数值
      )
      .pause(3)   //设置思考时间3s
      //修改session中信息
      .exec(session =>{
        val token_value = session("Get_token").as[String]   //获取session中Get_token参数值
        session.set("Get_tokens",token_value)      //对session中添加一个新参数，并给新参数赋值
      }
      )
      //打印session信息
      .exec{ session => println(session)
        session
      }
  }

  /** 发送登录请求
   * POST请求
   * 发送JSON方式
   * response返回值为json
   */
  object Login_json{
    val feeder = csv("D:\\gatling\\src\\test\\resources\\data\\Test_data.csv").circular
    //val Content_type = Map("Content-Type" -> "application/json")
    val Content_type = Map("Content-Type" -> "application/x-www-form-urlencoded")
    val login = feed(feeder)
      .exec(http("Login_request")
        .post( public_url + "/user/login")
        .headers(Content_type)
        .body(StringBody("{\"username\":\"${login_name}\",\"pwd\",\"${password}\"}")).asJSON
        .check(status.is(200))
        .check(jsonPath("$..token").exists)     //判断json中是否存在key
        .check(jsonPath("$..token").saveAs("Get_token"))  // 方法1：获取token值，并将值存入session中Get_token参数
      )
      .pause(3)   //设置思考时间3s
      //修改session中信息
      .exec(session =>{
        val token_value = session("Get_token").as[String]   //获取session中Get_token参数值
        session.set("Get_tokens",token_value)      //对session中添加一个新参数，并给新参数赋值
      }
      )
      //打印session信息
      .exec{ session => println(session)
        session
      }
  }

  /**发送退出系统请求
   * POST请求
   */
  object Exit_system{
    val loginout = exec(http("loginout")
      .post(public_url + "/user/loginout")
      .formParam("token","${Get_token}")
    )
  }

  /*******设置场景******/
  //设置场景1
  val scn = scenario("wo玩的就是任性...")     //设置场景名称，可随意定义
    .exec(Home.home,Login.login,Exit_system.loginout)  //调用用例
  //设置场景2
  val scn1 = scenario( "wo扯的都是淡...")
    .exec(Login.login)

  /*******执行场景策略******/
  /* setUp(
       //执行多个场景
      //scn.inject(atOnceUsers(1)),
      //scn1.inject(rampUsers(2) over(1))
    )*/
  setUp(
    //执行单场景
    scn1.inject(constantUsersPerSec(2) during(10))   //每秒运行2个虚拟用户，持续运行10s
  )
}