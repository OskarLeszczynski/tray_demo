package com.example.infrastructure

import com.example.domain.execution.WorkflowExecutionStorage
import com.example.domain.workflow.WorkflowStorage

object Storage {
  val workflows: WorkflowStorage = new SimpleWorkflowStorage
  val workflowExecutions: WorkflowExecutionStorage = new SimpleWorkflowExecutionStorage
}
