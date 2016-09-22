package com.example

import com.example.domain.WorkflowFacade

class Cleaner(val workflowFacade: WorkflowFacade) extends Runnable {

    override def run(): Unit = {
        workflowFacade.cleanUp()
    }
}
