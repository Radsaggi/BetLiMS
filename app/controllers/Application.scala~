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

import play.api.data.Form
import play.api.libs.json.Json
import play.api.mvc._

import Models._;
import FormEncapsulators._;
/**
 * The main controller for BetLiMS server-BetLiMSApplication extends BetLiMSApplication 
 * based on injected dependency  using Slick as frontend
 */
object BetLiMSApplication extends BetLiMSApplication with BetLiMSRestfulServer {
/**
 * @Overrrides databaseService and  
 * injects dependency uing slick as frontend
 * 
 */
  override lazy val databaseService = SlickDatabaseUtil.getDBUtil()(play.api.Play.current)
}


/**
 * Represents the HTTP response 
 * and sends back response to the web browser.
 */
trait BetLiMSApplication extends Controller with DatabaseServiceProvider{

  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }
/**
 * Defines function to search books in the database
 * Represents the HTTP response to send to the
 * browser which is a filtered list
 * @param bs  is BookSearch 
 */
  def search(bs: BookSearch) = Action {
    val list = databaseService.booksearch(bs)
    Ok(views.html.search(Forms.searchForm.fill(bs))(Some(list)))
  }

/**
 * Defines function searchpost  
 * Redirects the result to the searchpost function
 * If search is successful it is 
 * redirected to the search interface or else returns BadRequest    
 */
  def searchPost() = Action { implicit request =>
    println("Trying to bind form request")
    Forms.searchForm.bindFromRequest.fold (
      fe => BadRequest(views.html.search(fe)(None)),
      bs => Redirect(routes.BetLiMSApplication.search(bs))
    )
  }

}
/**
 * Defines function links_ejournalPublishers   
 * Sends the list of ejournalPublishers by converting  
 * ejournalPublishers to Json 
 */ 
trait BetLiMSRestfulServer extends Controller with DatabaseServiceProvider {
  import JsonWrappers._
  
