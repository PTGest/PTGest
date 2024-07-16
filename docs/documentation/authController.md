# Auth Controller Documentation

## Signup Method

**URI:**  `{Base URI}` + `/api/auth/signup`

**Method:** POST

**Request Body:**

The request body should be a JSON object containing details for either a company or an independent trainer signup. Use the appropriate fields depending on the user type.

* **user_type (required):**  String specifying the type of user signing up. Can be either `"company"` or `"independent_trainer"`.

**Company Signup:**

* **name (required):** String - Name of the company.
* **email (required):** String - Email address of the company. Must be a valid email address.
* **password (required):** String - Password for the company account. Must be at least 8 characters long.

**Independent Trainer Signup:**

* **name (required):** String - Name of the trainer.
* **email (required):** String - Email address of the trainer. Must be a valid email address.
* **password (required):** String - Password for the trainer account. Must be at least 8 characters long.
* **gender (required):** String - Gender of the trainer.
* **phone_number (optional):** String - Phone number of the trainer. Must be a valid phone number.

**Response:**

On successful signup, the API will return a `201 Created` status code with a JSON response body containing a message indicating successful registration.

**Example Request (Company Signup):**

```json
{
  "user_type": "company",
  "name": "FitForLife",
  "email": "info@fitforlife.com",
  "password": "securepassword123"
}
```

**Example Response:**

```json
{
  "message": "Company registered successfully."
}
```

**Error Codes:**

* `400 Bad Request`:  
    * Invalid request body format.
    * Missing required fields.
    * Invalid email address.
    * Password does not meet minimum length requirement.
    * Phone number format is invalid (for independent trainers only).
* `409 Conflict`: User with the provided email already exists.
* `500 Internal Server Error`: Unexpected server error occurred during signup.

---

## Authenticated Signup Method

**URI:**  `{Base URI}` + `/api/auth/signup` 

**Method:** POST

**Authentication:** Required. User must be authenticated with a valid token and have either `COMPANY` or `INDEPENDENT_TRAINER` role.

**Request Body:**

The request body should be a JSON object containing details for either a hired trainer or a trainee signup, depending on the user role. Use the appropriate fields based on the type of user you are creating.

* **user_type (required):** String specifying the type of user being signed up. Can be either `"hired_trainer"` or `"trainee"`.

**Hired Trainer Signup (Company Role Only):**

* **name (required):** String - Name of the hired trainer.
* **email (required):** String - Email address of the hired trainer. Must be a valid email address.
* **gender (required):** String - Gender of the hired trainer.
* **capacity (required):** Integer - The number of trainees the hired trainer can take. Must be greater than 0.
* **phone_number (optional):** String - Phone number of the hired trainer. Must be a valid phone number.

**Trainee Signup (Any Authenticated Role):**

* **name (required):** String - Name of the trainee.
* **email (required):** String - Email address of the trainee. Must be a valid email address.
* **birthdate (required):** Date - Birthdate of the trainee. Must be before the current date.
* **gender (required):** String - Gender of the trainee.
* **phone_number (optional):** String - Phone number of the trainee. Must be a valid phone number.

**Response:**

On successful signup, the API will return a `201 Created` status code with a JSON response body containing a message indicating successful registration and details about the created user (trainee ID or hired trainer ID).

**Example Request (Trainee Signup):**

**Assuming the user is authenticated with a valid token:**

```json
{
  "user_type": "trainee",
  "name": "John Doe",
  "email": "johndoe@example.com",
  "birthdate": "1990-01-01",
  "gender": "MALE",
  "phone_number": "+1234567890"
}
```

**Example Response:**

```json
{
  "message": "Trainee registered successfully.",
  "details": {
    "userId": "e3b0c442-98fc-11e7-9a40-4bdf40e34bfa"
  }
}
```

**Error Codes:**

* `400 Bad Request`:
    * Invalid request body format.
    * Missing required fields.
    * Invalid email address.
    * Invalid birthdate (for trainee signup).
    * Invalid capacity (must be greater than 0 for hired trainer signup).
    * Invalid phone number format (optional fields).
