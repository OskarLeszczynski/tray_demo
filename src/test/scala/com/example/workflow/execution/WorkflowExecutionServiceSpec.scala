package com.example.workflow.execution

import com.example.workflow.{WorkflowCreationService, WorkflowFacade}
import org.specs2.matcher.JsonMatchers
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import org.specs2.specification.Scope
import spray.http.{ContentTypes, HttpEntity, StatusCodes}
import spray.testkit.Specs2RouteTest
import org.mockito.Matchers.anyString

class WorkflowExecutionServiceSpec extends Specification with Specs2RouteTest {

    "WorkflowExecutionCreationService" should {

        "return a 201 and created execution id for POST request to /workflows/<workflow_id>/executions path" in new Context {
            val workflowId = "some_workflow_id"
            workflowFacade.createExecution(workflowId) returns Some(generatedId)

            Post(s"/workflows/$workflowId/executions") ~> sealRoute(workflowExecutionCreationRoute) ~> check {
                status mustEqual StatusCodes.Created
                responseAs[String] must /("workflow_execution_id" -> generatedId)
            }
        }

        "return a 404 for POST request to /workflows/<unknown_workflow_id>/executions path" in new Context {
            val unknownWorkflowId = "unknown_workflow_id"
            workflowFacade.createExecution(unknownWorkflowId) returns None

            Post(s"/workflows/$unknownWorkflowId/executions") ~> sealRoute(workflowExecutionCreationRoute) ~> check {
                status mustEqual StatusCodes.NotFound
            }
        }

        "return a 204 for PUT request to /workflows/<workflow_id>/executions/<execution_id> path" in new Context {
            val workflowId = "workflow_id"
            val executionId = "execution_id"
            workflowFacade.incrementExecution(workflowId, executionId) returns Some(true)

            Put(s"/workflows/$workflowId/executions/$executionId") ~> sealRoute(workflowExecutionIncrementRoute) ~> check {
                status mustEqual StatusCodes.NoContent
            }
        }

        "return a 400 for PUT request to /workflows/<workflow_id>/executions/<completed_execution_id> path" in new Context {
            val workflowId = "workflow_id"
            val completedExecutionId = "completed_execution_id"
            workflowFacade.incrementExecution(workflowId, completedExecutionId) returns Some(false)

            Put(s"/workflows/$workflowId/executions/$completedExecutionId") ~> sealRoute(workflowExecutionIncrementRoute) ~> check {
                status mustEqual StatusCodes.BadRequest
            }
        }

        "return a 404 for PUT request to /workflows/<workflow_id>/executions/<unknown_execution_id> path" in new Context {
            val workflowId = "workflow_id"
            val unknownExecutionId = "unknown_execution_id"
            workflowFacade.incrementExecution(workflowId, unknownExecutionId) returns None

            Put(s"/workflows/$workflowId/executions/$unknownExecutionId") ~> sealRoute(workflowExecutionIncrementRoute) ~> check {
                status mustEqual StatusCodes.NotFound
            }
        }

        "return a 200 and execution completation status for GET request to /workflows/<workflow_id>/executions/<execution_id> path" in new Context {
            val workflowId = "workflow_id"
            val executionId = "execution_id"
            workflowFacade.isExecutionCompleted(workflowId, executionId) returns Some(false)

            Get(s"/workflows/$workflowId/executions/$executionId") ~> sealRoute(workflowExecutionCompleteRoute) ~> check {
                status mustEqual StatusCodes.OK
                responseAs[String] must /("finished" -> false)
            }
        }

        "return a 404 for GET request to /workflows/<workflow_id>/executions/<execution_id> path" in new Context {
            val workflowId = "workflow_id"
            val executionId = "execution_id"
            workflowFacade.isExecutionCompleted(workflowId, executionId) returns None

            Get(s"/workflows/$workflowId/executions/$executionId") ~> sealRoute(workflowExecutionCompleteRoute) ~> check {
                status mustEqual StatusCodes.NotFound
            }
        }
    }

    trait Context extends Scope with WorkflowExecutionCreationService with Mockito with JsonMatchers
        with WorkflowExecutionIncrementService
        with WorkflowExecutionCompleteService {
        def actorRefFactory = system
        override val workflowFacade = mock[WorkflowFacade]
        val generatedId = "some_id"
    }
}
