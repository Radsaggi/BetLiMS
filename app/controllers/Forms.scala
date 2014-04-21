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

import play.api.data._
import play.api.data.Forms._
/**
 *Defines an object with certain parameters as specified
 *Defines searchForm which searches for book based 
 *on the input from the form
 *@param isbn accepts string for search
 *@param title accepts string for search
 *@param author accepts string for search
 *Successfull if atleast one of the required parameters is not empty
 */
object Forms {
  
  import FormEncapsulators._;

  val searchForm: Form[BookSearch] = Form {
    mapping(
      "isbn" -> optional(text),
      "title" -> optional(text),
      "author" -> optional(text)
    )(BookSearch.apply)(BookSearch.unapply) verifying(
      "Form must contain at least one filled field",
      (bSearch: BookSearch) => bSearch.isDefined
    )
  }
/**
 *Checks validation of the form only is username and password are non empty
 */
  val loginForm: Form[UserLogin] = Form {
    mapping(
      "username" -> nonEmptyText,
      "password" -> nonEmptyText
    )(UserLogin.apply)(UserLogin.unapply)
  }

}
/**
 *Defines an object named FormEncapsulators which decides about the type of 
 *paramaters to take inputs in the paramaters for searching
 *@param isbn,title,author act as inputs for search
 */
object FormEncapsulators {
  case class BookSearch(isbn: Option[String], title: Option[String], author: Option[String]) {
    def isDefined = isbn.isDefined || title.isDefined || author.isDefined
  }

  case class UserLogin(username: String, password: String)
}
