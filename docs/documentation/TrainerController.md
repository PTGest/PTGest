# Trainer Controller Documentation

## Get Trainer Trainees Method

**URI**: `{Base URI}` + `/api/trainer/trainees`

**Path Variable:**

* **name** (optional): String - Filter by trainee name.
* **gender** (optional): Gender - Filter by trainee gender.
* **skip** (optional): Integer - Number of records to skip.
* **limit** (optional): Integer - Number of records to retrieve.

**Authentication:** Required. User must be authenticated with a valid token.

**Response:**
On successful retrieval of trainer trainees, the API will return a 200 OK status code with a JSON response body containing a message indicating successful retrieval and details about the trainees.

**Example Request:**
```bash
GET {Base URI}/api/trainer/trainees?name=John&gender=MALE&skip=0&limit=10
```

**Example Response:**

```json
{
  "message": "Company trainees retrieved successfully.",
  "details": {
    "items": [
      {
        "id": "e3b0c442-98fc-11e7-9a40-4bdf40e34bfa",
        "name": "John Doe",
        "email": "johndoe@example.com",
        "gender": "MALE"
      }
    ],
    "total": 1
  }
}
```
**Error Codes:**

* `401 Unauthorized:` User is not authenticated or the token is invalid.
* `500 Internal Server Error:` Unexpected server error occurred during retrieval of trainees.

## Add Trainee Data Method
**URI:** `{Base URI}` + `/api/trainer/{traineeId}/data`

**Method: POST**

**Path Variable:**
* **traineeId (required):** UUID - The unique identifier of the trainee.
**Request Body:**
```json
{
  "gender": "MALE",
  "weight": 70,
  "height": 175,
  "bodyCircumferences": MUDAR,
  "bodyFatPercentage": 15.0,
  "skinFold": MUDAR
}
```

**Authentication:** Required. User must be authenticated with a valid token.

**Response:**

On successful addition of trainee data, the API will return a 201 Created status code with a JSON response body containing a message indicating successful addition and the ID of the newly created data.

**Example Request:**
```bash
POST {Base URI}/api/trainee/e3b0c442-98fc-11e7-9a40-4bdf40e34bfa/data
```

**Example Response:**
```json
{
"message": "Trainee data added successfully.",
"details": {
    "id": 12345
    }
}
```

**Error Codes:**

* `400 Bad Request:` The request body is invalid or missing required fields.
* `401 Unauthorized:` User is not authenticated or the token is invalid.
* `404 Not Found:` Trainee with the provided ID was not found.
* `500 Internal Server Error:` Unexpected server error occurred during the addition of trainee data.


# Get Trainee Data History Method
**URI:** `{Base URI}` + `/api/trainer/{traineeId}/data/history`

*Method:* GET

*Path Variable:*
* **traineeId (required):** UUID - The unique identifier of the trainee.

**Query Parameters:**
* **order (optional):** Order - The order in which to retrieve data. 
* **skip (optional): Integer** - Number of records to skip.
* **limit (optional):** Integer - Number of records to retrieve.
* **Authentication:** Required. User must be authenticated with a valid token.

**Response:**
On successful retrieval of trainee data history, the API will return a 200 OK status code with a JSON response body containing a message indicating successful retrieval and details about the trainee data history.

**Example Request:**
```sql
GET {Base URI}/api/trainee/e3b0c442-98fc-11e7-9a40-4bdf40e34bfa/data/history?order=ASC&skip=0&limit=10
```
**Example Response:**
```json
{
  "message": "Trainee data retrieved successfully.",
  "details": {
      "items": [
        {
        "id": 12345,
        "gender": "MALE",
        "weight": 70,
        "height": 175,
        "bodyCircumferences": MUDAR,
        "bodyFatPercentage": 15.0,
        "skinFold": MUDAR
        }
      ],
      "total": 1
  }
}
```
**Error Codes:**
* `401 Unauthorized:` User is not authenticated or the token is invalid.
* `404 Not Found:` Trainee with the provided ID was not found.
* `500 Internal Server Error:` Unexpected server error occurred during retrieval of trainee data history.



## Get Trainee Data Details Method
**URI:** `{Base URI}` + `/api/trainer/{traineeId}/data/{dataId}`

**Method:** GET

**Path Variables:**
* traineeId (required): UUID - The unique identifier of the trainee.
* dataId (required): Integer - The unique identifier of the trainee data. 
* Authentication: Required. User must be authenticated with a valid token.

