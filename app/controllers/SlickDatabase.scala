/**
 * BetLiMS is a Library management Software.
 * Copyright (C) 2014  radsaggi, shankdude, garuda19, shashidinthiri9

 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 3 as 
 * published by the Free Software Foundation.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License v3 for more details.

 * You should have received a copy of the GNU General Public License v3
 * along with this program.  If not, see <http://www.gnu.org/licenses/gpl.html>.
 */

package controllers

import play.api.db.slick.DB
import play.api.Application
import play.api.db.slick.Config.driver.simple._
import scala.slick.lifted.Tag
/**
 *@param booksTableName defines table 
 *@param isbn is defined to be the array of string
 *@param title,password,student,code,url are the array of type string 
 *@param copies,year is defined to be the array of integer
 *@param author is defined to be the array of string type with name author
 *@param name userid,branch,accessyear,publisher,pulisherYear,admins are arrays of string type
 *The parameters and functions of class BookTable extends that of Table[Book]
 *Multiple parameters are checked with single book with Book.tupled
 *Extracts the value if it matches with any of the parameters using Book.unapply
 *Defines each object of different parameters and its lists
 */
trait SlickDatabaseTables {

  import Models._
/**
 *Defines the list of books and its paramters
 */
  val booksTableName = "books"
  val books = TableQuery[BookTable]
  class BookTable(tag: Tag) extends Table[Book](tag, booksTableName) {
    def isbn = column[String]("isbn", O.PrimaryKey)
    def title = column[String]("title")
    def author = column[String]("author")
    def copies = column[Int]("copies")

    def * = (isbn, title, author, copies) <> (Book.tupled, Book.unapply)
  }
/**
 *Defines the list of student user creation and its paramters
 */
  val studentsTableName = "student_user"
  val students = TableQuery[StudentTable]
  class StudentTable(tag: Tag) extends Table[StudentUser](tag, studentsTableName) {
    def userid = column[String]("user_id", O.PrimaryKey)
    def name = column[String]("name")
    def branch = column[String]("branch")
    def year = column[Int]("year")

    def * = (userid, name, year, branch) <> (StudentUser.tupled, StudentUser.unapply)
  }
/**
 *Defines the list of admin user creation and its paramters
 */
  val adminsTableName = "admin_user"
  val admins = TableQuery[AdminTable]
  class AdminTable(tag: Tag) extends Table[AdminUser](tag, adminsTableName) {
    def userid = column[String]("user_id", O.PrimaryKey)
    def name = column[String]("name")

    def * = (userid, name) <> (AdminUser.tupled, AdminUser.unapply)
  }
/** 
 *Defines the list of student user authorization and its paramters
  */
  val studentUsersAuthTableName = "student_user_auth"
  val studentUsersAuth = TableQuery[StudentUserAuthTable]
  class StudentUserAuthTable(tag: Tag) extends Table[(String, String)](tag, studentUsersAuthTableName) {
    def userid = column[String]("user_id", O.PrimaryKey)
    def password = column[String]("pass")
    def student = foreignKey("student_user", userid, students)(_.userid)

    def * = (userid, password)
  }

/**
 *Defines the list of admin user authorization and its paramters
 */
  val adminUsersAuthTableName = "admin_user_auth"
  val adminUsersAuth = TableQuery[AdminUserAuthTable]
  class AdminUserAuthTable(tag: Tag) extends Table[(String, String)](tag, adminUsersAuthTableName) {
    def userid = column[String]("user_id", O.PrimaryKey)
    def password = column[String]("pass")
    def admin = foreignKey("admin_user", userid, admins)(_.userid)

    def * = (userid, password)
  }
/**
 *Defines the list of e-journal publishers creation and its paramters
 *Returns the name of ejournal publisher if found
 */  
  val ejournalPublishersTableName = "ejournal_publisher"
  val ejournalPublishers = TableQuery[EJournalPublisherTable]
  class EJournalPublisherTable(tag: Tag) extends Table[EJournalPublisher](tag, ejournalPublishersTableName) {
    def name = column[String]("name")
    def code = column[String]("code", O.PrimaryKey)
    def url = column[String]("url")
    
