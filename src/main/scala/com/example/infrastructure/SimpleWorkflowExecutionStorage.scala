package com.example.infrastructure

import java.time.Instant

import com.example.domain.execution.{WorkflowExecution, WorkflowExecutionStorage}

import scala.collection.concurrent.TrieMap



class SimpleWorkflowExecutionStorage extends WorkflowExecutionStorage {
    private val executionMap: TrieMap[String, WorkflowExecution] = new TrieMap[String, WorkflowExecution]()

    def tryToSave(workflowExecution: WorkflowExecution): Boolean = {
        executionMap.putIfAbsent(workflowExecution.id, workflowExecution).map(oldExecution => false).getOrElse(true)
    }

    def save(workflowExecution: WorkflowExecution): Unit = {
        executionMap.put(workflowExecution.id, workflowExecution)
    }

    def get(id: String): Option[WorkflowExecution] = {
        executionMap.get(id)
    }

    def removeCompletedOrCreatedBefore(date: Instant): Unit = {
        executionMap.filter(entry => isCompletedOrCreatedBefore(entry._2, date))
            .foreach(entryToRemove => executionMap.remove(entryToRemove._1))
    }

    private def isCompletedOrCreatedBefore(execution: WorkflowExecution, date: Instant) = {
        execution.creationDate.isBefore(date) || execution.isCompleted
    }
}
