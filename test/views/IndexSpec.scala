package views

import org.scalatestplus.play.PlaySpec
import org.specs2.mock.Mockito
import play.api.mvc.Flash
import play.api.test.FakeRequest
import play.api.test.Helpers.{contentAsString, _}


class IndexSpec extends PlaySpec with Mockito{
 "Rendering index page================================" in new App {
  val request = FakeRequest("GET", "/")
  val flash = mock[Flash]
  val html = views.html.index()(request,flash)
  contentAsString(html) must include("fname")
 }

}
