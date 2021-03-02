<h1 id="openapi-definition-mateo-api-controller">mateo-api-controller</h1>

## addMateoInstance

<a id="opIdaddMateoInstance"></a>

> Code samples

```java
URL obj = new URL("http://localhost:8083/api/mateo/add?mateoUrl=string");
HttpURLConnection con = (HttpURLConnection) obj.openConnection();
con.setRequestMethod("POST");
int responseCode = con.getResponseCode();
BufferedReader in = new BufferedReader(
    new InputStreamReader(con.getInputStream()));
String inputLine;
StringBuffer response = new StringBuffer();
while ((inputLine = in.readLine()) != null) {
    response.append(inputLine);
}
in.close();
System.out.println(response.toString());

```

`POST /api/mateo/add`

<h3 id="addmateoinstance-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|mateoUrl|query|string|true|none|

> Example responses

> 200 Response

<h3 id="addmateoinstance-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|string|

<aside class="success">
This operation does not require authentication
</aside>

## getMateos

<a id="opIdgetMateos"></a>

> Code samples

```java
URL obj = new URL("http://localhost:8083/api/mateo/all");
HttpURLConnection con = (HttpURLConnection) obj.openConnection();
con.setRequestMethod("GET");
int responseCode = con.getResponseCode();
BufferedReader in = new BufferedReader(
    new InputStreamReader(con.getInputStream()));
String inputLine;
StringBuffer response = new StringBuffer();
while ((inputLine = in.readLine()) != null) {
    response.append(inputLine);
}
in.close();
System.out.println(response.toString());

```

`GET /api/mateo/all`

> Example responses

> 200 Response

<h3 id="getmateos-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|Inline|

<h3 id="getmateos-responseschema">Response Schema</h3>

<aside class="success">
This operation does not require authentication
</aside>

## deleteMateoInstance

<a id="opIddeleteMateoInstance"></a>

> Code samples

```java
URL obj = new URL("http://localhost:8083/api/mateo/remove?mateoUrl=string");
HttpURLConnection con = (HttpURLConnection) obj.openConnection();
con.setRequestMethod("DELETE");
int responseCode = con.getResponseCode();
BufferedReader in = new BufferedReader(
    new InputStreamReader(con.getInputStream()));
String inputLine;
StringBuffer response = new StringBuffer();
while ((inputLine = in.readLine()) != null) {
    response.append(inputLine);
}
in.close();
System.out.println(response.toString());

```

`DELETE /api/mateo/remove`

<h3 id="deletemateoinstance-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|mateoUrl|query|string|true|none|

> Example responses

> 200 Response

<h3 id="deletemateoinstance-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|string|

<aside class="success">
This operation does not require authentication
</aside>

## deleteAllMateoInstance

<a id="opIddeleteAllMateoInstance"></a>

> Code samples

```java
URL obj = new URL("http://localhost:8083/api/mateo/remove-all");
HttpURLConnection con = (HttpURLConnection) obj.openConnection();
con.setRequestMethod("DELETE");
int responseCode = con.getResponseCode();
BufferedReader in = new BufferedReader(
    new InputStreamReader(con.getInputStream()));
String inputLine;
StringBuffer response = new StringBuffer();
while ((inputLine = in.readLine()) != null) {
    response.append(inputLine);
}
in.close();
System.out.println(response.toString());

```

`DELETE /api/mateo/remove-all`

> Example responses

> 200 Response

<h3 id="deleteallmateoinstance-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|string|

<aside class="success">
This operation does not require authentication
</aside>

<h1 id="openapi-definition-job-api-controller">job-api-controller</h1>

## startJob

<a id="opIdstartJob"></a>

> Code samples

```java
URL obj = new URL("http://localhost:8083/api/job/start?scriptFile=string");
HttpURLConnection con = (HttpURLConnection) obj.openConnection();
con.setRequestMethod("POST");
int responseCode = con.getResponseCode();
BufferedReader in = new BufferedReader(
    new InputStreamReader(con.getInputStream()));
String inputLine;
StringBuffer response = new StringBuffer();
while ((inputLine = in.readLine()) != null) {
    response.append(inputLine);
}
in.close();
System.out.println(response.toString());

```

`POST /api/job/start`

> Body parameter

```json
{
  "property1": "string",
  "property2": "string"
}
```

<h3 id="startjob-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|scriptFile|query|string|true|none|
|priority|query|string|false|none|
|outputVariables|query|array[string]|false|none|
|body|body|object|false|none|
|» **additionalProperties**|body|string|false|none|

#### Enumerated Values

|Parameter|Value|
|---|---|
|priority|HIGH|
|priority|MEDIUM|
|priority|URGENT|
|priority|LOW|
|priority|DEFAULT|