**Response:**
On successful retrieval of trainee data details, the API will return a 200 OK status code with a JSON response body containing a message indicating successful retrieval and details about the trainee data.

**Example Request:**
```bash
GET {Base URI}/api/trainee/e3b0c442-98fc-11e7-9a40-4bdf40e34bfa/data/12345
```

**Example Response:**
```json
{
  "message": "Trainee data details retrieved successfully.",
  "details": {
    "id": 12345,
    "gender": "MALE",
    "weight": 70,
    "height": 175,
    "bodyCircumferences": MUDAR,
    "bodyFatPercentage": 15.0,
    "skinFold": MUDAR
  }
}
```

**Error Codes:**

* **401 Unauthorized:** User is not authenticated or the token is invalid.
* **404 Not Found:** Trainee with the provided ID or data with the provided ID was not found. 
* **500 Internal Server Error:** Unexpected server error occurred during retrieval of trainee data details.


## Create Report Method
**URI:** `{Base URI}` + `/api/trainer/{traineeId}/report`

**Method: POST**

**Path Variable:**
* traineeId (required): UUID - The unique identifier of the trainee.
**Request Body:**
```json
{
"report": "Report content...",
"visibility": "PRIVATE"
}
```

**Authentication:** Required. User must be authenticated with a valid token.

**Response:**
On successful creation of a report, the API will return a 201 Created status code with a JSON response body containing a message indicating successful creation and the ID of the newly created report.

**Example Request:**
```bash
POST {Base URI}/api/trainee/e3b0c442-98fc-11e7-9a40-4bdf40e34bfa/report
```
**Example Response:**
```json
{
    "message": "Report created successfully.",
    "details": {
      "id": 67890
    }
}
```

**Error Codes:**

* 400 Bad Request: The request body is invalid or missing required fields.
* 401 Unauthorized: User is not authenticated or the token is invalid. 
* 404 Not Found: Trainee with the provided ID was not found. 
* 500 Internal Server Error: Unexpected server error occurred during the creation of the report.

## Get Reports Method
**URI:** `{Base URI}` + `/api/trainer/{traineeId}/reports`

**Method: GET**

**Path Variable:**

**traineeId (required):** UUID - The unique identifier of the trainee.
**Query Parameters:**
**skip (optional):** Integer - Number of records to skip.
**limit (optional):** Integer - Number of records to retrieve.
**Authentication:** Required. User must be authenticated with a valid token.

**Response:**
On successful retrieval of reports, the API will return a 200 OK status code with a JSON response body containing a message indicating successful retrieval and details about the reports.

**Example Request:**
```bash
GET {Base URI}/api/trainee/e3b0c442-98fc-11e7-9a40-4bdf40e34bfa/reports?skip=0&limit=10
```
**Example Response:**

```json
{
    "message": "Reports retrieved successfully.",
    "details": {
      "items": [
        {
        "id": 67890,
        "report": "Report content...",
        "visibility": "PRIVATE"
        }
        ],
      "total": 1
    }
}
```
**Error Codes:**

* **401 Unauthorized:** User is not authenticated or the token is invalid.
* **404 Not Found:** Trainee with the provided ID was not found.
* **500 Internal Server Error:** Unexpected server error occurred during retrieval of reports.

## Get Report Details Method

**URI:** `{Base URI}` + `/api/trainer/{traineeId}/report/{reportId}`

**Method: GET**

**Path Variables:**
* **traineeId (required):** UUID - The unique identifier of the trainee.
* **reportId (required):** Integer - The unique identifier of the report.
* **Authentication:** Required. User must be authenticated with a valid token.

**Response:**
On successful retrieval of report details, the API will return a 200 OK status code with a JSON response body containing a message indicating successful retrieval and details about the report.

**Example Request:**

```bash
GET {Base URI}/api/trainee/e3b0c442-98fc-11e7-9a40-4bdf40e34bfa/report/67890
```
**Example Response:**
```json
{
    "message": "Report details retrieved successfully.",
    "details": {
        "id": 67890,
        "report": "Report content...",
        "visibility": "PRIVATE"
    }
}
```
Error Codes:

401 Unauthorized: User is not authenticated or the token is invalid.
404 Not Found: Trainee with the provided ID or report with the provided ID was not found.
500 Internal Server Error: Unexpected server error occurred during retrieval of report details.
