package com.example.domain.execution

import java.time.Instant

case class WorkflowExecution(id: String, workflowId: String, currentStep: Int, maxStep: Int, creationDate: Instant) {

    def incrementIfPossible(): Option[WorkflowExecution] = {
        if (currentStep < maxStep) {
            Some(createIncrementedWorkflowExecution())
        } else {
            None
        }
    }

    private def createIncrementedWorkflowExecution() = {
        WorkflowExecution(id, workflowId, currentStep + 1, maxStep, creationDate)
    }

    def isCompleted: Boolean = {
        currentStep >= maxStep
    }
}
