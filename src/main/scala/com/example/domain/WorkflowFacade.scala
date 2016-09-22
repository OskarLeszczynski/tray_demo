package com.example.domain

import java.time.{Duration, Instant}

import com.example.domain.execution.{WorkflowExecution, WorkflowExecutionStorage}
import com.example.domain.workflow.{Workflow, WorkflowStorage}

class WorkflowFacade(workflowStorage: WorkflowStorage,
                     workflowExecutionStorage: WorkflowExecutionStorage,
                     idGenerator: IdGenerator) {

    private val WORKFLOW_EXECUTION_TTL: Duration = Duration.ofMinutes(1)

    def cleanUp(): Unit = {
        workflowExecutionStorage.removeCompletedOrCreatedBefore(Instant.now().minus(WORKFLOW_EXECUTION_TTL))
    }

    def createWorkflow(numberOfSteps: Int): String = {
        val workflowId = idGenerator.generate()
        val saveSucceed = workflowStorage.tryToSave(Workflow(workflowId, numberOfSteps))

        if (saveSucceed) {
            workflowId
        } else {
            createWorkflow(numberOfSteps)
        }
    }

    def createExecution(workflowId: String): Option[String] = {
        val maybeWorkflow = workflowStorage.get(workflowId)

        val maybeExecutionId: Option[String] = maybeWorkflow.flatMap { workflow =>
            val workflowExecutionId = idGenerator.generate()
            val workflowExecution = WorkflowExecution(
                id = workflowExecutionId,
                workflowId = workflowId,
                currentStep = 0,
                maxStep = workflow.numberOfSteps,
                creationDate = Instant.now()
            )

            val saveSucceed = workflowExecutionStorage.tryToSave(workflowExecution)

            if (saveSucceed) {
                Some(workflowExecutionId)
            } else {
                createExecution(workflowId)
            }
        }

        maybeExecutionId
    }

    def incrementExecution(workflowId: String, executionId: String): Option[Boolean] = {
        val maybeWorkflow = workflowStorage.get(workflowId)

        val maybeIncrementationResult = maybeWorkflow.flatMap { workflow =>
            workflowExecutionStorage.get(executionId).map(incrementStepAndStoreExecution(_))
        }

        maybeIncrementationResult
    }

    private def incrementStepAndStoreExecution(workflowExecution: WorkflowExecution) = {
        workflowExecution.incrementIfPossible()
            .map(workflowExecutionStorage.save)
            .exists(storedIncrementedWorkflowExecution => true)
    }

    def isExecutionCompleted(workflowId: String, executionId: String): Option[Boolean] = {
        val maybeWorkflow = workflowStorage.get(workflowId)

        val maybeCompleted = maybeWorkflow.flatMap { workflow =>
            workflowExecutionStorage.get(executionId).map(_.isCompleted)
        }

        maybeCompleted
    }
}
