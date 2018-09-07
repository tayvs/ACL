package test.tayvs.acl.actor

import akka.actor.{Actor, ActorRef, Props}
import akka.pattern._
import akka.util.Timeout

import scala.collection.mutable
import scala.concurrent.Future

class SessionManager extends Actor {
  implicit val timeout: Timeout = ???

  val sessions = mutable.HashMap.empty[String, ActorRef]

  override def receive: Receive = {
    case _ =>
  }

  def login(login: String, hash: String): String = {
    val token = ???
    val session = context.actorOf(Props[Session], login + token)
    sessions += ((login + token) -> session)
    token
  }

  def logout(login: String, token: String): Boolean = {
    sessions.remove(login + token).isDefined
  }

  def check(login: String, token: String, route: String): Future[Boolean] = {
    sessions.get(login + token)
      .map(_ ? Session.Check(login + token))
      .map(_.mapTo[Boolean])
      .getOrElse(Future.successful(false))
  }

  def isOnline(login: String, token: String) = {
    sessions.get(login + token).isDefined
  }

}
