import io.gatling.core.Predef._
import objectcls.LoginObject



class tastcase02 extends EmptyObject{


  /** *****设置场景 ******/
  //设置场景1
  val scn = scenario("登陆...") //设置场景名称，可随意定义
    .exec(LoginObject.login) //调用用例


//  /** *****执行场景策略 ******/
//  setUp(
//    scn.inject(constantUsersPerSec(1).during(10) //每秒运行2个虚拟用户，持续运行10s
//  )

  setUp(
    scn.inject(atOnceUsers(1))
  ).protocols(httpConf)

}
