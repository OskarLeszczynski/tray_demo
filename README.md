# Tray_demo

## **Repository contains:**
- tray-demo project build on Spray and Akka frameworks
- simple performance test script based on wrk and jq apps
- Docker file (not tested, defails below)

## **tray-demo**
Project was based on https://github.com/spray/spray-template and requires:
- scala-2.11
- sbt 0.13.8

####**Usage:**
Run sbt and then:
- "test" - run tests
- "run" - starts server (blocking)
- "re-start" - starts server (non-blocking)
- "re-stop" - stops server

Server is running on port 9000

####**Project description**
Project consists of 3 layers:
-http layer - it translate business events to proper http responses
-business layer - it contains all business logic (look at class WorkflowFacade)
-storage layer - it is 2 classes to store Workflow and WorkflowExecution (can be easily replaced to other implementation)

Clean up work is scheduled by akka actor system periodically every 1 minute (look at Boot class)

####**Dockerfile**
I could not install docker on my machine (I have some weird error), so I wrote this file without testing.

## **Real world decisions**
In real service of this kind I would split threads pools to connection pool and worker pool by creating another (worker) actor for execution computing.
I would also provide some case classes to communicate between actors (I haven't done this because in this case computation time is very short).
