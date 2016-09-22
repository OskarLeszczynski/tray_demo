package com.example.domain.execution

import java.time.Instant

trait WorkflowExecutionStorage {
    def tryToSave(workflowExecution: WorkflowExecution): Boolean
    def save(workflowExecution: WorkflowExecution): Unit
    def get(id: String): Option[WorkflowExecution]
    def removeCompletedOrCreatedBefore(date: Instant): Unit
}
