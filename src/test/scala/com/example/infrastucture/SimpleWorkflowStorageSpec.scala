package com.example.infrastucture

import com.example.domain.workflow.Workflow
import com.example.infrastructure.SimpleWorkflowStorage
import org.specs2.mutable.Specification
import org.specs2.specification.Scope

class SimpleWorkflowStorageSpec extends Specification {

    "SimpleWorkflowStorage" should {
        "get stored workflow" in new Context {
            // given
            val expectedWorkflow = Workflow("some_test_id", 5)
            workflowStorage.tryToSave(expectedWorkflow)

            // when
            val maybeWorkflow = workflowStorage.get(expectedWorkflow.id)

            // then
            maybeWorkflow.isDefined
            expectedWorkflow == maybeWorkflow.get
        }

        "save workflow" in new Context {
            // given
            val workflow = Workflow("some_test_id", 5)

            // when
            val saveResult = workflowStorage.tryToSave(workflow)

            // then
            saveResult mustEqual true
        }

        "not save workflow when workflow with equal id was already stored" in new Context {
            // given
            val oldWorkflow = Workflow("some_test_id", 5)
            val newWorkflow = Workflow("some_test_id", 10)
            workflowStorage.tryToSave(oldWorkflow)

            // when
            val saveResult = workflowStorage.tryToSave(newWorkflow)

            // then
            saveResult mustEqual false
        }
    }

    trait Context extends Scope {
        val workflowStorage = new SimpleWorkflowStorage
    }
}
