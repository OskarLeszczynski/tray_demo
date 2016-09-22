package com.example

import com.example.workflow.WorkflowFacade

class Cleaner(val workflowFacade: WorkflowFacade) extends Runnable {

    override def run(): Unit = {
        workflowFacade.cleanUp()
    }
}
