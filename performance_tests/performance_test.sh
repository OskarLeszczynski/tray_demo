#!/bin/sh

echo "**************** creating workflow\n"
workflowId=`curl -H "Content-Type: application/json" -X POST -d '{ "number_of_steps": 5000000 }' http://localhost:9000/workflows | jq -r '.workflow_id'`

echo "\n**************** creating execution for workflowId: $workflowId \n"
executionId=`curl -H "Content-Type: application/json" -X POST http://localhost:9000/workflows/$workflowId/executions | jq -r '.workflow_execution_id'`

echo "\n**************** Running wrk test on increment endpoint for workflowId=$workflowId and executionId=$executionId \n"
wrk -t12 -c400 -d10s -s put.lua http://localhost:9000/workflows/$workflowId/executions/$executionId

echo "\n**************** Running wrk test on execution status endpoint for workflowId=$workflowId and executionId=$executionId \n"
wrk -t12 -c400 -d10s http://localhost:9000/workflows/$workflowId/executions/$executionId

