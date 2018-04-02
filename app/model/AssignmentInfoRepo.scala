package model

import javax.inject.Inject

import akka.Done
import play.api.cache.AsyncCacheApi
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile
import slick.lifted.ProvenShape

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

case class Assignment(id: Int,title: String,desc: String)

trait AssignmentInfo extends HasDatabaseConfigProvider[JdbcProfile] {

 import profile.api._

 val assignmentQuery: TableQuery[UserTable] = TableQuery[UserTable]

 class UserTable(tag: Tag) extends Table[Assignment](tag, "test") {

  def * : ProvenShape[Assignment] = (id, title, desc) <> (Assignment.tupled, Assignment.unapply)

  def id: Rep[Int] = column[Int]("id", O.PrimaryKey, O.AutoInc)

  def title: Rep[String] = column[String]("title")

  def desc: Rep[String] = column[String]("description")

 }

}

 trait AssignmentOperations{

  self: AssignmentInfo =>
  import profile.api._


  def addAssignment(assignment: Assignment): Future[Boolean] = {
  db.run(assignmentQuery += assignment) map(_ > 0)
  }

  def assignmentCollections: Future[List[Assignment]] = {
   db.run(assignmentQuery.to[List].result)
  }

  def removeAssignment(id: Int): Future[Boolean] = {
   db.run(assignmentQuery.filter(assignments => assignments.id === id).delete.map(_ > 0))
  }

 }

class AssignmentRepo @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)
 extends AssignmentInfo with AssignmentOperations
