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
package controllers;

import play.api.mvc.QueryStringBindable
/**
 *Creates an object which imports the formencapsulators of BookSearch function
 *function booksearch creates a bindable query string and
 *searches the book based on the params
 *@param params creates a new Map of string and sequence of string
 *@param option creates a new bindable booksearch
 *@param queryStringBinder creates new BookSearch list
 */

object Binders {
  
  import FormEncapsulators.BookSearch
  
  implicit def queryStringBinder = new QueryStringBindable[BookSearch] {
    override def bind(key: String, params: Map[String, Seq[String]]): 
    Option[Either[String, BookSearch]] = {
      type b = QueryStringBindable[Option[String]]
      val reducedMap = params - "isbn" - "title" - "author"
      for {
        isbn <- implicitly[b].bind("isbn", params)
        title <- implicitly[b].bind("title", params)
        author <- implicitly[b].bind("author", params)
      } yield 
/**
  *Returns the matched book based on the bindsearch if found
  * orelse returns unable to bind a booksearch 
 */   
             {
        (isbn, title, author, reducedMap.size) match {
          case (Right(isbn), Right(title), Right(author), 0) => Right(BookSearch(isbn, title, author))
          case _ => Left("invalid search parameters")
        }
      }
    }

/**
 *Override method overrides the previous result of the function
 *with the string b which would replace the previous results with
 *empty values 
 */
    override def unbind(key: String, bs: BookSearch): String = {
      type b = QueryStringBindable[String]
      val str = bs.isbn.map(implicitly[b].unbind("isbn", _) + "&").getOrElse("") +
                bs.title.map(implicitly[b].unbind("title", _) + "&").getOrElse("") +
                bs.author.map(implicitly[b].unbind("author", _) + "&").getOrElse("")
      str.stripSuffix("&")
    }
  }
}
