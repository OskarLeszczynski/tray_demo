package com.example.domain

import java.time.{Duration, Instant}

import com.example.infrastructure.{SimpleWorkflowExecutionStorage, SimpleWorkflowStorage}
import com.example.domain.execution.WorkflowExecution
import com.example.domain.workflow.Workflow
import org.specs2.mock._
import org.specs2.mutable.Specification
import org.specs2.specification.Scope

class WorkflowFacadeSpec extends Specification {

    "WorkflowFacadeSpec" should {

        "create workflow" in new Context {
            // when
            val maybeWorkflowId = workflowFacade.createWorkflow(5)

            // then
            maybeWorkflowId mustEqual generatedId
        }

        "create workflow when id collision occurs" in new Context {
            // given
            workflowStorage.tryToSave(Workflow(generatedId, 5))

            // when
            val maybeWorkflowId = workflowFacade.createWorkflow(3)

            // then
            maybeWorkflowId mustEqual secondGeneratedId
        }

        "create workflow execution from existing workflow" in new Context {
            // given
            val workflowId = "workflow_id"
            workflowStorage.tryToSave(Workflow(workflowId, 5))

            // when
            val maybeExecutionId = workflowFacade.createExecution(workflowId)

            // then
            maybeExecutionId.get mustEqual generatedId
        }

        "create workflow execution when id collision occurs" in new Context {
            // given
            val workflowId = "workflow_id"
            workflowStorage.tryToSave(Workflow(workflowId, 5))
            workflowExecutionStorage.save(WorkflowExecution(generatedId, "workflow_id", 3, 3, Instant.now()))

            // when
            val maybeExecutionId = workflowFacade.createExecution(workflowId)

            // then
            maybeExecutionId.get mustEqual secondGeneratedId
        }

        "not create workflow execution from non existing workflow" in new Context {
            // when
            val maybeExecutionId = workflowFacade.createExecution("non_existing_workflow_id")

            // then
            maybeExecutionId.isEmpty mustEqual true
        }

        "increment execution step" in new Context {
            // given
            val workflowId = workflowFacade.createWorkflow(2)
            val executionId = workflowFacade.createExecution(workflowId).get

            // when
            val maybeIncrementationResult = workflowFacade.incrementExecution(workflowId, executionId)

            // then
            maybeIncrementationResult.get mustEqual true
        }

        "not increment execution step when workflow doesn't exist" in new Context {
            // given
            val workflowId = workflowFacade.createWorkflow(2)
            val executionId = workflowFacade.createExecution(workflowId).get

            // when
            val maybeIncrementationResult = workflowFacade.incrementExecution("non_existing_workflow_id", executionId)

            // then
            maybeIncrementationResult.isEmpty mustEqual true
        }

        "not increment execution step when execution doesn't exist" in new Context {
            // given
            val workflowId = workflowFacade.createWorkflow(2)

            // when
            val maybeIncrementationResult = workflowFacade.incrementExecution(workflowId, "non_existing_execution_id")

            // then
            maybeIncrementationResult.isEmpty mustEqual true
        }

        "not increment execution step when execution already finished" in new Context {
            // given
            val workflowId = workflowFacade.createWorkflow(2)
            val executionId = workflowFacade.createExecution(workflowId).get
            workflowFacade.incrementExecution(workflowId, executionId)
            workflowFacade.incrementExecution(workflowId, executionId)

            // when
            val maybeIncrementationResult = workflowFacade.incrementExecution(workflowId, executionId)

            // then
            maybeIncrementationResult.get mustEqual false
        }

        "inform when execution is completed" in new Context {
            // given
            val workflowId = workflowFacade.createWorkflow(1)
            val executionId = workflowFacade.createExecution(workflowId).get
            workflowFacade.incrementExecution(workflowId, executionId)

            // when
            val maybeIsCompleted = workflowFacade.isExecutionCompleted(workflowId, executionId)

            // then
            maybeIsCompleted.get mustEqual true
        }

        "inform when execution is not completed" in new Context {
            // given
            val workflowId = workflowFacade.createWorkflow(1)
            val executionId = workflowFacade.createExecution(workflowId).get

            // when
            val maybeIsCompleted = workflowFacade.isExecutionCompleted(workflowId, executionId)

            // then
            maybeIsCompleted.get mustEqual false
        }

        "not inform about execution completation when unknown workflow id" in new Context {
            // given
            val workflowId = workflowFacade.createWorkflow(1)
            val executionId = workflowFacade.createExecution(workflowId).get

            // when
            val maybeIsCompleted = workflowFacade.isExecutionCompleted("non_existing_workflow_id", executionId)

            // then
            maybeIsCompleted.isEmpty mustEqual true
        }

        "not inform about execution completation when unknown execution execution id" in new Context {
            // given
            val workflowId = workflowFacade.createWorkflow(1)

            // when
            val maybeIsCompleted = workflowFacade.isExecutionCompleted(workflowId, "non_existing_execution_id")

            // then
            maybeIsCompleted.isEmpty mustEqual true
        }

        "cleanUp completed and too old executions" in new Context {
            // given
            val completedId = "completed_id"
            val notCompletedId = "not_completed_id"
            val oldNotCompletedId = "old_not_completed_id"

            workflowExecutionStorage.save(WorkflowExecution(completedId, "workflow_id", 3, 3, Instant.now()))
            workflowExecutionStorage.save(WorkflowExecution(notCompletedId, "workflow_id", 2, 3, Instant.now()))
            val oldDate = Instant.now().minus(Duration.ofMinutes(2))
            workflowExecutionStorage.save(WorkflowExecution(oldNotCompletedId, "workflow_id", 2, 3, oldDate))

            // when
            workflowFacade.cleanUp()

            // then
            workflowExecutionStorage.get(completedId).isEmpty mustEqual true
            workflowExecutionStorage.get(oldNotCompletedId).isEmpty mustEqual true
            workflowExecutionStorage.get(notCompletedId).isDefined mustEqual true
        }
    }

    trait BaseContext extends Scope with Mockito {
        val generatedId = "first_test_id"
        val secondGeneratedId = "second_test_id"
        val idGenerator = mock[IdGenerator]
        idGenerator.generate() returns generatedId thenReturns secondGeneratedId
    }

    trait Context extends BaseContext {
        val workflowStorage = new SimpleWorkflowStorage
        val workflowExecutionStorage = new SimpleWorkflowExecutionStorage
        val workflowFacade = new WorkflowFacade(workflowStorage, workflowExecutionStorage, idGenerator)
    }
}
