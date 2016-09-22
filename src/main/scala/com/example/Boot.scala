package com.example

import java.util.concurrent.TimeUnit

import akka.actor.{ActorSystem, Props}
import akka.io.IO
import spray.can.Http
import akka.pattern.ask
import akka.util.Timeout
import com.example.infrastructure.Storage
import com.example.domain.{IdGenerator, WorkflowFacade}

import scala.concurrent.duration._

object Boot extends App {
    implicit val system = ActorSystem("on-spray-can")
    implicit val timeout = Timeout(5.seconds)

    val workflowFacade = new WorkflowFacade(Storage.workflows, Storage.workflowExecutions, new IdGenerator)

    val service = system.actorOf(Props(new ServiceActor(workflowFacade)), "tray-demo-service")
    IO(Http) ? Http.Bind(service, interface = "localhost", port = 9000)

    val cleaner = new Cleaner(workflowFacade)
    private val interval: FiniteDuration = FiniteDuration(1, TimeUnit.MINUTES)
    system.scheduler.schedule(interval, interval, cleaner)(executor = system.dispatcher)
}
