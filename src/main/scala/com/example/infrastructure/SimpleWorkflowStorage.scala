package com.example.infrastructure

import com.example.domain.workflow.{Workflow, WorkflowStorage}
import scala.collection.concurrent.TrieMap



class SimpleWorkflowStorage extends WorkflowStorage{
    private val workflowMap: TrieMap[String, Workflow] = new TrieMap[String, Workflow]()

    def tryToSave(workflow: Workflow): Boolean = {
        workflowMap.putIfAbsent(workflow.id, workflow).map(oldWorkflow => false).getOrElse(true)
    }

    def get(id: String): Option[Workflow] = {
        workflowMap.get(id)
    }
}