> Example responses

> 200 Response

<h3 id="startjob-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|string|

<aside class="success">
This operation does not require authentication
</aside>

## downloadReportAsZip

<a id="opIddownloadReportAsZip"></a>

> Code samples

```java
URL obj = new URL("http://localhost:8083/api/job/result/download/zip?uuid=497f6eca-6276-4993-bfeb-53cbbbba6f08");
HttpURLConnection con = (HttpURLConnection) obj.openConnection();
con.setRequestMethod("POST");
int responseCode = con.getResponseCode();
BufferedReader in = new BufferedReader(
    new InputStreamReader(con.getInputStream()));
String inputLine;
StringBuffer response = new StringBuffer();
while ((inputLine = in.readLine()) != null) {
    response.append(inputLine);
}
in.close();
System.out.println(response.toString());

```

`POST /api/job/result/download/zip`

<h3 id="downloadreportaszip-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|uuid|query|string(uuid)|true|none|

> Example responses

> 200 Response

<h3 id="downloadreportaszip-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|Inline|

<h3 id="downloadreportaszip-responseschema">Response Schema</h3>

<aside class="success">
This operation does not require authentication
</aside>

## getJob

<a id="opIdgetJob"></a>

> Code samples

```java
URL obj = new URL("http://localhost:8083/api/job?uuid=497f6eca-6276-4993-bfeb-53cbbbba6f08");
HttpURLConnection con = (HttpURLConnection) obj.openConnection();
con.setRequestMethod("GET");
int responseCode = con.getResponseCode();
BufferedReader in = new BufferedReader(
    new InputStreamReader(con.getInputStream()));
String inputLine;
StringBuffer response = new StringBuffer();
while ((inputLine = in.readLine()) != null) {
    response.append(inputLine);
}
in.close();
System.out.println(response.toString());

```

`GET /api/job`

<h3 id="getjob-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|uuid|query|string(uuid)|true|none|

> Example responses

> 200 Response

<h3 id="getjob-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|[JobEntity](#schemajobentity)|

<aside class="success">
This operation does not require authentication
</aside>

## getJobsWithState

<a id="opIdgetJobsWithState"></a>

> Code samples

```java
URL obj = new URL("http://localhost:8083/api/job/state?jobStatus=QUEUED");
HttpURLConnection con = (HttpURLConnection) obj.openConnection();
con.setRequestMethod("GET");
int responseCode = con.getResponseCode();
BufferedReader in = new BufferedReader(
    new InputStreamReader(con.getInputStream()));
String inputLine;
StringBuffer response = new StringBuffer();
while ((inputLine = in.readLine()) != null) {
    response.append(inputLine);
}
in.close();
System.out.println(response.toString());

```

`GET /api/job/state`

<h3 id="getjobswithstate-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|jobStatus|query|string|true|none|

#### Enumerated Values

|Parameter|Value|
|---|---|
|jobStatus|QUEUED|
|jobStatus|RUNNING|
|jobStatus|FINISHED|
|jobStatus|FAILED|

> Example responses

> 200 Response

<h3 id="getjobswithstate-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|Inline|

<h3 id="getjobswithstate-responseschema">Response Schema</h3>

