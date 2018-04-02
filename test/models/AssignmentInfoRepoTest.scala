package models
import model.AssignmentRepo
import model.Assignment
import akka.Done
import org.specs2.mutable.Specification
import play.api.Application
import play.api.inject.guice.GuiceApplicationBuilder

import scala.concurrent.Await
import scala.concurrent.duration.Duration
import scala.reflect.ClassTag

class ModelTest[T: ClassTag] {
 def fakeApp: Application = {
  new GuiceApplicationBuilder()
   .build
 }

 lazy val app2doo: Application => T = Application.instanceCache[T]
 lazy val repository : T = app2doo(fakeApp)
}


class AssignmentInfoRepoTest extends org.specs2.mutable.Specification {
 val repo = new ModelTest[AssignmentRepo]

 "assignment info repository" should {
  "associate detail of an assignment" in {
   val assignment = Assignment(0,"java 8","use streams to implement the operations")
   val storeResult = Await.result(repo.repository.addAssignment(assignment), Duration.Inf)
   storeResult must equalTo(true)
  }
 }


 "assignment info repository" should {
  "fetch details of all assignments" in {
   val assignment = List(Assignment(1,"java 8","use streams to implement the operations"))
   val storeResult = Await.result(repo.repository.assignmentCollections, Duration.Inf)
   storeResult must equalTo(assignment)
  }
 }


// "user info repository" should {
//  "delete detail of an assignment" in {
//   val storeResult = Await.result(repo.repository.removeAssignment(6), Duration.Inf)
//   storeResult must equalTo(true)
//  }
// }

}
