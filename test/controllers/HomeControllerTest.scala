package controllers

import akka.Done
import controllers.HomeController
import model._
import org.scalatestplus.play.PlaySpec
import org.specs2.mock.Mockito
import org.mockito.Mockito.when
import play.api.mvc.ControllerComponents
import play.api.test.FakeRequest
import play.api.test.Helpers.{stubControllerComponents, _}
import play.api.test.Helpers._
import play.api.test.CSRFTokenHelper._

import scala.concurrent.Future

class HomeControllerTest extends PlaySpec with Mockito {

 val controller: TestObjects = getMockedObject
 when(controller.userForm.userInfoForm) thenReturn new UserForm().userInfoForm
 when(controller.userForm.passwordChange) thenReturn new UserForm().passwordChange
 when(controller.userForm.forgotEmailForm) thenReturn new UserForm().forgotEmailForm
 when(controller.userForm.userUpdateForm) thenReturn new UserForm().userUpdateForm

 "Home Controller" should{
  "render update password form" in {
   controller.homeController.updatePassword().apply(FakeRequest(GET,"/")
    .withFormUrlEncodedBody("csrfToken" -> "9c48f081724087b31fcf6099b7eaf6a276834cd9-1487743474314-cda043ddc3d791dc500e66ea")
    .withCSRFToken)
  }
 }

 "should store a user" in {
  val userInformation = UserInfo(0, "deepankar", "singh", "ranswal", "deep", "deep14@gmail.com",
   "123", "123", "8076662034", "male", 24, "music", isEnable = true)

  when(controller.userInfoRepo.fetchByEmail("deep14@gmail.com")) thenReturn Future.successful(Option(userInformation))

  when(controller.userInfoRepo.store(userInformation)) thenReturn Future.successful(true)

  val request = FakeRequest(POST, "/storeUserData").withFormUrlEncodedBody("csrfToken"
   -> "9c48f081724087b31fcf6099b7eaf6a276834cd9-1487743474314-cda043ddc3d791dc500e66ea", "fname" -> "ankit",
   "mname" -> "kumar", "lname" -> "barthwal", " uname" -> "anni", "email" -> "test@example.com",
   "pass" -> "123", "cpass" -> "123", "mobile" -> "9999999999", "gender" -> "male", "age" -> "26",
   "hobby" -> "coding")
   .withCSRFToken

  val result = controller.homeController.storeUserData().apply(request)
  status(result) mustBe 400
 }


  "change password of a user" in {
   val password = ChangePassword("321","321")

   when(controller.userInfoRepo.changePassword("test@example.com",password)) thenReturn Future.successful(true)

   val request = FakeRequest(POST, "/passwordChange")
    .withSession("EmailAuthenticated" -> "test@example.com")
    .withFormUrlEncodedBody("csrfToken" -> "9c48f081724087b31fcf6099b7eaf6a276834cd9-1487743474314-cda043ddc3d791dc500e66ea",
     "email" -> "test@example.com","pass" -> "321","cpass" -> "321")
    .withCSRFToken

   val result = controller.homeController.changePassword().apply(request)
   status(result) mustBe SEE_OTHER
  }

 "not change password if user doesn't exists" in {
  val password = ChangePassword("321","321")

  when(controller.userInfoRepo.changePassword("test@example.com",password)) thenReturn Future.successful(false)

  val request = FakeRequest(POST, "/passwordChange")
    .withSession("EmailAuthenticated" -> "test@example.com")
   .withFormUrlEncodedBody("csrfToken" -> "9c48f081724087b31fcf6099b7eaf6a276834cd9-1487743474314-cda043ddc3d791dc500e66ea",
    "email" -> "test@example.com","pass" -> "321","cpass" -> "321")
   .withCSRFToken

  val result = controller.homeController.changePassword().apply(request)
  status(result) mustBe SEE_OTHER
 }

 "render forgot password form" in {
  controller.homeController.forgot().apply(FakeRequest(GET,"/")
   .withFormUrlEncodedBody("csrfToken" -> "9c48f081724087b31fcf6099b7eaf6a276834cd9-1487743474314-cda043ddc3d791dc500e66ea")
   .withCSRFToken)
 }

 "test email of a user" in {
  val password = Forgot("test@example.com")

  when(controller.userInfoRepo.userExist("test@example.com")) thenReturn Future.successful(true)

  val request = FakeRequest(POST, "/emailForgot")
   .withFormUrlEncodedBody("csrfToken" -> "9c48f081724087b31fcf6099b7eaf6a276834cd9-1487743474314-cda043ddc3d791dc500e66ea",
    "email" -> "test@example.com")
   .withCSRFToken

  val result = controller.homeController.emailForgot().apply(request)
  status(result) mustBe SEE_OTHER
 }

 "render profile page" in {
  controller.homeController.profile().apply(FakeRequest(GET,"/"))
//   .withFormUrlEncodedBody("csrfToken" -> "9c48f081724087b31fcf6099b7eaf6a276834cd9-1487743474314-cda043ddc3d791dc500e66ea")
//   .withCSRFToken)
 }

 "update user data" in {
    val userUpdate = UserUpdateForm("deepankar","singh","ranswal","deep","8076662034","male",
   24,"music")

  when(controller.userInfoRepo.updateProfile("deep14@gmail.com",userUpdate)) thenReturn Future.successful(true)

  val request = FakeRequest(POST, "/update")
   .withSession("userEmail" -> "deep14@gmail.com")
   .withFormUrlEncodedBody("csrfToken" -> "9c48f081724087b31fcf6099b7eaf6a276834cd9-1487743474314-cda043ddc3d791dc500e66ea",
    "fname" -> "deepankar", "mname" -> "singh", "lname" -> "ranswal", " uname" -> "deep", "email" -> "deep14@gmail.com",
    "mobile" -> "9999999999", "gender" -> "male", "age" -> "26",
    "hobby" -> "music")
   .withCSRFToken

  val result = controller.homeController.updateUserData().apply(request)
  status(result) mustBe 400
 }



 def getMockedObject: TestObjects = {
  val mockedUserFormRepo = mock[UserForm]
  val mockedUserUserInfoRepo= mock[UserProfileImplementation]

  val controller = new HomeController(mockedUserFormRepo, mockedUserUserInfoRepo, stubControllerComponents())

  TestObjects(stubControllerComponents(), mockedUserFormRepo, mockedUserUserInfoRepo, controller)
 }

 case class TestObjects(controllerComponent: ControllerComponents,
                        userForm: UserForm,
                        userInfoRepo: UserProfileImplementation,
                        homeController: HomeController)
}