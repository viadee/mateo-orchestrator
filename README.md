# Mateo Orchestrator

Mateo orchestrator repository

Mateo orchestrator is a standalone application that allows to orchestrate mateo instances.

## Functionality
The Mateo orchestrator has a list of Mateo instances (described in `src/main/resources/mateoInstances.txt`).
Test scripts (path) can be passed to the orchestrator for execution via REST api.
An id of the started job is returned (with which the status of the job can be queried).

### Job
A [job](./api-doc.md#jobentity) consists of:
- An uuid
- The test script name to execute (incl. path)
- A map of variables for the test script (input variables)
- A map of variables for the test script (output variables)
- The JobStatus (`QUEUED`, `RUNNING`, `FINISHED`, `FAILED`)
- A priority  (`URGENT`, `HIGH`, `MEDIUM`, `LOW`, `DEFAULT`)
- Create date
- Result of mateo


## Prerequisites and supported environments
- At least one running Mateo instance
- Each Mateo instance must contain the same test scripts (use the same testfile directory)

## Usage
First, a job is started via [POST](./api-doc.md#startjob) (and the corresponding [parameters](./api-doc.md#parameters-2)) with the endpoint `/api/job/start` gestartet.
The Uuid of the created job is returned as the [response](./api-doc.md#responses-4).
With this uuid the job can be returned by [GET](./api-doc.md#getjob) via the endpoint `/api/job{uuid}`.
For more information, see [Rest documentation](./api-doc.md).


### Example
POST: <br>
[http://localhost:8083/api/job/start?scriptFile=/opt/mateo/Scripts/Beispiel Chrome/Beispiel Chrome.xlsm]() 

Body:<br>
`{
  "inputVariable": "ValueWrittenToTheStorage"
}`


The returned Id can now be used to query the job:<br>
GET: <br> 
http://localhost:8083/api/job?uuid=4c74d8c4-1c0c-4e43-8725-32e867e76b23

## Swagger
If you started the application you can access the Swagger UI via `http://HOST:PORT/swagger-ui.html`.
