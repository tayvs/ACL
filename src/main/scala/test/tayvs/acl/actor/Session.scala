package test.tayvs.acl.actor

import akka.actor.{Actor, Cancellable, PoisonPill}

import scala.concurrent.duration._

class Session(login: String, ttl: Long, routes: Set[String]) extends Actor {
  import context._

  var expireSchedule: Cancellable = context.system.scheduler.scheduleOnce(ttl seconds, self, Session.Expire)

  override def receive: Receive = {
    case Session.Check(route) =>
      sender ! routes(route)
      expireSchedule.cancel()
      context.system.scheduler.scheduleOnce(ttl seconds, self, Session.Expire)

    case Session.Expire => self ! PoisonPill
  }

  override def postStop(): Unit = {
    super.postStop()
    expireSchedule.cancel()
  }

}

object Session {
  case object Expire
  case class Check(route: String)
}
