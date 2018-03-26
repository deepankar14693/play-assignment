package model

import javax.inject.Inject

import akka.Done
import play.api.cache.AsyncCacheApi
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile
import slick.lifted.ProvenShape
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


// def storeUserInformation(userInfo: UserInfo): Future[Done] = {
//  cache.set(userInfo.email, userInfo)
// }
//
// def getUserInformation(email: String): Future[Option[UserInfo]] = {
//  cache.get[UserInfo](email)
// }
case class UserInfo(id: Int, fname: String, mname: String, lname: String, uname: String,
                    email: String, pass: String, cpass: String, mobile: String,
                    gender: String, age: Int, hobby: String)

trait UserProfileRepository extends HasDatabaseConfigProvider[JdbcProfile] {

 import profile.api._

 class UserTable(tag: Tag) extends Table[UserInfo](tag, "users") {

  def id: Rep[Int] = column[Int]("u_id", O.PrimaryKey, O.AutoInc)

  def fname: Rep[String] = column[String]("u_fname")

  def mname: Rep[String] = column[String]("u_mname")

  def lname: Rep[String] = column[String]("u_lname")

  def uname: Rep[String] = column[String]("u_uname")

  def email: Rep[String] = column[String]("u_email")

  def pass: Rep[String] = column[String]("u_pass")

  def cpass: Rep[String] = column[String]("u_cpass")

  def mobile: Rep[String] = column[String]("u_mobile")

  def gender: Rep[String] = column[String]("u_gender")

  def age: Rep[Int] = column[Int]("u_age")

  def hobby: Rep[String] = column[String]("u_hobby")

  def * : ProvenShape[UserInfo] = (id, fname, mname, lname, uname, email, pass, cpass, mobile,
   gender, age, hobby) <> (UserInfo.tupled, UserInfo.unapply)
 }
 val userProfileQuery: TableQuery[UserTable] = TableQuery[UserTable]

}

trait UserRepository {
 self: UserProfileRepository =>

 import profile.api._

 def store(user: UserInfo): Future[Boolean] = {
  db.run(userProfileQuery += user) map (_ > 0)
 }

 def fetchByEmail(email: String): Future[Option[UserInfo]] = {
  val queryResult = userProfileQuery.filter(_.email.toLowerCase === email.toLowerCase).result.headOption
  db.run(queryResult)
 }

}


class UserProfileImplementation @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)
 extends UserProfileRepository with UserRepository
