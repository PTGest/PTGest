# Weekly Report 3

## Week: 01/04/2024 - 07/04/2024

## Summary

- **Week´s Objectives:**
    1. Finalize the authentication routes unit tests.
    2. Implement forget password and signup for trainee and hired trainer.
    3. Start implementing the user controller.
    4. Implement the password recovery page.
    5. Implement the user profile page.
- **Progress:**
    1. The authentication routes unit tests were finalized missing only the controller tests.
    2. Implemented refresh token to the authentication routes missing only the controller tests.
    3. The forget password and resetPassword were implemented missing unit tests.
    4. Forget password page and respective services were implemented.
    5. Created some error handling for the authentication routes.
    6. Created some error pages for the frontend.

## Frontend changes

1. **Password Recovery:**

    The view and the necessary services for the password recovery process were implemented.

2. **Login and Signup Improvements:**

    Improvements were made to the interface and services related to the login and signup process.

    Realizou-se melhorias na interface e nos serviços relacionados ao processo de login e cadastro.

3. **Error Handling:**

    Specific treatment was implemented for incorrect password and email errors in the view itself, based on the responses received from the fetchs.
    In case of a 200 OK response, the user is directed to the home page.
    In case of an incorrect password, an informative message is displayed based on the response received.

---

## Backend changes

The authentication approach was changed from using only a JWT authentication token to an approach that uses a JWT authentication token and a refresh token. The refresh token is used to obtain a new authentication token when the authentication token is about to expire, when requesting a new authentication token, it is checked if the userId present in the JWT token is valid and matches the userId of the refresh token, if so, a pair of authentication and refresh tokens is generated and sent to the client.

The forgetPassword and resetPassword methods were implemented, the first is used to send an email with a link, using the MailService implemented with JavaMailSender, to the resetPassword page, this link will contain a token that identifies the user and allows the password to be changed. This token is stored in the database with a duration of 30 minutes.

---

## Database changes

Three new tables were created in the database, one to store the token data and two to identify the type of token, one references the token that is used to update the authentication token and the other references the token that is used to recover the password.

## Planning for Next Week

- **Objectives:**
    1. Finalize the authentication routes unit tests.
    2. Make the necessary adjustments to the authentication routes.
    3. Implement signup for trainee and hired trainer.
    4. Improve backend error handling.
    5. Start implementing the user controller.
    6. Improve frontend error handling.
    7. Implement the reset password page.
    8. Implement the user profile page.
    9. Implement the trainer and hire trainer signup page.
