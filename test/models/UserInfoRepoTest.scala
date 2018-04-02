package models
import model.{ChangePassword, UserInfo, UserProfileImplementation, UserUpdateForm}
import akka.Done
import org.specs2.mutable.Specification
import play.api.Application
import play.api.inject.guice.GuiceApplicationBuilder

import scala.concurrent.Await
import scala.concurrent.duration.Duration
import scala.reflect.ClassTag

class ModelsTest[T: ClassTag] {
 def fakeApp: Application = {
  new GuiceApplicationBuilder()
   .build
 }

 lazy val app2doo: Application => T = Application.instanceCache[T]
 lazy val repository : T = app2doo(fakeApp)
}

class UserInfoRepoTest extends org.specs2.mutable.Specification {
 val repo = new ModelsTest[UserProfileImplementation]

 "user info repository" should  {
  "associate detail of a users" in  {
   val user = UserInfo(0,"sudeep","james","tirkey","sudo","sudo94@gmail.com",
   "321","321","9910646559","male",34,"music",isEnable = true)
   val storeResult = Await.result(repo.repository.store(user),Duration.Inf)
   storeResult must equalTo(true)
  }
 }

 "user info repository" should  {
  "fetch detail of a users" in  {
   val user = UserInfo(1,"sudeep","james","tirkey","sudo","sudo94@gmail.com",
    "321","321","9910646559","male",34,"music",true)
   val email = "sudo94@gmail.com"
   val storeResult = Await.result(repo.repository.fetchByEmail(email),Duration.Inf)
   storeResult must beSome(user)
  }
 }

 "user info repository" should  {
  "update details of a users" in  {
   val user =  UserUpdateForm("sudeep", "james", "kumar", "sood", "9910646559", "male",
   34, "music")
   val email = "sudo94@gmail.com"
   val storeResult = Await.result(repo.repository.updateProfile(email,user),Duration.Inf)
   storeResult must equalTo(true)
  }
 }

 "user info repository" should  {
  "give detail of a users" in  {
//   val user = UserInfo(1,"dipankar","singh","ranswal","deep","deep@gmail.com",
//    "444","444","7838011709","male",24,"music",true)
   val email = "sudo94@gmail.com"
   val storeResult = Await.result(repo.repository.validateUser(email,"321"),Duration.Inf)
   storeResult must equalTo(true)
  }
 }

 "user info repository" should  {
  "give detail of a users" in  {
   val email = "deepankar.ranswal@knoldus.in"
   val pass = "007"
   val storeResult = Await.result(repo.repository.validateAdmin(email,pass),Duration.Inf)
   storeResult must equalTo(true)
  }
 }

 "user info repository" should  {
  "fetch detail of a users" in  {
     val email = "sudo94@gmail.com"
   val storeResult = Await.result(repo.repository.userExist(email),Duration.Inf)
   storeResult must equalTo(true)
  }
 }

 "user info repository" should  {
  "change password of a user" in  {
   val info =  ChangePassword("123","123")
   val email = "sudo94@gmail.com"
   val storeResult = Await.result(repo.repository.changePassword(email,info),Duration.Inf)
   storeResult must equalTo(true)
  }
 }

 "user info repository" should {
  "fetch details of all users" in {
   val listUser = List(UserInfo(1,"sudeep","james","kumar","sudo","sudo94@gmail.com","123","123","9910646559",
   "male",34,"music",true))
   val storeResult = Await.result(repo.repository.userCollections, Duration.Inf)
   storeResult must equalTo(listUser)
  }
 }

 "user info repository" should  {
  "enable or disable a user" in  {
   val storeResult = Await.result(repo.repository.userEnableAndDisable(1,false),Duration.Inf)
   storeResult must equalTo(true)
  }
 }

}