    def * = (name, code, url) <> (EJournalPublisher.tupled, EJournalPublisher.unapply)
  }
/**
 *Defines the list of ejournals creation and its paramters
 *Returns the name of ejournal if found
 */
  val ejournalsTableName = "ejournals"
  val ejournals = TableQuery[EJournalTable]
  class EJournalTable(tag: Tag) extends Table[EJournal](tag, ejournalsTableName) {
    def name = column[String]("name", O.PrimaryKey)
    def accessYear = column[Int]("access_from")
    def url = column[String]("url")
    def publisherCode = column[String]("publisher_code")
    def publisher = foreignKey(ejournalPublishersTableName, publisherCode, 
                               ejournalPublishers)(_.code)
    
    def * = (name, accessYear, url, publisherCode) <> (EJournal.tupled, EJournal.unapply)
  }
/**
 *Defines the list of e-publishers  creation and its paramters
 *Returns the name of ebookpublisher if found
 */  
  val ebookPublishersTableName = "ebook_publisher"
  val ebookPublishers = TableQuery[EBookPublisherTable]
  class EBookPublisherTable(tag: Tag) extends Table[EBookPublisher](tag, ebookPublishersTableName) {
    def name = column[String]("name")
    def code = column[String]("code", O.PrimaryKey)
    def url = column[String]("url")
    
    def * = (name, code, url) <> (EBookPublisher.tupled, EBookPublisher.unapply)
  }
/**
 *Defines the list of ebooks creation and its paramters
 *Returns the name of ebook if found
 */  
  val ebooksTableName = "ebooks"
  val ebooks = TableQuery[EBookTable]
  class EBookTable(tag: Tag) extends Table[EBook](tag, ebooksTableName) {
    def name = column[String]("name", O.PrimaryKey)
    def url = column[String]("url")
    def publisherCode = column[String]("publisher_code")
    def publisher = foreignKey(ebookPublishersTableName, publisherCode, 
                               ebookPublishers)(_.code)
    
    def * = (name, url, publisherCode) <> (EBook.tupled, EBook.unapply)
  }
/**
 *Defines the list of edatabases creation and its paramters
 *Returns the name of book if found
 */  
  val edatabasesTableName = "edatabases"
  val edatabases = TableQuery[EDatabaseTable]
  class EDatabaseTable(tag: Tag) extends Table[EDatabase](tag, edatabasesTableName) {
    def name = column[String]("name", O.PrimaryKey)
    def url = column[String]("url")
    
    def * = (name, url) <> (EDatabase.tupled, EDatabase.unapply)
  }
}
/**
 *SlickDatabaseService extends the paramaters and functions as that of DatabaseService
 *Adds new book to existing database if necessery using insertBooks
 *Defines a new session using DB withsession
 *@param name is a string type by default
 */
trait SlickDatabaseService extends DatabaseService {
  tables: SlickDatabaseTables =>

  import Models._
  import FormEncapsulators._

  implicit val application: Application
  val name: String = "default"

  def insertBooks(b: Seq[Book]) {
    DB withSession { implicit session =>
      tables.books ++= b
    }
  }
/**
 *Overrides the previous values of paramters with the new ones which means the 
 *old database can be replaced or merged with the new database 
 *by creating a new session
 *@param v0 defines the books in the table
 *@param v1 defines the isbn match
 *@param v2 defines the title match
 *@param v3 defines the author match
 *v1 returns v0, v2 returns v1, v3 returns v2 if the respective parameters do not match
 *v3.list returns the list of author matched books
 */ 
  override def booksearch(q: BookSearch): List[Book] = {
    DB(name) withSession { implicit session =>
      val v0 = tables.books

      val v1 = q.isbn match {
        case Some(x) => v0.filter(y => y.isbn.like("%" + x + "%"))
        case None    => v0
      }
      val v2 = q.title match {
        case Some(x) => v1.filter(y => y.title.like("%" + x + "%"))
        case None    => v1
      }
      val v3 = q.author match {
        case Some(x) => v2.filter(y => y.author.like("%" + x + "%"))
        case None    => v2
      }

      v3.list
    }
  }
/**
 *Inserts user might be studentuser or adminuser
 *Logs in if the user id and password given by the user or admin are matching respectively
 *Password is encrypted
 */
  def insertUser(u: Seq[(User, String)]) {
    DB(name) withSession { implicit session =>
      for ((user, pass) <- u) {
        user match {
          case s: StudentUser => 
            tables.students += s
            tables.studentUsersAuth += (user.userid, encryptPassword(pass))
          case a: AdminUser   => 
            tables.admins += a
            tables.adminUsersAuth += (user.userid, encryptPassword(pass))
        }
      }
    }
  }
/**
 *Overrides the current existing details of the admin user with the new
 *Yields admin
 */ 
  override def authenticateAdminUser(q: UserLogin): Option[AdminUser] = {
    DB(name) withSession { implicit session =>
      val a = for {
        aAuth <- adminUsersAuth filter (_.userid === q.username) if (matchPasswords(aAuth.password, q.password))
        admin <- aAuth.admin
      } yield admin
      //Since userid is unique
      a.list.headOption
    }
  }
/**
 *Overrides the current existing details of the student user with the new
 *Yields student
 */   
  override def authenticateStudentUser(q: UserLogin): Option[StudentUser] = {
    DB(name) withSession { implicit session =>
      val s = for {
        sAuth <- studentUsersAuth filter (_.userid === q.username) if (matchPasswords(sAuth.password, q.password))
        student <- sAuth.student
      } yield student
      //Since userid is unique
      s.list.headOption
    }
  }

