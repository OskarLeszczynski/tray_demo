package com.example.integration

import com.example.infrastructure.{SimpleWorkflowExecutionStorage, SimpleWorkflowStorage}
import com.example.domain.{IdGenerator, WorkflowFacade}
import com.example.domain.execution.{WorkflowExecutionCompleteService, WorkflowExecutionCreationService, WorkflowExecutionIncrementService}
import com.example.domain.workflow.WorkflowCreationService
import org.specs2.matcher.JsonMatchers
import org.specs2.mutable.Specification
import org.specs2.specification.Scope
import spray.http.{ContentTypes, HttpEntity, StatusCodes}
import spray.testkit.Specs2RouteTest

class WorkflowServiceAcceptanceSpec extends Specification with Specs2RouteTest {

    "WorkflowService" should {

        "handle workflow creation, execution and status check" in new Context {
            val requestJson =
                """
                  | {
                  |     "number_of_steps": 5
                  | }
                  |""".stripMargin

            Post("/workflows", HttpEntity(ContentTypes.`application/json`, requestJson)) ~> sealRoute(workflowCreationRoute) ~> check {
                status == StatusCodes.Created
                val workflowId = responseAs[WorkflowCreationResponse].workflow_id

                Post(s"/workflows/$workflowId/executions") ~> sealRoute(workflowExecutionCreationRoute) ~> check {
                    status mustEqual StatusCodes.Created
                    val executionId = responseAs[WorkflowExecutionCreationResponse].workflow_execution_id

                    Put(s"/workflows/$workflowId/executions/$executionId") ~> sealRoute(workflowExecutionIncrementRoute) ~> check {
                        status mustEqual StatusCodes.NoContent

                        Get(s"/workflows/$workflowId/executions/$executionId") ~> sealRoute(workflowExecutionCompleteRoute) ~> check {
                            status mustEqual StatusCodes.OK
                            responseAs[String] must /("finished" -> false)
                        }
                    }
                }
            }
        }
    }

    trait Context extends Scope with WorkflowCreationService with WorkflowExecutionCreationService with JsonMatchers
        with WorkflowExecutionIncrementService
        with WorkflowExecutionCompleteService {
        def actorRefFactory = system
        override val workflowFacade = new WorkflowFacade(new SimpleWorkflowStorage, new SimpleWorkflowExecutionStorage, new IdGenerator)
    }
}
