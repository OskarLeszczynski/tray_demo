package com.example.domain.workflow

trait WorkflowStorage {
    def tryToSave(workflow: Workflow): Boolean
    def get(id: String): Option[Workflow]
}
