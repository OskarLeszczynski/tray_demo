package com.example.domain.workflow

import com.example.domain.WorkflowFacade
import spray.http.MediaTypes._
import spray.http._
import spray.httpx.SprayJsonSupport
import spray.json.DefaultJsonProtocol
import spray.routing._

trait WorkflowCreationService extends HttpService  with DefaultJsonProtocol with SprayJsonSupport {
    val workflowFacade: WorkflowFacade

    case class WorkflowCreationResponse(workflow_id: String)
    case class WorkflowCreationRequest(number_of_steps: Int)

    implicit val workflowCreationResponseJsonProtocol = jsonFormat1(WorkflowCreationResponse)
    implicit val workflowCreationRequestJsonProtocol = jsonFormat1(WorkflowCreationRequest)

    val workflowCreationRoute =
        path("workflows") {
            post {
                respondWithMediaType(`application/json`) {
                    entity(as[WorkflowCreationRequest]) { request =>
                        complete(
                            StatusCodes.Created,
                            WorkflowCreationResponse(workflowFacade.createWorkflow(request.number_of_steps))
                        )
                    }
                }
            }
        }
}