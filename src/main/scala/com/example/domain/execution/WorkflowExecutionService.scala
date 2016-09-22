package com.example.domain.execution

import com.example.domain.WorkflowFacade
import spray.http.MediaTypes._
import spray.http.StatusCodes._
import spray.routing.HttpService
import spray.httpx.SprayJsonSupport
import spray.json.DefaultJsonProtocol

trait WorkflowExecutionCreationService extends HttpService with DefaultJsonProtocol with SprayJsonSupport {
    val workflowFacade: WorkflowFacade

    case class WorkflowExecutionCreationResponse(workflow_execution_id: String)
    implicit val workflowExecutionCreationResponseFormat = jsonFormat1(WorkflowExecutionCreationResponse)

    val workflowExecutionCreationRoute =
        path("workflows" / Segment / "executions") { (workflowId) =>
            post {
                respondWithMediaType(`application/json`) {
                    val maybeExecutionId = workflowFacade.createExecution(workflowId)

                    maybeExecutionId match {
                        case Some(id) => complete(Created, WorkflowExecutionCreationResponse(id))
                        case None => complete(NotFound)
                    }
                }
            }
        }
}

trait WorkflowExecutionIncrementService extends HttpService with DefaultJsonProtocol with SprayJsonSupport {
    val workflowFacade: WorkflowFacade

    val workflowExecutionIncrementRoute =
        path("workflows" / Segment / "executions" / Segment) { (workflowId, workflowExecutionId) =>
            put {
                respondWithMediaType(`application/json`) {
                    val maybeIncremented = workflowFacade.incrementExecution(workflowId, workflowExecutionId)

                    maybeIncremented match {
                        case Some(true) => complete(NoContent)
                        case Some(false) => complete(BadRequest)
                        case None => complete(NotFound)
                    }
                }
            }
        }
}

trait WorkflowExecutionCompleteService extends HttpService with DefaultJsonProtocol with SprayJsonSupport {
    val workflowFacade: WorkflowFacade

    case class WorkflowExecutionCompleteResponse(finished: Boolean)
    implicit val workflowExecutionCompleteResponseFormat = jsonFormat1(WorkflowExecutionCompleteResponse)

    val workflowExecutionCompleteRoute =
        path("workflows" / Segment / "executions" / Segment) { (workflowId, workflowExecutionId) =>
            get {
                respondWithMediaType(`application/json`) {
                    val maybeCompleted = workflowFacade.isExecutionCompleted(workflowId, workflowExecutionId)

                    maybeCompleted match {
                        case Some(completed) => complete(OK, WorkflowExecutionCompleteResponse(completed))
                        case None => complete(NotFound)
                    }
                }
            }
        }
}