Status Code **200**

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|*anonymous*|[[JobEntity](#schemajobentity)]|false|none|none|
|» id|string(uuid)|false|none|none|
|» scriptName|string|false|none|none|
|» inputVariables|object|false|none|none|
|»» **additionalProperties**|string|false|none|none|
|» outputVariables|object|false|none|none|
|»» **additionalProperties**|string|false|none|none|
|» status|string|false|none|none|
|» priority|string|false|none|none|
|» priorityValue|integer(int32)|false|none|none|
|» askedTimes|integer(int32)|false|none|none|
|» createDate|string(date-time)|false|none|none|
|» modifyDate|string(date-time)|false|none|none|
|» filePath|string|false|none|none|
|» filename|string|false|none|none|
|» testSetName|string|false|none|none|
|» resultLevel|string|false|none|none|
|» resultString|string|false|none|none|
|» startTime|string|false|none|none|
|» originFileName|string|false|none|none|
|» runIndex|string|false|none|none|
|» vtfVersion|string|false|none|none|
|» mateoInstanz|string|false|none|none|

#### Enumerated Values

|Property|Value|
|---|---|
|status|QUEUED|
|status|RUNNING|
|status|FINISHED|
|status|FAILED|
|priority|HIGH|
|priority|MEDIUM|
|priority|URGENT|
|priority|LOW|
|priority|DEFAULT|

<aside class="success">
This operation does not require authentication
</aside>

## getFinishedJobs

<a id="opIdgetFinishedJobs"></a>

> Code samples

```java
URL obj = new URL("http://localhost:8083/api/job/state/finished");
HttpURLConnection con = (HttpURLConnection) obj.openConnection();
con.setRequestMethod("GET");
int responseCode = con.getResponseCode();
BufferedReader in = new BufferedReader(
    new InputStreamReader(con.getInputStream()));
String inputLine;
StringBuffer response = new StringBuffer();
while ((inputLine = in.readLine()) != null) {
    response.append(inputLine);
}
in.close();
System.out.println(response.toString());

```

`GET /api/job/state/finished`

> Example responses

> 200 Response

<h3 id="getfinishedjobs-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|Inline|

<h3 id="getfinishedjobs-responseschema">Response Schema</h3>

Status Code **200**

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|*anonymous*|[[JobEntity](#schemajobentity)]|false|none|none|
|» id|string(uuid)|false|none|none|
|» scriptName|string|false|none|none|
|» inputVariables|object|false|none|none|
|»» **additionalProperties**|string|false|none|none|
|» outputVariables|object|false|none|none|
|»» **additionalProperties**|string|false|none|none|
|» status|string|false|none|none|
|» priority|string|false|none|none|
|» priorityValue|integer(int32)|false|none|none|
|» askedTimes|integer(int32)|false|none|none|
|» createDate|string(date-time)|false|none|none|
|» modifyDate|string(date-time)|false|none|none|
|» filePath|string|false|none|none|
|» filename|string|false|none|none|
|» testSetName|string|false|none|none|
|» resultLevel|string|false|none|none|
|» resultString|string|false|none|none|
|» startTime|string|false|none|none|
|» originFileName|string|false|none|none|
|» runIndex|string|false|none|none|
|» vtfVersion|string|false|none|none|
|» mateoInstanz|string|false|none|none|

#### Enumerated Values

|Property|Value|
|---|---|
|status|QUEUED|
|status|RUNNING|
|status|FINISHED|
|status|FAILED|
|priority|HIGH|
|priority|MEDIUM|
|priority|URGENT|
|priority|LOW|
|priority|DEFAULT|

<aside class="success">
This operation does not require authentication
</aside>

## isOnline

<a id="opIdisOnline"></a>

> Code samples

```java
URL obj = new URL("http://localhost:8083/api/job/online");
HttpURLConnection con = (HttpURLConnection) obj.openConnection();
con.setRequestMethod("GET");
int responseCode = con.getResponseCode();
BufferedReader in = new BufferedReader(
    new InputStreamReader(con.getInputStream()));
String inputLine;
StringBuffer response = new StringBuffer();
while ((inputLine = in.readLine()) != null) {
    response.append(inputLine);
}
in.close();
System.out.println(response.toString());

```

`GET /api/job/online`

> Example responses

> 200 Response

<h3 id="isonline-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|string|

<aside class="success">
This operation does not require authentication
</aside>

## getJobs

<a id="opIdgetJobs"></a>

> Code samples

```java
URL obj = new URL("http://localhost:8083/api/job/all");
HttpURLConnection con = (HttpURLConnection) obj.openConnection();
con.setRequestMethod("GET");
int responseCode = con.getResponseCode();
BufferedReader in = new BufferedReader(
    new InputStreamReader(con.getInputStream()));
String inputLine;
StringBuffer response = new StringBuffer();
while ((inputLine = in.readLine()) != null) {
    response.append(inputLine);
}
in.close();
System.out.println(response.toString());

```

`GET /api/job/all`

> Example responses

> 200 Response

<h3 id="getjobs-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|Inline|

<h3 id="getjobs-responseschema">Response Schema</h3>

Status Code **200**

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|*anonymous*|[[JobEntity](#schemajobentity)]|false|none|none|
|» id|string(uuid)|false|none|none|
|» scriptName|string|false|none|none|
|» inputVariables|object|false|none|none|
|»» **additionalProperties**|string|false|none|none|
|» outputVariables|object|false|none|none|
|»» **additionalProperties**|string|false|none|none|
|» status|string|false|none|none|
|» priority|string|false|none|none|
|» priorityValue|integer(int32)|false|none|none|
|» askedTimes|integer(int32)|false|none|none|
|» createDate|string(date-time)|false|none|none|
|» modifyDate|string(date-time)|false|none|none|
|» filePath|string|false|none|none|
|» filename|string|false|none|none|
|» testSetName|string|false|none|none|
|» resultLevel|string|false|none|none|
|» resultString|string|false|none|none|
|» startTime|string|false|none|none|
|» originFileName|string|false|none|none|
|» runIndex|string|false|none|none|
|» vtfVersion|string|false|none|none|
|» mateoInstanz|string|false|none|none|

#### Enumerated Values

|Property|Value|
|---|---|
|status|QUEUED|
|status|RUNNING|
|status|FINISHED|
|status|FAILED|
|priority|HIGH|
|priority|MEDIUM|
|priority|URGENT|
|priority|LOW|
|priority|DEFAULT|

<aside class="success">
This operation does not require authentication
</aside>

## removeJob

<a id="opIdremoveJob"></a>

> Code samples

```java
URL obj = new URL("http://localhost:8083/api/job/remove?uuid=497f6eca-6276-4993-bfeb-53cbbbba6f08");
HttpURLConnection con = (HttpURLConnection) obj.openConnection();
con.setRequestMethod("DELETE");
int responseCode = con.getResponseCode();
BufferedReader in = new BufferedReader(
    new InputStreamReader(con.getInputStream()));
String inputLine;
StringBuffer response = new StringBuffer();
while ((inputLine = in.readLine()) != null) {
    response.append(inputLine);
}
in.close();
System.out.println(response.toString());

```

`DELETE /api/job/remove`

<h3 id="removejob-parameters">Parameters</h3>

|Name|In|Type|Required|Description|
|---|---|---|---|---|
|uuid|query|string(uuid)|true|none|

> Example responses

> 200 Response

<h3 id="removejob-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|string|

<aside class="success">
This operation does not require authentication
</aside>

## removeAllJobs

<a id="opIdremoveAllJobs"></a>

> Code samples

```java
URL obj = new URL("http://localhost:8083/api/job/remove-all");
HttpURLConnection con = (HttpURLConnection) obj.openConnection();
con.setRequestMethod("DELETE");
int responseCode = con.getResponseCode();
BufferedReader in = new BufferedReader(
    new InputStreamReader(con.getInputStream()));
String inputLine;
StringBuffer response = new StringBuffer();
while ((inputLine = in.readLine()) != null) {
    response.append(inputLine);
}
in.close();
System.out.println(response.toString());

```

`DELETE /api/job/remove-all`

> Example responses

> 200 Response

<h3 id="removealljobs-responses">Responses</h3>

|Status|Meaning|Description|Schema|
|---|---|---|---|
|200|[OK](https://tools.ietf.org/html/rfc7231#section-6.3.1)|OK|string|

<aside class="success">
This operation does not require authentication
</aside>

# Schemas

<h2 id="tocS_JobEntity">JobEntity</h2>
<!-- backwards compatibility -->
<a id="schemajobentity"></a>
<a id="schema_JobEntity"></a>
<a id="tocSjobentity"></a>
<a id="tocsjobentity"></a>

```json
{
  "id": "497f6eca-6276-4993-bfeb-53cbbbba6f08",
  "scriptName": "string",
  "inputVariables": {
    "property1": "string",
    "property2": "string"
  },
  "outputVariables": {
    "property1": "string",
    "property2": "string"
  },
  "status": "QUEUED",
  "priority": "HIGH",
  "priorityValue": 0,
  "askedTimes": 0,
  "createDate": "2019-08-24T14:15:22Z",
  "modifyDate": "2019-08-24T14:15:22Z",
  "filePath": "string",
  "filename": "string",
  "testSetName": "string",
  "resultLevel": "string",
  "resultString": "string",
  "startTime": "string",
  "originFileName": "string",
  "runIndex": "string",
  "vtfVersion": "string",
  "mateoInstanz": "string"
}

```

### Properties

|Name|Type|Required|Restrictions|Description|
|---|---|---|---|---|
|id|string(uuid)|false|none|none|
|scriptName|string|false|none|none|
|inputVariables|object|false|none|none|
|» **additionalProperties**|string|false|none|none|
|outputVariables|object|false|none|none|
|» **additionalProperties**|string|false|none|none|
|status|string|false|none|none|
|priority|string|false|none|none|
|priorityValue|integer(int32)|false|none|none|
|askedTimes|integer(int32)|false|none|none|
|createDate|string(date-time)|false|none|none|
|modifyDate|string(date-time)|false|none|none|
|filePath|string|false|none|none|
|filename|string|false|none|none|
|testSetName|string|false|none|none|
|resultLevel|string|false|none|none|
|resultString|string|false|none|none|
|startTime|string|false|none|none|
|originFileName|string|false|none|none|
|runIndex|string|false|none|none|
|vtfVersion|string|false|none|none|
|mateoInstanz|string|false|none|none|

#### Enumerated Values

|Property|Value|
|---|---|
|status|QUEUED|
|status|RUNNING|
|status|FINISHED|
|status|FAILED|
|priority|HIGH|
|priority|MEDIUM|
|priority|URGENT|
|priority|LOW|
|priority|DEFAULT|

