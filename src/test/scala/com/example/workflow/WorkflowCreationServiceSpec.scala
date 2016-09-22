package com.example.workflow

import org.mockito.Matchers.anyInt
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import org.specs2.specification.Scope
import spray.http.StatusCodes._
import spray.http._
import spray.testkit.Specs2RouteTest
import org.specs2.matcher.JsonMatchers

class WorkflowCreationServiceSpec extends Specification with Specs2RouteTest {

    "WorkflowCreationService" should {

        "return a 201 and created workflow id for POST request to /workflows" in new Context {
            workflowFacade.createWorkflow(anyInt) returns generatedWorkflowId

            val requestJson =
                """
                  | {
                  |     "number_of_steps": 5
                  | }
                  |""".stripMargin

            Post("/workflows", HttpEntity(ContentTypes.`application/json`, requestJson)) ~> sealRoute(workflowCreationRoute) ~> check {
                status mustEqual Created
                responseAs[String] must /("workflow_id" -> generatedWorkflowId)
            }
        }
    }

    trait Context extends Scope with WorkflowCreationService with Mockito with JsonMatchers {
        def actorRefFactory = system
        override val workflowFacade = mock[WorkflowFacade]
        val generatedWorkflowId = "some_id"
    }
}
