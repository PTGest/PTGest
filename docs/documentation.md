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
