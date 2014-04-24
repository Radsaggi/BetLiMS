package controllers

import play.api.cache.{CacheAPI, CachePlugin}
import play.api.data.Form
import play.api.mvc._
import play.api.mvc.BodyParsers.parse.anyContent
import play.api.libs.iteratee.Done
import play.api.Play.current

import java.util.UUID

trait SecuredBase {

  type User
  val Cache: CacheAPI

  def readUser(request: RequestHeader): Option[UUID]
  def writeUser(u: UUID): Seq[(String, String)]

  val usernameField = play.api.Play.maybeApplication.flatMap {
    _.configuration.getString("session.username")
  } getOrElse ("username")

  final def withAuth[A](p: BodyParser[A] = anyContent)(f: => Option[User] => 
                                                      Request[A] => Result) = {
    EssentialAction {
      Action(p) { request =>
        val uuid = readUser(request)
        val user = Cache.get(uuid.toString) map (_.asInstanceOf[User])
        f(user)(request)
      }
    }
  }
  //  def withUser(f: User => Request[AnyContent] => Result) = withAuth { username => implicit request =>
  //    Some(findBySessionName(username)).map { user =>
  //      f(user)(request)
  //    }.getOrElse(onUnauthorized(request))
  //  }
  //  
  //  def onUnauthorized(request: RequestHeader) = Results.Redirect(routes.Auth.login)
  //
  //  def withAuth(f: => String => Request[AnyContent] => Result) = {
  //    Authenticated(username, onUnauthorized) { user =>
  //      Action(request => f(user)(request))
  //    }
  //  }
  //
  //  def Authenticated[A](userinfo: RequestHeader => Option[A],
  //    onUnauthorized: RequestHeader => Result)(action: A => EssentialAction): EssentialAction = {
  //
  //    EssentialAction { request =>
  //      userinfo(request).map { user =>
  //        action(user)(request)
  //      }.getOrElse {
  //        Done(onUnauthorized(request), Input.Empty)
  //      }
  //    }
  //  }
}

trait SecuredForms extends Controller { base: SecuredBase =>

  type UserLogin <: {
    def username: String
    def password: String
  }
  val loginForm: Form[UserLogin]

  def postLoginRedirect: Call
  def postLoginBadRequest(errForm: Form[UserLogin]): String
  def postLogout: Call

  def authenticate = Action { implicit request =>
    loginForm.bindFromRequest.fold(
      formWithErrors => BadRequest(postLoginBadRequest(formWithErrors)),
      user => Redirect(postLoginRedirect).
        withSession(writeUser(createUUID(user)): _*))
  }

  def logout = withAuth() {
    case Some(x) => req => 
      Cache.remove(readUser(req).get.toString)
      Redirect(postLogout).withNewSession.flashing(
        "success" -> "You are now logged out.")
    case _ => _ => Unauthorized
  }

  def createUUID(ul: UserLogin) = {
    var h1 = 1125899906842597L // prime
    for (ch <- ul.username.toCharArray) {
      h1 = 31 * h1 + ch
    }

    var h2 = 1099511628211L // prime
    for (ch <- ul.password.toCharArray) {
      h2 = 31 * h2 + ch
    }

    new UUID(h1, h2)
  }

}

trait SecuredDatabaseExtension extends SecuredBase
  with SecuredForms {
  database: DatabaseServiceProvider =>

  override val Cache = current.plugin[CachePlugin].get.api
  override type User = Models.User
  override type UserLogin = FormEncapsulators.UserLogin

  override val loginForm = Forms.loginForm(authenticateLocal(_).isDefined)

  override def readUser(request: RequestHeader) = {
    request.session.get(usernameField) map {
      UUID.fromString(_)
    }
  }

  override def writeUser(u: UUID) = Seq {
    usernameField -> u.toString
  }

  def authenticateLocal(ul: UserLogin) = {
    database.databaseService.authenticateStudentUser(ul).orElse {
      database.databaseService.authenticateAdminUser(ul)
    } map { user =>
      val uuid = createUUID(ul).toString
      Cache.set(uuid, user, 0)
      uuid
    }
  }
}