  def links_ejournalPublishers() = Action {
    val ejournalPublishers = databaseService.allEJournalPublishers()
    Ok(Json.toJson(ejournalPublishers))
  }

/**
 * Defines the function links_ejournalPublishers_inserts that
 * inserts new book.
 * Validates ejournalPublisher and if invalid-"Invalid EJournal Publisher"         
 * or else inserts EjournalPublisher  
 * @param code-code of the ejournalPublisher
 */  
  def links_ejournalPublishers_insert(code: String) = Action(parse.json) { request =>
    val ejPublisherJSON = request.body
    ejPublisherJSON.validate[EJournalPublisher](ejournalPublisherReads(code)).fold (
      invalid => BadRequest("Invalid EJournal Publisher"),
      valid => {
        databaseService.addEJournalPublisher(valid)
        Ok
      }
    )
  }
/**
 * Deletes ejournalPublisher based on injected dependency 
 * Validates EJournalPublisher and if invalid-"Invalid EJournal Publisher"
 * or else removes the EJournalPublisher
 * @param code is a string which is code of book 
 */  
  def links_ejournalPublishers_delete(code: String) = Action(parse.json) { request =>
    val ejPublisherJSON = request.body
    ejPublisherJSON.validate[EJournalPublisher](ejournalPublisherReads(code)).fold (
      invalid => BadRequest("Invalid EJournal Publisher"),
      valid => {
        databaseService.removeEJournalPublisher(valid)
        Ok
      }
    )
  }
/**
 * Defines the function links_ejournals 
 * Tries to match with the book in the list  
 * Converts list to Json format
 * @param code-code of the book
 */  
  def links_ejournals(code: String) = Action {
    databaseService.allEJournals(code).map { list =>
      Ok(Json.toJson(list))
    } getOrElse {
      NotFound(s"Product code $code not found")
    }
  }
/**
 * Defines function links_ejournals_inserts  
 * @param code-the code of the ejournal
 * @param name-the name of the ejournal
 * Checks the validity of ejournalJSON
 * If found to be invalid JSON sends badrequest or else 
 * Adds EJournal  or else sends "Product code not found"
 */
  def links_ejournals_insert(code: String, name: String) = Action(parse.json) { request =>
    val ejournalJSON = request.body
    ejournalJSON.validate[EJournal](ejournalReads(code, name)).fold (
      invalid => BadRequest("Invalid EJournal"),
      valid => {
        databaseService.addEJournal(valid).map { _ =>
          Ok
        } getOrElse {
          NotFound(s"Product code $code not found")
        }
      }
    )
  }
 
/**
 * Defines a function links_ejournals_delete 
 * @params code and name
 * Validates eJournalJSON and if invalid sends BadRequest 
 * If valid  tries to remove EJournal or else
 * sends Product code not found  
 */    
  def links_ejournals_delete(code: String, name: String) = Action(parse.json) { request =>
    val ejournalJSON = request.body
    ejournalJSON.validate[EJournal](ejournalReads(code, name)).fold (
      invalid => BadRequest("Invalid EJournal"),
      valid => {
        databaseService.removeEJournal(valid).map { _ =>
          Ok
        } getOrElse {
          NotFound(s"Product code $code not found")
        }
      }
    )
  }
/**
 * Converts all EBookPublishers to JSON 
 */  
  def links_ebookPublishers() = Action {
    val ebookPublishers = databaseService.allEBookPublishers()
    Ok(Json.toJson(ebookPublishers))
  }
/** 
 * Defines a function links_ebookPublishers_insert 
 * Validates the EBookPublisher to JSON and if it is
 * invalid then sends BadRequest or else 
 * inserts the valid EBookPublisher
 * @param code-code of ebookPublisher 
 */  
  def links_ebookPublishers_insert(code: String) = Action(parse.json) { request =>
    val ebPublisherJSON = request.body
    ebPublisherJSON.validate[EBookPublisher](ebookPublisherReads(code)).fold (
      invalid => BadRequest("Invalid EBook Publisher"),
      valid => {
        databaseService.addEBookPublisher(valid)
        Ok
      }
    )
  }
 
/**
 * Defines a function links_ebookPublishers_delete to delete the ebookpublisher
 * Validates the EBookPublisher to JSON and if it is 
 * invalid then sends BadRequest or else
 * Removes the valid EBookPublisher.
 * @param code-code of the EBookPublisher
 */
  def links_ebookPublishers_delete(code: String) = Action(parse.json) { request =>
    val ebPublisherJSON = request.body
    ebPublisherJSON.validate[EBookPublisher](ebookPublisherReads(code)).fold (
      invalid => BadRequest("Invalid EBook Publisher"),
      valid => {
        databaseService.removeEBookPublisher(valid)
        Ok
      }
    )
  }
/**
 * Defines a funtion links_ebooks
 * The list is converted to JSON type
 * Tries to map the code with the valid EBooksJSON file
 * or else sends product code not found.
 * @param code-code of the book
 */  
  def links_ebooks(code: String) = Action {
    databaseService.allEBooks(code).map { list =>
      Ok(Json.toJson(list))
    } getOrElse {
      NotFound(s"Product code $code not found")
    }
  }

/**
 * Defines a function links_ebooks_insert to insert ebooks
 * Validates EBook to EBook JSON type 
 * If its found to be  invalid file it sends BadRequest  
 * or else adds book or else
 * sends "product code not found"
 * @params code and name-details of the ebook
 */   
  def links_ebooks_insert(code: String, name: String) = Action(parse.json) { request =>
    val ebookJSON = request.body
    ebookJSON.validate[EBook](ebookReads(code, name)).fold (
      invalid => BadRequest("Invalid EBook"),
      valid => {
        databaseService.addEBook(valid).map { _ =>
          Ok
        } getOrElse {
          NotFound(s"Product code $code not found")
        }
      }
    )
  }
/**
 * Defines a function links_ebooks_delete to delete ebooks  
 * Validates the EBooks to JSON type
 * If found to be invalid it sends BadRequest or else
 * Tries to remove  valid EBook or else
 * sends Product code not found
 * @params code and name are the strings 
 */ 
  def links_ebooks_delete(code: String, name: String) = Action(parse.json) { request =>
    val ebookJSON = request.body
    ebookJSON.validate[EBook](ebookReads(code, name)).fold (
      invalid => BadRequest("Invalid EBook"),
      valid => {
        databaseService.removeEBook(valid).map { _ =>
          Ok
        } getOrElse {
          NotFound(s"Product code $code not found")
        }
      }
    )
  }
/**
 * Defines links_edatabases to validate edatabase to Json
 */  
  def links_edatabases() = Action {
    val edatabase = databaseService.allEDatabases()
    Ok(Json.toJson(edatabase))
  }
  
/**
 * Defines links_edatabases_insert to insert edatabase
 * Validates EDatabase to edatabaseJSON 
 * If found to be invalid sends Invalid EDatabase Publisher or else
 * removes  EDatabase  
 * @params name is the string provided
 */ 
  def links_edatabases_insert(name: String) = Action(parse.json) { request =>
    val edatabaseJSON = request.body
    edatabaseJSON.validate[EDatabase](edatabaseReads(name)).fold (
      invalid => BadRequest("Invalid EDatabase Publisher"),
      valid => {
        databaseService.removeEDatabase(valid)
        Ok
      }
    )
  }

/**
 * Defines links_edatabases_delete to delete edatabase 
 * Validates EDatabase to edatabseJSON 
 * If found to be invalid sends BadRequest or else
 * removes the valid EDatabase
 * @params name-name of the edatabse 
 */  
  def links_edatabases_delete(name: String) = Action(parse.json) { request =>
    val edatabaseJSON = request.body
    edatabaseJSON.validate[EDatabase](edatabaseReads(name)).fold (
      invalid => BadRequest("Invalid EDatabase Publisher"),
      valid => {
        databaseService.removeEDatabase(valid)
        Ok
      }
    )
  }
  
}
