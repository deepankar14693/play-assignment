package controllers

import javax.inject._

import model._
import play.api._
import play.api.data.Form
import play.api.mvc._
import play.api.i18n.I18nSupport

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * This controller creates an `Action` to handle HTTP requests to the
  * application's home page.
  */
@Singleton
class HomeController @Inject()(userForm: UserForm, userInfo: UserProfileImplementation,
                               cc: ControllerComponents) extends AbstractController(cc) {

 /**
   * Create an Action to render an HTML page.
   *
   * The configuration in the `routes` file means that this method
   * will be called when the application receives a `GET` request with
   * a path of `/`.
   */
 def index() = Action { implicit request: Request[AnyContent] =>
  Ok(views.html.index())
 }

 /**
   *
   * @return action for redirecting to signup template
   */
 def register(): Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
  //  Ok(views.html.signup(userForm.userInfoForm))
  Ok(views.html.signup(userForm.userInfoForm))
 }

 /**
   *
   * @return action for redirecting user to login template
   */
 def login() = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.loginuser(userForm.userLoginForm))
 }

 def logoutUser() = Action { implicit request: Request[AnyContent] =>
   Ok(views.html.index())
   .flashing("userLogout" -> "you are successfully logged out...!!!!")
 }
 /**
   * for authenticating user and redirecting to user profile page
   *
   * @return action for redirecting user to his profile page
   */
 def userLogin(): Action[AnyContent] = Action.async { implicit request =>
  userForm.userLoginForm.bindFromRequest().fold(
   formWithErrors => {
    Future.successful(BadRequest(views.html.loginuser(formWithErrors)))
   },
   data => {
    val result = userInfo.validateUser(data.email, data.pass)
    result.map {
     case true => Redirect(routes.HomeController.profile())
       .withSession("userEmail" -> data.email)
      .flashing("valid" -> s"succesfully logged in")
     case false => Redirect(routes.HomeController.login())
      .flashing("invalid" -> s"invalid credentials try again")
    }
   }
  )
 }

 def updateProfile(): Action[AnyContent] = Action.async { implicit request =>
  val email = request.session.get("userEmail").get
  val userData = userInfo.fetchByEmail(email)
  userData.map {
   case Some(y) =>
    val userProfile = UserUpdateForm(y.fname, y.mname, y.lname, y.uname,
     y.mobile, y.gender, y.age, y.hobby)
    val profileForm = userForm.userUpdateForm.fill(userProfile)
    Ok(views.html.update(profileForm))

   case None => Ok
  }
 }

 def updateUserData(): Action[AnyContent] = Action.async { implicit request =>
  val email = request.session.get("userEmail").get
  userForm.userUpdateForm.bindFromRequest().fold(
   formWithError => {
    Future.successful(BadRequest(views.html.update(formWithError)))
   },
   data => {
    val record = UserUpdateForm(data.fname, data.mname, data.lname, data.uname, data.mobile,
     data.gender, data.age, data.hobby)
    val result = userInfo.updateProfile(email, record)
    result.map {
     case true => Redirect(routes.HomeController.profile())
      .flashing("Success" -> s"details for ${data.uname} has been updated")
     case false => Redirect(routes.HomeController.profile())
      .flashing("Success" -> s"details for ${data.uname} has not been updated due to some server error")
    }
   }
  )
 }

 def profile() = Action { implicit request: Request[AnyContent] =>
  Ok(views.html.profile())
 }

 def forgot() = Action { implicit request: Request[AnyContent] =>
  Ok(views.html.forgotemail(userForm.forgotEmailForm))
 }

 def updatePassword(): Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
  Ok(views.html.changepassword(userForm.passwordChange))
 }

 def emailForgot(): Action[AnyContent] = Action.async { implicit request =>
  userForm.forgotEmailForm.bindFromRequest().fold(
   formWithError => {
    Future.successful(BadRequest(views.html.forgotemail(formWithError)))
   },
   data => {
    val result = userInfo.userExist(data.email)
    result.map {
     case true => Redirect(routes.HomeController.updatePassword)
    .withSession("EmailAuthenticated" -> data.email)
      .flashing("change" -> "correct email now you change your password")
     case false => Redirect(routes.HomeController.forgot)
      .flashing("unchanged" -> "email didn't matched our database please try again")
    }
   }
  )
 }

 def changePassword: Action[AnyContent] = Action.async { implicit request =>
  val email = request.session.get("EmailAuthenticated").get
userForm.passwordChange.bindFromRequest().fold(
 formWithError => {
  Future.successful(BadRequest(views.html.changepassword(formWithError)))
 },
 data => {
  val record = ChangePassword(data.pass,data.cpass)
  val result = userInfo.changePassword(email,record)
  result.map {
   case true => Redirect(routes.HomeController.index())
     .flashing("complete" -> "password changed successfully")
   case false => Redirect(routes.HomeController.updatePassword())
     .flashing("incomplete" -> "internal server error please try again later")
  }
 }
)
 }

 def storeUserData: Action[AnyContent] = Action.async { implicit request =>
  userForm.userInfoForm.bindFromRequest().fold(
   formWithError => {
    Future.successful(BadRequest(views.html.signup(formWithError)))
   },
   data => {
    userInfo.fetchByEmail(data.email).flatMap {
     optionalRecord =>
      optionalRecord.fold {
       val record = UserInfo(0, data.fname, data.mname, data.lname, data.uname, data.email,
        data.pass, data.cpass, data.mobile, data.gender, data.age, data.hobby,true)
       val result = userInfo.store(record)
       result.map {
        case true => Redirect(routes.HomeController.profile())
         .withSession("Name" -> data.uname, "Email" -> data.email)
         .flashing("Success" -> s"welcome ${data.uname} keep calm assignments coming soon")
        case false => Logger.info("oops ..... something went wrong..!!!!!")
         Redirect(routes.HomeController.index())
       }

      } {
       _ => Future.successful(InternalServerError("User exist SuccessFully"))
      }
    }
   })
 }

}
