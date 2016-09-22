package com.example.infrastucture

import java.time.Instant

import com.example.domain.execution.WorkflowExecution
import com.example.infrastructure.SimpleWorkflowExecutionStorage
import org.specs2.mutable.Specification
import org.specs2.specification.Scope

class SimpleWorkflowExecutionStorageSpec extends Specification {

    "SimpleWorkflowExecutionStorage" should {
        "get stored workflow execution" in new Context {
            // given
            val expectedWorkflowExecution = WorkflowExecution("some_test_id", "workflow_id", 0, 5, Instant.now())
            workflowExecutionStorage.save(expectedWorkflowExecution)

            // when
            val maybeWorkflowExecution = workflowExecutionStorage.get(expectedWorkflowExecution.id)

            // then
            maybeWorkflowExecution.isDefined
            expectedWorkflowExecution == maybeWorkflowExecution.get
        }

        "save new workflow execution" in new Context {
            // given
            val workflowExecution = WorkflowExecution("some_test_id", "workflow_id", 0, 5, Instant.now())

            // when
            val saveResult = workflowExecutionStorage.tryToSave(workflowExecution)

            // then
            saveResult mustEqual true
        }

        "not save new workflow execution with already stored id" in new Context {
            // given
            val oldWorkflowExecution = WorkflowExecution("some_test_id", "workflow_id", 0, 5, Instant.now())
            val newWorkflowExecution = WorkflowExecution("some_test_id", "workflow_id", 0, 10, Instant.now())
            workflowExecutionStorage.save(oldWorkflowExecution)

            // when
            val saveResult = workflowExecutionStorage.tryToSave(newWorkflowExecution)

            // then
            saveResult mustEqual false
        }

        "save new version of workflow execution" in new Context {
            // given
            val oldVersionOfWorkflowExecution = WorkflowExecution("some_test_id", "workflow_id", 0, 5, Instant.now())
            val newVersionOfWorkflowExecution = WorkflowExecution("some_test_id", "workflow_id", 2, 5, Instant.now())
            workflowExecutionStorage.save(oldVersionOfWorkflowExecution)

            // when
            workflowExecutionStorage.save(newVersionOfWorkflowExecution)
            val workflowExecution = workflowExecutionStorage.get(oldVersionOfWorkflowExecution.id)

            // then
            workflowExecution.isDefined
            newVersionOfWorkflowExecution == workflowExecution.get
        }
    }

    trait Context extends Scope {
        val workflowExecutionStorage = new SimpleWorkflowExecutionStorage
    }
}