* `401 Unauthorized`: User is not authenticated or does not have the required role.
* `409 Conflict`: User with the provided email already exists.
* `500 Internal Server Error`: Unexpected server error occurred during signup.

---

## Forgot Password Method

**URI:**  `{Base URI}` + `/api/forget-password`

**Method:** POST

**Request Body:**

The request body should be a JSON object containing the email address of the user who forgot their password.

* **email (required):** String - Email address of the user. Must be a valid email address.

**Response:**

On successful request, the API will return a `200 OK` status code with a JSON response body containing a message indicating that the password reset email has been sent.

**Example Request:**

```json
{
  "email": "johndoe@example.com"
}
```

**Example Response:**

```json
{
  "message": "Password reset email sent successfully."
}
```

**Error Codes:**

* `400 Bad Request`: Invalid request body format or missing required field (email).
* `404 Not Found`: User with the provided email address was not found.
* `500 Internal Server Error`: Unexpected server error occurred during processing the request.

---

## Validate Password Reset Request Method

**URI:**  `{Base URI}` + `/api/validate-password-reset-request/{requestToken}` 

**Method:** GET

**Path Variable:**

* **requestToken (required):** String - The password reset token received by the user via email.

**Response:**

On successful validation, the API will return a `200 OK` status code with a JSON response body containing a message indicating that the password reset token is valid.

**Example Request:**

```
GET {Base URI}/api/validate-password-reset-request/s0m3V4l1d-t0k3n
```

**Example Response:**

```json
{
  "message": "Password reset token validated successfully."
}
```

**Error Codes:**

* `400 Bad Request`: Invalid request path (missing or empty token).
* `404 Not Found`: The provided password reset token was not found or is invalid.
* `401 Unauthorized`: The password reset token has expired.

---

## Reset Password Method

**URI:**  `{Base URI}` + `/api/reset-password/{requestToken}` 

**Method:** PUT

**Path Variable:**

* **requestToken (required):** String - The password reset token received by the user via email.

**Request Body:**

The request body should be a JSON object containing the new password for the user.

* **password (required):** String - The new password for the user. Must meet the password complexity requirements defined by your system.

**Response:**

On successful password reset, the API will return a `200 OK` status code with a JSON response body containing a message indicating that the password has been reset successfully.

**Example Request:**

```
PUT {Base URI}/api/reset-password/s0m3V4l1d-t0k3n
```

```json
{
  "password": "newPassword123"
}
```

**Example Response:**

```json
{
  "message": "Password reset successfully."
}
```

**Error Codes:**

* `400 Bad Request`: Invalid request path (missing or empty token).
* `401 Unauthorized`: 
    * The provided password reset token was not found or is invalid.
    * The password reset token has expired.
* `500 Internal Server Error`: Unexpected server error occurred during password reset.

---

## Login Method

**URI:**  `{Base URI}` + `/api/login` 

**Method:** POST

**Request Body:**

The request body should be a JSON object containing the user's email and password.

* **email (required):** String - The email address of the user. Must be a valid email address.
* **password (required):** String - The user's password. Must meet the minimum password length requirement (at least 8 characters).

**Response:**

On successful login, the API will return a `200 OK` status code with a JSON response body containing a message indicating successful login details about the user (user ID, role, access token expiration date, and refresh token expiration date), and additional headers containing the access token and refresh token.

**Example Request:**

```json
{
  "email": "johndoe@example.com",
  "password": "securepassword123"
}
```

**Example Response:**

```json
{
  "message": "User logged in successfully.",
  "details": {
    "userId": "e3b0c442-98fc-11e7-9a40-4bdf40e34bfa",
    "role": "INDEPENDENT_TRAINER",
    "accessTokenExpirationDate": "2024-07-20T00:00:00.000+00:00",
    "refreshTokenExpirationDate": "2024-08-19T00:00:00.000+00:00"
  }
}
```

**Headers:**

The response will also include the following headers containing the access token and refresh token:

* `Authorization: Bearer <access_token>`
* `Refresh-Token: Bearer <refresh_token>`

**Cookies:**

The response will also include the following cookies containing the access token and refresh token:

