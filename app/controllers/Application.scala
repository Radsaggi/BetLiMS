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
 * Creates a BetLiMSApplication with a 
 * databaseService as specified
 *@param databaseService creates new databasedriver
 *@param BetLiMSApplication creates a new object
 *Returns the replaced databaseService
 */
object BetLiMSApplication extends BetLiMSApplication with BetLiMSRestfulServer {
/**
 * @override the  databasedriver is 
 * replaced with new databaseService
 * 
 */
  override lazy val databaseService = SlickDatabaseUtil.getDBUtil()(play.api.Play.current)
}


/**
 * Returns a Result,that represents the HTTP response 
 * message to send back to the web browser.
 */
trait BetLiMSApplication extends Controller with DatabaseServiceProvider{

  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }
/**
 *Defines the action to Search books 
 *in the database by the object named list
 *Retruns the HTTP response message 
 *to the browser which is a filtered list
 *@param bs is the object of searching a book by creating a result "list" 
 */
  def search(bs: BookSearch) = Action {
    val list = databaseService.booksearch(bs)
    Ok(views.html.search(Forms.searchForm.fill(bs))(Some(list)))
  }

/**
 *Accepts request from the forms and performs the action of booksearch
 *Redirects the result to the searchfunction
 *Sends a request and if search is successful it is 
 *redirected to the search interface orelse returns BadRequest    
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
 *Searches for the EJournalPublishers 
 *Returns the result to ejournalPublishers
 *@param ejournalPublishers searches in the database
 */ 
trait BetLiMSRestfulServer extends Controller with DatabaseServiceProvider {
  import JsonWrappers._
  
  def links_ejournalPublishers() = Action {
    val ejournalPublishers = databaseService.allEJournalPublishers()
    Ok(Json.toJson(ejournalPublishers))
  }

/**
 *Adds new book with the code of string provided and sends request
 *Returns Invalid EJournal Publisher if its not successful orelse takes valid request
 *@param code is a string which is the code of the book
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
 *Deletes book with the name of string provided by sending request 
 *Returns Invalid EJournal Publisher if its not successful orelse takes valid request
 *@param code is a string which is code of book 
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
 *Searches for the requested book 
 *Returns book if found in the list orelse
 *"product code not found"
 *@param code is the string which is the code of the book
 */  
  def links_ejournals(code: String) = Action {
    databaseService.allEJournals(code).map { list =>
      Ok(Json.toJson(list))
    } getOrElse {
      NotFound(s"Product code $code not found")
    }
  }
/**
 *Sends request to book with given string code and name to the database of ejournals
 *@param code is the string which is the code of the book
 *@param name is the string which is the name of the book 
 */
  def links_ejournals_insert(code: String, name: String) = Action(parse.json) { request =>
    val ejournalJSON = request.body
    ejournalJSON.validate[EJournal](ejournalReads(code, name)).fold (
/**
 *Returns invalid Ejournal if it is found to be invalid orelse book is added and validated
 *orelse Returns product code not found
 */
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
 *Send request to delete the book with the provided params 
 *@params code and name are strings provided
 *Returns Invalid EJournal if it fails 
 *else removes book if found orelse 
 *Returns Product code not found
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
 *Validates the all present ebookPublishers by overwriting
 */  
  def links_ebookPublishers() = Action {
    val ebookPublishers = databaseService.allEBookPublishers()
    Ok(Json.toJson(ebookPublishers))
  }
/**
 *Requests to insert an EBookPublisher by validating it first
 *Returns invalid EBook Publisher if its invalid orelse it is added
 *@param code is the string provided
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
 *Requests to delete the ebookpublisher
 *Returns Invalid EBook Publisher if fails orelse
 *Removes the EBookPublisher.
 @param code is the string provided
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
 *Returns Prodct code not found if fails orelse
 *overwrites the book in the list
 *@param code is the string  which is code of the book
 */  
  def links_ebooks(code: String) = Action {
    databaseService.allEBooks(code).map { list =>
      Ok(Json.toJson(list))
    } getOrElse {
      NotFound(s"Product code $code not found")
    }
  }

/**
 *Requests to insert a book of give parameter
 *Returns Invalid EBook if its invalid request
 *orelse adds book 
 *else returns "product code not found"
 *@params code and name are the string types as provided
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
 *Requests to delete a book based on given parameters 
 *Returns Invalid Book if it fails orelse
 *removes Book else
 *Returns product code not found
 *@params code and name are the strings 
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
 *Validates the new edatabase with the current existing edatabase
 */  
  def links_edatabases() = Action {
    val edatabase = databaseService.allEDatabases()
    Ok(Json.toJson(edatabase))
  }
  
/**
 *Requests to insert and validates the edatabase
 *Returns invalid EDatabase Publisher if it fails orelse
 *removes EDatabase
 *@params name is the string provided
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
 *Requests to delete and validate the edatabase 
 *Returns Invalid EDatabase if it fails
 *orelse removes EDatabase
 *@params name is the string provided 
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
