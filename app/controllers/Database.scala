/**
 * BetLiMS is a Library management Software.
 * Copyright (C) 2014  radsaggi, shankdude, garuda19, shashidonthiri9

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

import play.api.db._
import play.api.Play.current 

import FormEncapsulators._;
/**
 *Defines an object which consists of parameters
 *@param isbn,title,author,branch,user,code,url,publisherCode,userid,name are taken as valid strings
 *@param year,copies,accessYear are taken as valid integers
 *Objects are defined for each funcitonality
 */

object Models {
  case class Book(isbn: String, title: String, author: String, copies: Int)
  
  sealed abstract class User(val userid: String)
  case class StudentUser(override val userid: String, name: String, year: Int, branch: String) extends User(userid)
  case class AdminUser(override val userid: String, name: String) extends User(userid)
  
  case class EJournalPublisher(name: String, code: String, url: String)
  case class EJournal(name: String, accessYear: Int, url: String, publisherCode: String)
  case class EBookPublisher(name: String, code: String, url: String)
  case class EBook(name: String, url: String, publisherCode: String)
  case class EDatabase(name: String, url: String)
}

/**
 *Defines each case of functionalities like adduser,booksearch,removeuser,listEbookPublisher,addEJournal etc 
 *from both admin's panel and user's panel
 *@params lists are defined for each of them
 */
trait DatabaseService {

  import Models._

  init()
/**
 *Defines case for booksearch
 *@param List[Book] creates list of books
 */
  def booksearch(q: BookSearch): List[Book]
/**
 *Defines case for aunticating student and adminuser
 *@param Option[StudentUser]  and Option[AdminUser] creates list of students and admins
 */
  def authenticateStudentUser(q: UserLogin): Option[StudentUser]  
  def authenticateAdminUser(q: UserLogin): Option[AdminUser]
/**
 *Defines case for adding and removing EJournalPublishers,add or remove EJournals
 *@param Option[EJournal],Option[Unit],Option[Unit],List[EJournalPublisher],Unit are created
 */  
  def allEJournalPublishers(): List[EJournalPublisher]  
  def addEJournalPublisher(publisher: EJournalPublisher): Unit  
  def removeEJournalPublisher(publisher: EJournalPublisher): Unit  
  def allEJournals(publisherCode: String): Option[List[EJournal]]  
  def addEJournal(journal: EJournal): Option[Unit]  
  def removeEJournal(journal: EJournal): Option[Unit]
/**
 *Defines case for add,remove EBookPublisher and add,remove Ebook 
 *@param List[EBookPublisher],Option[list[EBook]],Option[Unit],Option[Unit], are created,
 *Unit is created as instant case
 */  
  def allEBookPublishers(): List[EBookPublisher]  
  def addEBookPublisher(publisher: EBookPublisher): Unit  
  def removeEBookPublisher(publisher: EBookPublisher): Unit  
  def allEBooks(publisherCode: String): Option[List[EBook]]  
  def addEBook(book: EBook): Option[Unit]  
  def removeEBook(book: EBook): Option[Unit]
/**
 *Defines case for addEDatabase,removeEDatabase
 *@param Units are created as instant cases
 */  
  def allEDatabases(): List[EDatabase]
  def addEDatabase(book: EDatabase): Unit
  def removeEDatabase(book: EDatabase): Unit

  def init(): Unit
}

/**
 * Object that Creates a databaseService
 *@param databaseService creates a new DatabaseService
 */ 
trait DatabaseServiceProvider {
  def databaseService: DatabaseService
}