* `access_token=<access_token>; HttpOnly; Secure; SameSite=None; Path=/`
* `refresh_token=<refresh_token>; HttpOnly; Secure; SameSite=None; Path=/`

**Error Codes:**

* `400 Bad Request`: Invalid request body format or missing required fields (email or password).
* `401 Unauthorized`: Invalid email or password combination.
* `500 Internal Server Error`: Unexpected server error occurred during login.

---

## Refresh Token Method

**URI:**  `{Base URI}` + `/api/auth/refresh` 

**Method:** POST

**Request:**

The request should contain a valid refresh token either in a cookie named "refresh_token" or in the Refresh-Token header with the format "Bearer <refresh_token>".

**Response:**

On successful refresh, the API will return a `200 OK` status code with a JSON response body containing a message indicating successful token refresh, details about the new tokens (access token expiration date and refresh token expiration date), and additional headers containing the new access token and refresh token.

**Example Request (Refresh Token in Cookie):**

A cookie named "refresh_token" exists in the request with a valid refresh token value.

**Example Request (Refresh Token in Authorization Header):**

```
Refresh-Token: Bearer <refresh_token>
```

**Example Response:**

```json
{
  "message": "Token refreshed successfully.",
  "details": {
    "accessTokenExpiration": "2024-07-20T00:00:00.000+00:00",
    "refreshTokenExpiration": "2024-08-19T00:00:00.000+00:00"
  }
}
```

**Headers:**

The response will also include the following headers containing the new access token and refresh token:

* `Authorization: Bearer <new_access_token>`
* `Refresh-Token: Bearer <new_refresh_token>`

**Error Codes:**

* `400 Bad Request`: Refresh token was not provided in either the request body or headers.
* `401 Unauthorized`: Invalid refresh token.
* `500 Internal Server Error`: Unexpected server error occurred during token refresh.

---

## Validate Authentication Method

**URI:**  `{Base URI}` + `/api/auth/validate-authentication` 

**Method:** GET

**Authentication:** Required. User must be authenticated with a valid token.

**Response:**

On successful validation, the API will return a `200 OK` status code with a JSON response body containing a message indicating that the user is authenticated.

**Example Request:**

```
GET {Base URI}/api/auth/validate-authentication
```

The request should contain a valid access token in the Authorization header with the format "Bearer <access_token>" or in a cookie named "access_token".

**Example Response:**

```json
{
  "message": "User authenticated successfully."
}
```

**Error Codes:**

* `401 Unauthorized`: User is not authenticated or the token is invalid.
* `500 Internal Server Error`: Unexpected server error occurred during authentication validation.

---

## Change Password Method

**URI:**  `{Base URI}` + `/api/change-password` 

**Method:** PUT

**Authentication:** Required. User must be authenticated with a valid token.

**Request Body:**

The request body should be a JSON object containing the user's current password and the new password.

* **currentPassword (required):** String - The user's current password. Must meet the minimum password length requirement (at least 8 characters).
* **newPassword (required):** String - The user's new password. Must meet the minimum password length requirement (at least 8 characters).

**Response:**

On successful password change, the API will return a `200 OK` status code with a JSON response body containing a message indicating that the password has been changed successfully.

**Example Request:**

```json
{
  "currentPassword": "currentpassword123",
  "newPassword": "newsecurepassword456"
}
```

**Example Response:**

```json
{
  "message": "Password changed successfully."
}
```

**Error Codes:**

* `400 Bad Request`: Invalid request body format or missing required fields (currentPassword or newPassword).
* `401 Unauthorized`: User is not authenticated or the current password is invalid.
* `500 Internal Server Error`: Unexpected server error occurred during password change.

---

## Logout Method

**URI:**  `{Base URI}` + `/api/auth/logout` 

**Method:** POST

**Request:**

The request should contain any existing authentication cookies set by the server.

**Response:**

On successful logout, the API will return a `200 OK` status code with a JSON response body containing a message indicating that the user has been logged out. The server will also invalidate any existing authentication cookies associated with the user's session.

**Example Request:**

The request is sent with any cookies set by the server during previous authentication.

**Example Response:**

```json
{
  "message": "User logged out successfully."
}
```
