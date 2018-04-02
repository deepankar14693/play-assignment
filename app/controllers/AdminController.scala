package controllers

import javax.inject._

import model._
import play.api._
import play.api.data.Form
import play.api.mvc._
import play.api.i18n._
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class AdminController @Inject()(userInfo: UserProfileImplementation,adminForm: AdminForms,
                                assignmentInfo: AssignmentRepo,cc: ControllerComponents,
                                userForm: UserForm)
 extends AbstractController(cc) {

 def adminLogin() = Action { implicit request: Request[AnyContent] =>
  Ok(views.html.loginadmin(adminForm.adminLoginForm))
 }

 def adminProfile() = Action { implicit request: Request[AnyContent] =>
  Ok(views.html.adminprofile())
 }

 def loginAdmin(): Action[AnyContent] = Action.async { implicit request =>
  adminForm.adminLoginForm.bindFromRequest().fold(
   formWithErrors => {
    Future.successful(BadRequest(views.html.loginadmin(formWithErrors)))
   },
   data => {
    val result = userInfo.validateAdmin(data.email, data.pass)
    result.map {
     case true => Redirect(routes.AdminController.adminProfile())
       .withSession("adminEmail" -> data.email ,"admin" -> s"welcome admin you have logged in as ${data.email}")
      .flashing("admin" -> s"succesfully logged in")
     case false => Redirect(routes.AdminController.adminLogin())
      .flashing("invalidadmin" -> s"invalid credentials of an admin")
    }
   }
  )
 }

 def updateAdminProfile(): Action[AnyContent] = Action.async { implicit request =>
  val email = request.session.get("adminEmail").get
  val userData = userInfo.fetchByEmail(email)
  userData.map {
   case Some(user) =>
    val userProfile = UserUpdateForm(user.fname, user.mname, user.lname, user.uname,
     user.mobile, user.gender, user.age, user.hobby)
    val profileForm = userForm.userUpdateForm.fill(userProfile)
    Ok(views.html.updateadmin(profileForm))

   case None => Ok
  }
 }

 def updateAdminData(): Action[AnyContent] = Action.async { implicit request =>
  val email = request.session.get("adminEmail").get
  userForm.userUpdateForm.bindFromRequest().fold(
   formWithError => {
    Future.successful(BadRequest(views.html.updateadmin(formWithError)))
   },
   data => {
    val record = UserUpdateForm(data.fname, data.mname, data.lname, data.uname, data.mobile,
     data.gender, data.age, data.hobby)
    val result = userInfo.updateProfile(email, record)
    result.map {
     case true => Redirect(routes.AdminController.adminProfile())
      .flashing("SuccessUpdate" -> s"details for ${data.uname} has been updated")
     case false => Redirect(routes.AdminController.adminProfile())
      .flashing("SuccessFailure" -> s"details for ${data.uname} has not been updated due to some server error")
    }
   }
  )
 }

 def addTest() = Action { implicit request: Request[AnyContent] =>
  Ok(views.html.testadd(adminForm.addAssignmentForm))
 }

 def addAssignment(): Action[AnyContent] = Action.async { implicit request =>
  adminForm.addAssignmentForm.bindFromRequest().fold(
   formWithErrors => {
    Future.successful(BadRequest(views.html.testadd(formWithErrors)))
   },
   data => {
    val record = Assignment(0, data.title, data.description)
    val result = assignmentInfo.addAssignment(record)
    result.map{
    case true => Redirect(routes.AdminController.adminProfile())
     .flashing("added" -> "added assignment successfully")
    case false => Redirect(routes.AdminController.adminProfile())
     .flashing("addFailed" -> "something wrong unable to add assignment")
   }
   }
  )
 }

 def viewTests(): Action[AnyContent] = Action.async { implicit request =>
  assignmentInfo.assignmentCollections
   .map{
    assignments => Ok(views.html.adminassignmentview(assignments))
   }
 }

 def deleteAssignment(id: Int): Action[AnyContent] = Action.async { implicit request =>
  assignmentInfo.removeAssignment(id).map{
   case true => Redirect(routes.AdminController.viewTests())
   case false => InternalServerError("oops...something went wrong")
  }
 }

 def viewUsers(): Action[AnyContent] = Action.async { implicit request =>
  userInfo.userCollections.map{
   users => Ok(views.html.adminviewusers(users))
  }
 }

 def enableUser(id: Int,enable: Boolean): Action[AnyContent] = Action.async { implicit request =>
  if (enable) {
   userInfo.userEnableAndDisable(id, enable).map {
    case true => Redirect(routes.AdminController.viewUsers())
    case false => InternalServerError("oops...something went wrong")
   }
  } else {
   userInfo.userEnableAndDisable(id, enable = true).map {
    case true => Redirect(routes.AdminController.viewUsers())
    case false => InternalServerError("oops...something went wrong")
   }
  }
 }

 def disableUser(id: Int,enable: Boolean): Action[AnyContent] = Action.async { implicit request =>
  if (enable) {
   userInfo.userEnableAndDisable(id, enable = false).map {
    case true => Redirect(routes.AdminController.viewUsers())
    case false => InternalServerError("oops...something went wrong")
   }
  } else {
   userInfo.userEnableAndDisable(id, enable).map {
    case true => Redirect(routes.AdminController.viewUsers())
    case false => InternalServerError("oops...something went wrong")
   }
  }
 }

 def userViewTests(): Action[AnyContent] = Action.async { implicit request =>
  assignmentInfo.assignmentCollections
   .map{
    assignments => Ok(views.html.userviewassignments(assignments))
   }
 }

}
