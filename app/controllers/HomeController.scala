package controllers

import javax.inject._

import model.{UserForm, UserInfo, UserInformation, UserProfileImplementation}
import play.api._
import play.api.data.Form
import play.api.mvc._
import play.api.i18n._

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * This controller creates an `Action` to handle HTTP requests to the
  * application's home page.
  */
@Singleton
class HomeController @Inject()(userForm: UserForm, userInfo: UserProfileImplementation, cc: ControllerComponents) extends AbstractController(cc) {

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

 def register() = Action { implicit request: Request[AnyContent] =>
  //  Ok(views.html.signup(userForm.userInfoForm))
  Ok(views.html.signup(userForm.userInfoForm))
 }

 def updateProfile() = Action { implicit request: Request[AnyContent] =>
  //  Ok(views.html.signup(userForm.userInfoForm))
  Ok(views.html.update(userForm.userInfoForm))
   .withSession("")
 }

 def profile(name: String) = Action { implicit request: Request[AnyContent] =>
  Ok(views.html.profile(name))
 }

 def storeUserData: Action[AnyContent] = Action.async { implicit request =>
  userForm.userInfoForm.bindFromRequest().fold(
   formWithError => {
    Future.successful(BadRequest(views.html.signup(formWithError)))
   },
   data => {
    userInfo.fetchByEmail(data.email).flatMap  {
     optionalRecord =>
      optionalRecord.fold {
       val record = UserInfo(0, data.fname, data.mname, data.lname, data.uname, data.email,
        data.pass, data.cpass, data.mobile, data.gender, data.age, data.hobby)
       val result = userInfo.store(record)
       result.map {
        case true => Redirect(routes.HomeController.profile(data.uname))
         .withSession("Name" -> data.uname, "Email" -> data.email)
         .flashing("Success" -> s"welcome ${data.uname} keep calm assignments coming soon")
        case false => Logger.info("oops ..... something went wrong..!!!!!")
                      Redirect(routes.HomeController.index())
       }

      }{
       _ => Future.successful(InternalServerError("User exist SuccessFully"))
      }
    }
   })
 }

}
