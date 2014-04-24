package controllers

import play.api.data.Form
import play.api.libs.json.Json
import play.api.mvc._

import Models._;
import FormEncapsulators._;

object BetLiMSApplication extends BetLiMSApplication with BetLiMSRestfulServer {
  override lazy val databaseService = SlickDatabaseUtil.getDBUtil()(play.api.Play.current)
}

trait BetLiMSApplication extends Controller 
                            with SecuredDatabaseExtension 
                            with DatabaseServiceProvider {

  def index = Action {
    Ok(views.html.index("Your new application is ready.")(None, loginForm))
  }

  def search(bs: BookSearch) = withAuth() { _ => _ =>
    val list = databaseService.booksearch(bs)
    Ok(views.html.search(Forms.searchForm.fill(bs))(Some(list))(None, loginForm))
  }

  def searchPost() = withAuth() { _ => implicit req =>
    println("Trying to bind form request")
    Forms.searchForm.bindFromRequest.fold (
      fe => BadRequest(views.html.search(fe)(None)(None, loginForm)),
      bs => Redirect(routes.BetLiMSApplication.search(bs))
    )
  }
  
  def postLoginRedirect = routes.BetLiMSApplication.index
  def postLoginBadRequest(errForm: Form[UserLogin]): String = ""
  def postLogout = routes.BetLiMSApplication.index
}

trait BetLiMSRestfulServer extends Controller with DatabaseServiceProvider {
  app: BetLiMSApplication =>
  import JsonWrappers._
  
  def links_ejournalPublishers() = withAuth() { _ => _ =>
    val ejournalPublishers = databaseService.allEJournalPublishers()
    Ok(Json.toJson(ejournalPublishers))
  }
  
  def links_ejournalPublishers_insert(code: String) = withAuth(parse.json) { 
    case Some(x: AdminUser) => request =>
    val ejPublisherJSON = request.body
    ejPublisherJSON.validate[EJournalPublisher](ejournalPublisherReads(code)).fold (
      invalid => BadRequest("Invalid EJournal Publisher"),
      valid => {
        databaseService.addEJournalPublisher(valid)
        Ok
      }
    )
    case _ => _ => Unauthorized
  }
  
  def links_ejournalPublishers_delete(code: String) = withAuth(parse.json) {
    case Some(x: AdminUser) => request =>
    val ejPublisherJSON = request.body
    ejPublisherJSON.validate[EJournalPublisher](ejournalPublisherReads(code)).fold (
      invalid => BadRequest("Invalid EJournal Publisher"),
      valid => {
        databaseService.removeEJournalPublisher(valid)
        Ok
      }
    )
    case _ => _ => Unauthorized
  }
  
  def links_ejournals(code: String) = withAuth() { _ => _ =>
    databaseService.allEJournals(code).map { list =>
      Ok(Json.toJson(list))
    } getOrElse {
      NotFound(s"Product code $code not found")
    }
  }
  
  def links_ejournals_insert(code: String, name: String) = withAuth(parse.json) { 
    case Some(x: AdminUser) => request =>
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
    case _ => _ => Unauthorized
  }
  
  def links_ejournals_delete(code: String, name: String) = withAuth(parse.json) {
    case Some(x: AdminUser) => request =>
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
    case _ => _ => Unauthorized
  }
  
  def links_ebookPublishers() = withAuth() { _ => _ =>
    val ebookPublishers = databaseService.allEBookPublishers()
    Ok(Json.toJson(ebookPublishers))
  }
  
  def links_ebookPublishers_insert(code: String) = withAuth(parse.json) {
    case Some(x: AdminUser) => request =>
    val ebPublisherJSON = request.body
    ebPublisherJSON.validate[EBookPublisher](ebookPublisherReads(code)).fold (
      invalid => BadRequest("Invalid EBook Publisher"),
      valid => {
        databaseService.addEBookPublisher(valid)
        Ok
      }
    )
    case _ => _ => Unauthorized
  }
  
  def links_ebookPublishers_delete(code: String) = withAuth(parse.json) { 
    case Some(x: AdminUser) => request =>
    val ebPublisherJSON = request.body
    ebPublisherJSON.validate[EBookPublisher](ebookPublisherReads(code)).fold (
      invalid => BadRequest("Invalid EBook Publisher"),
      valid => {
        databaseService.removeEBookPublisher(valid)
        Ok
      }
    )
    case _ => _ => Unauthorized
  }
  
  def links_ebooks(code: String) = withAuth() { _ => _ =>
    databaseService.allEBooks(code).map { list =>
      Ok(Json.toJson(list))
    } getOrElse {
      NotFound(s"Product code $code not found")
    }
  }
  
  def links_ebooks_insert(code: String, name: String) = withAuth(parse.json) {
    case Some(x: AdminUser) => request =>
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
    case _ => _ => Unauthorized
  }
  
  def links_ebooks_delete(code: String, name: String) = withAuth(parse.json) { 
    case Some(x: AdminUser) => request =>
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
    case _ => _ => Unauthorized
  }
  
  def links_edatabases() = withAuth() { _ => _ =>
    val edatabase = databaseService.allEDatabases()
    Ok(Json.toJson(edatabase))
  }
  
  def links_edatabases_insert(name: String) = withAuth(parse.json) { 
    case Some(x: AdminUser) => request =>
    val edatabaseJSON = request.body
    edatabaseJSON.validate[EDatabase](edatabaseReads(name)).fold (
      invalid => BadRequest("Invalid EDatabase Publisher"),
      valid => {
        databaseService.removeEDatabase(valid)
        Ok
      }
    )
    case _ => _ => Unauthorized
  }
  
  def links_edatabases_delete(name: String) = withAuth(parse.json) { 
    case Some(x: AdminUser) => request =>
    val edatabaseJSON = request.body
    edatabaseJSON.validate[EDatabase](edatabaseReads(name)).fold (
      invalid => BadRequest("Invalid EDatabase Publisher"),
      valid => {
        databaseService.removeEDatabase(valid)
        Ok
      }
    )
    case _ => _ => Unauthorized
  }
  
}