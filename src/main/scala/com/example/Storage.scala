package com.example

import com.example.workflow.WorkflowStorage
import com.example.workflow.execution.WorkflowExecutionStorage

object Storage {
  val workflows: WorkflowStorage = new WorkflowStorage
  val workflowExecutions: WorkflowExecutionStorage = new WorkflowExecutionStorage
}
