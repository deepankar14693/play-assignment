package model

import play.api.data.Form
import play.api.data.Forms._

import scala.runtime.RichInt

case class UserInformation(fname: String, mname: String, lname: String, uname: String,
                           email: String, pass: String, cpass: String, mobile: String, gender: String,
                           age: Int, hobby: String)
case class

class UserForm {

 val userInfoForm = Form(mapping(
  "fname" -> nonEmptyText,
  "mname" -> nonEmptyText,
  "lname" -> nonEmptyText,
  "uname" -> nonEmptyText,
  "email" -> email,
  "pass" -> nonEmptyText,
  "cpass" -> nonEmptyText,
  "mobile" -> nonEmptyText,
  "gender" -> nonEmptyText,
  "age" -> number,
  "hobby" -> nonEmptyText
 )(UserInformation.apply)(UserInformation.unapply)
  verifying("max 10 digits required!!!", field => field match {
  case formData => field.mobile.forall(x => x.isDigit) && (field.mobile.length == 10)
 })
  verifying("Passwords must match", verify => verify.pass == verify.cpass)
 )


 val userUpdateForm = Form(mapping(
  "fname" -> nonEmptyText,
  "mname" -> nonEmptyText,
  "lname" -> nonEmptyText,
  "uname" -> nonEmptyText,
  "mobile" -> nonEmptyText,
  "gender" -> nonEmptyText,
  "age" -> number,
  "hobby" -> nonEmptyText
 )(UserInformation.apply)(UserInformation.unapply)
  verifying("max 10 digits required!!!", field => field match {
  case formData => field.mobile.forall(x => x.isDigit) && (field.mobile.length == 10)
 }))

}
