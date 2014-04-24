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

import play.api.libs.json.{Json, JsPath, Writes, Reads}
import play.api.libs.functional.syntax._
import play.api.data.validation.ValidationError

import Models._
/**
 * Object JsonWrappers defines the Reads and writes that convert native Scala type into/from JSON
 */
object JsonWrappers {
  
  implicit val ejournalPublisherWrites = new Writes[EJournalPublisher] {
    def writes(publisher: EJournalPublisher) = Json.obj(
      "name" -> publisher.name,
      "code" -> publisher.code,
      "url" -> publisher.url
    )
  }
/**
 * the name of the journal is encoded as journal.name
 * the url of the journal is encoded as journal.url
 * the year accessed of the journal is encoded as journal.accessYear
 * For ex: krooked-name of journal is encoded as journal.krooked
 * 	   url-krooked journal is encoded as journal.url
 * 	   yearaccessed is encoded as krooked.2014
 */   
  implicit val ejournalWrites = new Writes[EJournal] {
    def writes(journal: EJournal) = Json.obj(
      "name" -> journal.name,
      "year accessed" -> journal.accessYear,
      "url" -> journal.url
    )
  }
/**
 * the name of the publisher is encoded as publisher.name
 * the url of the publisher is encoded as publisher.url
 * the code of the publisher is encoded as publisher.code 
 * EJournal("Krooked", 2014, "http://www.krooked.com", "abc") is converted to
 * {
 * "name" : Krooked,
 * "year accessed" : 2014
 * "url" : http://www.krooked.com
 * }
 */ 
  implicit val ebookPublisherWrites = new Writes[EBookPublisher] {
    def writes(publisher: EBookPublisher) = Json.obj(
      "name" -> publisher.name,
      "code" -> publisher.code,
      "url" -> publisher.url
    )
  }
/**
 * the name of the book is encoded as book.name
 * the url of the book is encoded as book.url
 * EJournal("Krooked", 2014, "http://www.krooked.com", "abc") is converted to
 * {
 * "name" : Krooked,
 * "year accessed" : 2014
 * "url" : http://www.krooked.com
 * }
 */  
  implicit val ebookWrites = new Writes[EBook] {
    def writes(book: EBook) = Json.obj(
      "name" -> book.name,
      "url" -> book.url
    )
  }
/**
 * the name of the database is encoded as database.name
 * the url of the database is encoded as database.url
 * EJournal("Krooked", 2014, "http://www.krooked.com", "abc") is converted to
 * { 
 * "name" : Krooked,
 * "year accessed" : 2014
 * "url" : http://www.krooked.com
 * }
 */  
  implicit val edatabaseWrites = new Writes[EDatabase] {
    def writes(database: EDatabase) = Json.obj(
      "name" -> database.name,
      "url" -> database.url
    )
  }
/**
 * name is read as a string 
 * url is read as a string
 * code is read as a string
 * ejournalpublisher.name is read as name
 * ejournalpublisher.url is read as url
 * ejournalpulisher.code is read as code
 * ejournalpublisher.code,ejournalpublisher.url,ejournalpublisher.url defaultly reads as code 
 */   
  val ejournalPublisherReads: String => Reads[EJournalPublisher] = code => (
    (JsPath \ "name").read[String] and
    (JsPath \ "url").read[String] and
    (JsPath \ "code").read[String](defaultValueReads(code))
  )(EJournalPublisher)
/**
 * name is read as a string 
 * url is read as a string
 * year is read as a string
 * ejournal.name is read as name
 * ejournal.url is read as url
 * ejournal.yearaccessed is read as Accessed Year
 * ejournal.publishercode is read as Publisher code
 * ejournalpublisher.name,ejournalpublisher.url,ejournalpublisher.yearaccessed,ejournal.publishercode defaultly reads as code 
 */     
  val ejournalReads: (String, String) => Reads[EJournal] = (code, name) => (
    (JsPath \ "name").read[String](defaultValueReads(name)) and
    (JsPath \ "year accessed").read(Reads.of[Int] keepAnd Reads.min(1950)) and
    (JsPath \ "url").read[String] and
    (JsPath \ "publisherCode").read[String](defaultValueReads(code))
  )(EJournal)
/**
 * name is read as a string 
 * url is read as a string
 * code is read as a string
 * ebookpublisher.name is read as name
 * ebookpublisher.url is read as url
 * ebookpulisher.code is read as code
 * ebookpublisher.code,ebookpublisher.url,ebookpublisher.name defaultly reads as code 
 */     
  val ebookPublisherReads: String => Reads[EBookPublisher] = code => (
    (JsPath \ "name").read[String] and
    (JsPath \ "url").read[String] and
    (JsPath \ "code").read[String](defaultValueReads(code))
  )(EBookPublisher)
/**
 * name is read as a string 
 * url is read as a string
 * publishercode is read as a string
 * ebook.name is read as name
 * ebook.url is read as url
 * ebook.publishercode is read as code
 * ebook.code,ebook.url,ebook.publishercode defaultly reads as code 
 */     
  val ebookReads: (String, String) => Reads[EBook] = (code, name) => (
    (JsPath \ "name").read[String](defaultValueReads(name)) and
    (JsPath \ "url").read[String] and
    (JsPath \ "publisherCode").read[String](defaultValueReads(code))
  )(EBook)
/**
 * name is read as a string 
 * url is read as a string
 * edatabase.name is read as name
 * edatabase.url is read as url
 * edatabase.name,edatabase.url defaultly reads as code 
 */     
  val edatabaseReads: (String) => Reads[EDatabase] = (name) => (
    (JsPath \ "name").read[String](defaultValueReads(name)) and
    (JsPath \ "url").read[String]
  )(EDatabase)
/**
 * Reads noerror if its mapped correctly and 
 * Reads the filtered value else 
 * Returns error for unexpected value
 */   
  def defaultValueReads[T](default: T)(implicit r: Reads[T]) = {
    Reads.optionNoError[T].map(_.getOrElse(default)) keepAnd
    Reads.filter[T](ValidationError("validate.error.unexpected.value"))(_ == default)
  }
}
