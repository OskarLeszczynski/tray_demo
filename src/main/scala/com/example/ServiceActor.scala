package com.example

import akka.actor.Actor
import spray.http._
import com.example.domain.WorkflowFacade
import com.example.domain.execution.{WorkflowExecutionCompleteService, WorkflowExecutionCreationService, WorkflowExecutionIncrementService}
import com.example.domain.workflow.WorkflowCreationService

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