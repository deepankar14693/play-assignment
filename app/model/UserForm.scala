package model

import play.api.data.Form
import play.api.data.Forms._

import scala.runtime.RichInt

case class UserInformation(fname: String, mname: String, lname: String, uname: String,
                           email: String, pass: String, cpass: String, mobile: String, gender: String,
                           age: Int, hobby: String)

case class UserUpdateForm(fname: String, mname: String, lname: String, uname: String, mobile: String, gender: String,
                          age: Int, hobby: String)

case class LoginUser(email: String, pass: String)

case class Forgot(email: String)

case class ChangePassword(pass: String,cpass: String)

class UserForm {

 val userInfoForm = Form(mapping(
  "fname" -> text.verifying("cannot be empty", _.nonEmpty),
  "mname" -> text.verifying("cannot be empty", _.nonEmpty),
  "lname" -> text.verifying("cannot be empty", _.nonEmpty),
  "uname" -> text.verifying("cannot be empty", _.nonEmpty),
  "email" -> email,
  "pass" -> text.verifying("cannot be empty", _.nonEmpty),
  "cpass" -> nonEmptyText,
  "mobile" -> nonEmptyText,
  "gender" -> nonEmptyText,
  "age" -> number,
  "hobby" -> text.verifying("cannot be empty", _.nonEmpty)
 )(UserInformation.apply)(UserInformation.unapply)
  verifying("max 10 digits required!!!", field => field match {
  case formData => field.mobile.forall(x => x.isDigit) && (field.mobile.length == 10)
 })
  verifying("Passwords must match", verify => verify.pass == verify.cpass)
 )


 val userUpdateForm = Form(mapping(
  "fname" -> text.verifying("cannot be empty", _.nonEmpty),
  "mname" -> text.verifying("cannot be empty", _.nonEmpty),
  "lname" -> text.verifying("cannot be empty", _.nonEmpty),
  "uname" -> text.verifying("cannot be empty", _.nonEmpty),
  "mobile" -> nonEmptyText,
  "gender" -> text.verifying("cannot be empty", _.nonEmpty),
  "age" -> number.verifying("cannot be empty", _.toString.trim.length != 0),
  "hobby" -> text.verifying("cannot be empty", _.nonEmpty)
 )(UserUpdateForm.apply)(UserUpdateForm.unapply)
  verifying("max 10 digits required!!!", field => field match {
  case formData => field.mobile.forall(x => x.isDigit) && (field.mobile.length == 10)
 }))

 val userLoginForm = Form(mapping(
  "email" -> text.verifying("cannot be empty", _.nonEmpty),
  "pass" -> text.verifying("cannot be empty", _.nonEmpty)
 )(LoginUser.apply)(LoginUser.unapply)
 )

 val forgotEmailForm = Form(mapping(
  "email" -> text.verifying("cannot be empty", _.nonEmpty)
 )(Forgot.apply)(Forgot.unapply)
 )

 val passwordChange = Form(mapping(
  "pass" -> text.verifying("cannot be empty", _.nonEmpty),
 "cpass" -> nonEmptyText
 )(ChangePassword.apply)(ChangePassword.unapply)
 verifying("Passwords must match", verify => verify.pass == verify.cpass)
 )
}
