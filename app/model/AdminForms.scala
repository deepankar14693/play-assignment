package model

import play.api.data.Form
import play.api.data.Forms._

case class LoginAdmin(email: String,pass: String)
case class AddAssignment(title: String,description: String)

class AdminForms {

 val adminLoginForm = Form(mapping(
  "email" -> text.verifying("cannot be empty", _.nonEmpty),
  "pass" -> text.verifying("cannot be empty", _.nonEmpty)
 )(LoginAdmin.apply)(LoginAdmin.unapply)
 )

 val addAssignmentForm = Form(mapping(
  "title" -> text.verifying("cannot be empty", _.nonEmpty),
  "description" -> text.verifying("cannot be empty", _.nonEmpty)
 )(AddAssignment.apply)(AddAssignment.unapply)
 )

}
