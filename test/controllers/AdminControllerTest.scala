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

class AdminControllerTest extends PlaySpec with Mockito {

 val controller: TestObjects = getMockedObject
 when(controller.adminForm.addAssignmentForm) thenReturn new AdminForms().addAssignmentForm

 "Admin Controller" should {
  "show assignment collections to user" in {
   val assignment = Assignment(0,"hello world","scala application")

   when(controller.assignmentInfoRepo.assignmentCollections) thenReturn Future.successful(List(assignment))

   controller.adminController.userViewTests().apply(FakeRequest(GET,"/").withBody(assignment))
     }

  "view assignment collections to admin" in {
   val assignment = Assignment(0,"hello world","scala application")

   when(controller.assignmentInfoRepo.assignmentCollections) thenReturn Future.successful(List(assignment))

   controller.adminController.viewTests().apply(FakeRequest(GET,"/").withBody(assignment))
  }

  "add assignment to db" in {
   val assignment = Assignment(0,"hello world","scala application")

   when(controller.assignmentInfoRepo.addAssignment(assignment)) thenReturn Future.successful(true)

   val request = FakeRequest(POST, "/passwordChange")
        .withFormUrlEncodedBody("csrfToken" -> "9c48f081724087b31fcf6099b7eaf6a276834cd9-1487743474314-cda043ddc3d791dc500e66ea",
     "title" -> "hello world","description" -> "scala application")
    .withCSRFToken

   val result = controller.adminController.addAssignment().apply(request)
   status(result) mustBe SEE_OTHER
  }

  "render form add test" in {
   controller.adminController.addTest().apply(FakeRequest(GET,"/")
    .withFormUrlEncodedBody("csrfToken" -> "9c48f081724087b31fcf6099b7eaf6a276834cd9-1487743474314-cda043ddc3d791dc500e66ea",
    "title" -> "hello world", "description" -> "scala application")
    .withCSRFToken)
  }

  "update admin data" in {
   val record = UserUpdateForm("deepankar", "singh", "ranswal", "deep", "9818928020",
    "male", 34, "music")

   when(controller.userInfoRepo.updateProfile("deep14@gmail.com",record)) thenReturn Future.successful(true)

   val request = FakeRequest(POST, "/update")
    .withSession("adminEmail" -> "deep14@gmail.com")
    .withFormUrlEncodedBody("csrfToken" -> "9c48f081724087b31fcf6099b7eaf6a276834cd9-1487743474314-cda043ddc3d791dc500e66ea",
     "fname" -> "deepankar", "mname" -> "singh", "lname" -> "ranswal", " uname" -> "deep",
     "mobile" -> "9818928020", "gender" -> "male", "age" -> "34",
     "hobby" -> "music")
    .withCSRFToken

   val result = controller.adminController.updateAdminData().apply(request)
   status(result) mustBe  SEE_OTHER
  }

  "update admin profile" in {
   val userInformation = UserInfo(0, "deepankar", "singh", "ranswal", "deep", "deep14@gmail.com",
    "123", "123", "8076662034", "male", 24, "music", isEnable = true)
   val record = UserUpdateForm("deepankar", "singh", "ranswal", "deep", "9818928020",
    "male", 34, "music")

   when(controller.userInfoRepo.fetchByEmail("deep14@gmail.com")) thenReturn Future.successful(Option(userInformation))

   controller.adminController.updateAdminProfile().apply(FakeRequest(GET,"/").withBody(record))
  }

 }

 def getMockedObject: TestObjects = {
  val mockedAdminFormRepo = mock[AdminForms]
  val mockedUserFormRepo = mock[UserForm]
  val mockedUserUserInfoRepo= mock[UserProfileImplementation]
  val mockedAssignmentInfoRepo= mock[AssignmentRepo]

  val controller = new AdminController(mockedUserUserInfoRepo,mockedAdminFormRepo, mockedAssignmentInfoRepo, stubControllerComponents(),mockedUserFormRepo)

  TestObjects(stubControllerComponents(), mockedAdminFormRepo, mockedUserUserInfoRepo,mockedAssignmentInfoRepo, mockedUserFormRepo, controller)
 }

 case class TestObjects(controllerComponent: ControllerComponents,
                        adminForm: AdminForms,
                        userInfoRepo: UserProfileImplementation,
                        assignmentInfoRepo: AssignmentRepo,
                        userForm: UserForm,
                        adminController: AdminController)
}
