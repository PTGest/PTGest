# User Controller Documentation

## User Details Method

**URI:** `{Base URI}` + `/api/user/{id}` 

**Method:** GET

**Path Variable:**

* **id (required):** UUID - The unique identifier of the user.

**Authentication:** Required. User must be authenticated with a valid token.

**Response:**

On successful retrieval of user details, the API will return a `200 OK` status code with a JSON response body containing a message indicating successful retrieval and details about the user. The specific details returned will depend on the user's role:

* **Trainee:** Name, email, gender, birthdate, and phone number (optional).
* **Trainer (Hired or Independent):** Name, email, gender, and phone number (optional).
* **Company:** Name and email.

**Example Request:**

```
GET {Base URI}/api/users/e3b0c442-98fc-11e7-9a40-4bdf40e34bfa
```

**Example Response (Trainee):**

```json
{
  "message": "User details retrieved successfully",
  "details": {
    "name": "John Doe",
    "email": "johndoe@example.com",
    "gender": "MALE",
    "birthdate": "1990-01-01",
    "phoneNumber": "+1234567890"
  }
}
```

**Example Response (Trainer):**

```json
{
  "message": "User details retrieved successfully",
  "details": {
    "name": "Jane Smith",
    "email": "janesmith@example.com",
    "gender": "FEMALE",
    "phoneNumber": "+9876543210"
  }
}
```

**Example Response (Company):**

```json
{
  "message": "User details retrieved successfully",
  "details": {
    "name": "ACME Corporation",
    "email": "acmecorp@example.com"
  }
}
```

**Error Codes:**

* `401 Unauthorized`: User is not authenticated or the token is invalid.
* `404 Not Found`: User with the provided ID was not found.
* `500 Internal Server Error`: Unexpected server error occurred during retrieval of user details.
