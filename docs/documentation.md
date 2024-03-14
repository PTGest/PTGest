# API Documentation

## Auth Controller

### Sign Up

URI: /api/auth/signup

Method: POST

Request Body:

- Company;

    ```json
    {
        "name": "string",
        "email": "string",
        "password": "string",
        "user_type": "company"
    }
    ```

- Independent Trainer;

    ```json
    {
        "name": "string",
        "email": "string",
        "password": "string",
        "gender": "string",
        "phoneNumber": "string",
        "user_type": "independent_trainer"
    }
    ```

- Hired Trainer;

    ```json
    {
        "name": "string",
        "email": "string",
        "gender": "string",
        "phoneNumber": "string",
        "user_type": "hired_trainer"
    }
    ```

- Trainee;

    ```json
    {
        "name": "string",
        "email": "string",
        "birthdate": "string",
        "gender": "string",
        "phoneNumber": "string",
        "user_type": "trainee"
    }
    ```

### Log In

URI: /api/auth/login

Method: POST

Request Body:

```json
{
    "email": "string",
    "password": "string",
    "user_type": "string"
}
```

### Log Out

URI: /api/auth/logout

Method: DELETE

Authorization: Bearer Token

---

## User Controller

### Get Company Information

URI: /api/user/company/:id

Method: GET

### Update Company Information

**Notes**: check how or if we can update the company email and this information can only be updated by the person who created the company

URI: /api/user/company

Method: PUT

Authorization: Bearer Token

Request Body:

```json
{
    "name": "string",
    "email": "string",
    "current_password": "string",
    "password": "string"
}
```

### Remove Company

**Notes**:

- Only the person who created the company can remove it and all its related data including hired trainers and trainees

- The company can only be removed by the person who created it

URI: /api/user/company

Method: DELETE

Authorization: Bearer Token

### Get Trainer Information

URI: /api/user/independent-trainer/:id

Method: GET

### Update Trainer Information

**Notes**: only the person who created the independent trainer can update it

URI: /api/user/independent-trainer

Method: PUT

Authorization: Bearer Token

Request Body:

```json
{
    "name": "string",
    "email": "string",
    "current_password": "string",
    "password": "string", 
    "gender": "string",
    "contact": "string"
}
```

### Remove Independent Trainer

**Notes**: only the person who created the independent trainer can remove it

URI: /api/user/independent-trainer

Method: DELETE

Authorization: Bearer Token

### Remove Hired Trainer

**Notes**: only the company that hired the trainer can remove it

URI: /api/user/hired-trainer/:id

Method: DELETE

Authorization: Bearer Token

### Get Trainee Information

URI: /api/user/trainee/:id

Method: GET

### Update Trainee Information

URI: /api/user/trainee

Method: PUT

Authorization: Bearer Token

Request Body:

```json
{
    "name": "string",
    "email": "string",
    "current_password": "string",
    "password": "string",
    "birthdate": "string",
    "gender": "string",
    "phoneNumber": "string"
}
```

### Remove Trainee

**Notes**: only the person who created the company or tainer can remove it

URI: /api/user/trainee

Method: DELETE

Authorization: Bearer Token