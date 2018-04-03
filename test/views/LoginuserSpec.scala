package views
import model.LoginUser
import org.scalatestplus.play.PlaySpec
import org.specs2.mock.Mockito
import play.api.data.Form
import play.api.mvc.Flash
import play.api.test.FakeRequest
import play.api.test.Helpers.{contentAsString, _}

class LoginuserSpec extends PlaySpec with Mockito{
 "Rendering user login page================================" in new App {
  val request = FakeRequest("POST", "/")
  val flash = mock[Flash]
  val mockedForm = mock[Form[LoginUser]]
  val html = views.html.loginuser(mockedForm)(flash,request)
  contentAsString(html) must include("email")
 }

}
