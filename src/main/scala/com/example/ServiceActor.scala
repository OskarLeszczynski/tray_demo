package com.example

import akka.actor.Actor
import spray.http._
import com.example.workflow.{WorkflowCreationService, WorkflowFacade}
import com.example.workflow.execution.{WorkflowExecutionCompleteService, WorkflowExecutionCreationService, WorkflowExecutionIncrementService}

class ServiceActor(facade: WorkflowFacade) extends Actor
    with WorkflowCreationService
    with WorkflowExecutionCreationService
    with WorkflowExecutionIncrementService
    with WorkflowExecutionCompleteService {
    def actorRefFactory = context

    def receive = runRoute(
        workflowCreationRoute ~
        workflowExecutionCreationRoute ~
        workflowExecutionIncrementRoute ~
        workflowExecutionCompleteRoute
    )

    override val workflowFacade: WorkflowFacade = facade
}