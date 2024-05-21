# Weekly Report 8

## Week: 06/05/2024 - 12/05/2024

## Summary

- **Week´s Objectives:**
    1. Improve the methods to create custom exercises, sets and workouts.
    2. Start implementing the methods to manage sessions.
    3. Finish implementing a system to manage what methods the user can access in frontend.
    4. Finish the views to the methods 'assignTrainer', 'reassignTrainer', and 'updateTrainerCapacity' in the frontend.
- **Progress:**
    1. Improved the methods to create custom exercises, sets and workouts.
    2. Implemented the method to get the trainees from a company.
    3. Finished the system to manage what methods the user can access in frontend.
    4. Finished the views and conectivity with the backend to the methods 'assignTrainer', and 'updateTrainerCapacity'.

---

## Frontend changes

This week were implemented the views to the methods 'assignTrainer', and 'updateTrainerCapacity'. 

Also was finished the system to manage what methods the user can access in frontend, in this way, the user can only access the methods that he has permission to access.

To implement this system, each view inside the Vue router have an array of roles that can access it, and before each route is accessed the router checks if the user have permission to access the view otherwise, he is redirected to a page that says that he does not have permission to access the page.

To check the user Role a method that access the user information in the local storage returns the role of the user.
This method of getting and storing the userRole is not the safest way to do it, and we are aware of that, so it will be changed.

---

## Backend changes

The method to get the trainees from a company was implemented, with a filter, and the method to get the trainers from a company was improved introducing filters for the search.

The filters implemented for these methods are the following:

- **getCompanyTrainees:**
    - **gender:** Gender of the trainee to search.
    - **name:** Name of the trainee that is intended to search.

- **getCompanyTrainers:**
    - **gender:** Gender of the trainer to search.
    - **availability:** Availability, ascending or descending, of the trainer according to the capacity of trainee that supports (capacity - associated trainees).
    - **name:** Name of the trainer that is intended to search.

Besides that, a bug was detected in which if a *hired trainer* was removed from the system, the trainees associated with him, were no longer associated with the company to which they belonged, in this way the trainee could not be associated with another *hired trainer*. To solve this problem, a new association was created between the company and the trainee, so that the trainee continues to be associated with the company even if the *hired trainer* is removed.

---

## Database changes

Some changes were made in the database to solve the problem of the bug detected, for this a new table was created to associate the company with the trainee, in this way, even if the *hired trainer* is removed, the trainee will continue to be associated with the company.

In this way, the tables were changed to the following:

![Database Diagram](./images/dbDiagram7.png)

---

## Planning for Next Week

- **Objectives:**
    1. Improve the methods to create custom exercises, sets and workouts.
    2. Implement the methods to list all the exercises, sets and workouts.
    3. Start implementing the methods to manage sessions.
    4. Finish the views to the methods 'reassignTrainer' in the frontend.
    5. Start the implementation of the views to create workouts, sets and exercises.

    