  private def matchPasswords(passCorrect: Column[String], passRequest: String) = {
    passCorrect === passRequest
  }

  private def encryptPassword(pass: String) = {
    pass
  }
/**
 *Overrides the current existing details of the allEJournalPublishers with the new
 *Yields admin
 */   
  override def allEJournalPublishers() = DB(name) withSession { implicit session =>
    ejournalPublishers.list
  }
/**
 *Overrides the current existing details of the EJournalPublisher
 *Adds the new EjournalPublisher before overriding
 */   
  override def addEJournalPublisher(publisher: EJournalPublisher) = 
  DB(name) withSession { implicit session =>
    ejournalPublishers += publisher
  }
/**
 *Overrides the current existing details of EJournalPublisher
 *Removes the EJournalPublisher before overriding based on the given parameters
 */ 

  override def removeEJournalPublisher(publisher: EJournalPublisher) = 
  DB(name) withSession { implicit session =>
    val publisherQuery = ejournalPublishers.filter(_.code === publisher.code)
    publisherQuery.list.headOption.map { p =>
      ejournals.filter(j => j.publisherCode === p.code && j.name === name).delete
    }
    publisherQuery.delete
  }
 
/**
 *Overrides the current existing details of EJournals
 *Filters the EJournalPublisher before overriding based on the given parameters
 */  
  override def allEJournals(publisherCode: String) = DB(name) withSession {
    implicit session =>
    ejournalPublishers.filter(_.code === publisherCode).list.headOption.map { p =>
      ejournals.filter(_.publisherCode === p.code).list
    }
  }
/**
 *Overrides the current existing details of EJournal
 *Adds the EJournal before overriding based on the given parameters
 */   
  override def addEJournal(journal: EJournal) = DB(name) withSession { implicit session =>
    ejournalPublishers.filter(_.code === journal.publisherCode).list.headOption.map { p =>
      ejournals += journal
    }
  }

/**
 *Overrides the current existing details of EJournal
 *Removes the EJournal before overriding based on the given parameters
 */   
  override def removeEJournal(journal: EJournal) = DB(name) withSession {
    implicit session =>
    ejournalPublishers.filter(_.code === journal.publisherCode).list.headOption.map { p =>
      ejournals.filter(j => j.publisherCode === p.code && j.name === journal.name).delete
    }
  }
/**
 *Overrides the current existing details of EBookPublisher
 */   
  override def allEBookPublishers() = DB(name) withSession { implicit session =>
    ebookPublishers.list
  }
/**
 *Overrides the current existing details of EBookPublisher
 *Adds the EBookPublisher before overrideing based on given parameters
 */  
  override def addEBookPublisher(publisher: EBookPublisher) = 
  DB(name) withSession { implicit session =>
    ebookPublishers += publisher
  }
/**
 *Overrides the current existing details of EBookPublisher
 *Removes the EBookPublisher before overrideing based on given parameters
 */  
  override def removeEBookPublisher(publisher: EBookPublisher) = 
  DB(name) withSession { implicit session =>
    val publisherQuery = ebookPublishers.filter(_.code === publisher.code)
    publisherQuery.list.headOption.map { p =>
      ebooks.filter(j => j.publisherCode === p.code && j.name === name).delete
    }
    publisherQuery.delete
  }
/**
 *Overrides the current existing details of EBooks
 *Filters the EBook before overrideing based on given parameters
 */    
  override def allEBooks(publisherCode: String) = DB(name) withSession {
    implicit session =>
    ebookPublishers.filter(_.code === publisherCode).list.headOption.map { p =>
      ebooks.filter(_.publisherCode === p.code).list
    }
  }
/**
 *Overrides the current existing details of EBook
 *Adds the EBook before overrideing based on given parameters
 */    
  override def addEBook(book: EBook) = DB(name) withSession { implicit session =>
    ebookPublishers.filter(_.code === book.publisherCode).list.headOption.map { p =>
      ebooks += book
    }
  }
/**
 *Overrides the current existing details of EBooks
 *Removes the EBooks before overrideing based on given parameters
 */    
  override def removeEBook(book: EBook) = DB(name) withSession {
    implicit session =>
    ebookPublishers.filter(_.code === book.publisherCode).list.headOption.map { p =>
      ebooks.filter(j => j.publisherCode === p.code && j.name === book.name).delete
    }
  }
/**
 *Overrides the current existing details of allEDatabases
 */  
  override def allEDatabases() = DB(name) withSession { implicit session =>
    edatabases.list
  }
/**
 *Overrides the current existing details of EDatabase
 */    
  override def addEDatabase(database: EDatabase) = 
  DB(name) withSession { implicit session =>
    edatabases += database
  }
/**
 *Overrides the current existing details of allEDatabase
 *Removes the allEDatabase before overrideing based on given parameters
 */  
  override def removeEDatabase(database: EDatabase) = 
  DB(name) withSession { implicit session =>
    val databaseQuery = edatabases.filter(_.name === database.name)
    databaseQuery.delete
  }
/**
 *Overrides all the displayed data present and 
 *Creates tables for books,students,admins,adminUserAuth,ejournalPublishers,ebooks
 *ebookPublishers,edatabases if they are  empty
 *@param books,students,admins,adminUserAuth,ejournalPublishers,ebooks,ebookPublishers,edatabases
 *are the string types
 */  
  override def init() {
    DB(name) withSession { implicit session =>
      import scala.slick.jdbc.meta._

      if (MTable.getTables(tables.booksTableName).list().isEmpty) {
        tables.books.ddl.create
      }
      if (MTable.getTables(tables.studentsTableName).list().isEmpty) {
        tables.students.ddl.create
      }
      if (MTable.getTables(tables.adminsTableName).list().isEmpty) {
        tables.admins.ddl.create
      }
      if (MTable.getTables(tables.adminUsersAuthTableName).list().isEmpty) {
        tables.adminUsersAuth.ddl.create
      }
      if (MTable.getTables(tables.studentUsersAuthTableName).list().isEmpty) {
        tables.studentUsersAuth.ddl.create
      }
      if (MTable.getTables(tables.ejournalPublishersTableName).list().isEmpty) {
        tables.ejournalPublishers.ddl.create
      }
      if (MTable.getTables(tables.ejournalsTableName).list().isEmpty) {
        tables.ejournals.ddl.create
      }
      if (MTable.getTables(tables.ebookPublishersTableName).list().isEmpty) {
        tables.ebookPublishers.ddl.create
      }
      if (MTable.getTables(tables.ebooksTableName).list().isEmpty) {
        tables.ebooks.ddl.create
      }
      if (MTable.getTables(tables.edatabasesTableName).list().isEmpty) {
        tables.edatabases.ddl.create
      }
    }
  }
}
/**
 *Creates new application and overrides the current names with new ones
 *DatabaseService acts with SlickDatabaseTables and SlickDatabaseService
 */
object SlickDatabaseUtil {
  def getDBUtil(_name: String = "default")(implicit app: Application): DatabaseService = {
    new {
      val application = app
      override val name = _name
    } with SlickDatabaseTables with SlickDatabaseService
  }
}
