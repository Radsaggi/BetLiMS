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
 *Object JsonWrappers created to define method
 *@param name,code,url are strings
 *Name,Code,URL are directed to respective publisher details
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
 *@param name,url are strings 
 *@param year accessed is integer
 *year accessed,url,name are directed to respective journal details
 */   
  implicit val ejournalWrites = new Writes[EJournal] {
    def writes(journal: EJournal) = Json.obj(
      "name" -> journal.name,
      "year accessed" -> journal.accessYear,
      "url" -> journal.url
    )
  }
/**
 *@param name,code,url are strings
 *name,code,url are directed to respective publisher details 
 */ 
  implicit val ebookPublisherWrites = new Writes[EBookPublisher] {
    def writes(publisher: EBookPublisher) = Json.obj(
      "name" -> publisher.name,
      "code" -> publisher.code,
      "url" -> publisher.url
    )
  }
/**
 *@param name,url are strings
 *name,url are directed to respective publisher details
 */  
  implicit val ebookWrites = new Writes[EBook] {
    def writes(book: EBook) = Json.obj(
      "name" -> book.name,
      "url" -> book.url
    )
  }
/**
 *name,url are strings
 *name,url are directed to respective database details
 */  
  implicit val edatabaseWrites = new Writes[EDatabase] {
    def writes(database: EDatabase) = Json.obj(
      "name" -> database.name,
      "url" -> database.url
    )
  }
/**
 *@param name,url,code are the strings
 *This defines that each JsPath directs to do each task
 */   
  val ejournalPublisherReads: String => Reads[EJournalPublisher] = code => (
    (JsPath \ "name").read[String] and
    (JsPath \ "url").read[String] and
    (JsPath \ "code").read[String](defaultValueReads(code))
  )(EJournalPublisher)
/**
 *@param name,url,publishercode are the strings
 *@param yearaccessed is integer type
 *This defines that each JsPath directs to do each task
 */     
  val ejournalReads: (String, String) => Reads[EJournal] = (code, name) => (
    (JsPath \ "name").read[String](defaultValueReads(name)) and
    (JsPath \ "year accessed").read(Reads.of[Int] keepAnd Reads.min(1950)) and
    (JsPath \ "url").read[String] and
    (JsPath \ "publisherCode").read[String](defaultValueReads(code))
  )(EJournal)
/**
 *@param name,url,code are the strings
 *This defines that each JsPath directs to do each task
 */     
  val ebookPublisherReads: String => Reads[EBookPublisher] = code => (
    (JsPath \ "name").read[String] and
    (JsPath \ "url").read[String] and
    (JsPath \ "code").read[String](defaultValueReads(code))
  )(EBookPublisher)
/**
 *@param name,url,publishercode are the strings
 *This defines that each JsPath directs to do each task
 */     
  val ebookReads: (String, String) => Reads[EBook] = (code, name) => (
    (JsPath \ "name").read[String](defaultValueReads(name)) and
    (JsPath \ "url").read[String] and
    (JsPath \ "publisherCode").read[String](defaultValueReads(code))
  )(EBook)
/**
 *@param name,url are the strings
 *This defines that each JsPath directs to do each task
 */     
  val edatabaseReads: (String) => Reads[EDatabase] = (name) => (
    (JsPath \ "name").read[String](defaultValueReads(name)) and
    (JsPath \ "url").read[String]
  )(EDatabase)
/**
 *Reads noerror if its mapped correctly and 
 *Reads the filtered value else 
 *Returns error for unexpected value
 */   
  def defaultValueReads[T](default: T)(implicit r: Reads[T]) = {
    Reads.optionNoError[T].map(_.getOrElse(default)) keepAnd
    Reads.filter[T](ValidationError("validate.error.unexpected.value"))(_ == default)
  }
}
