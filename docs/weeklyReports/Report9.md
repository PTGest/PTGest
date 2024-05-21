# Weekly Report 9

## Week: 13/05/2024 - 19/05/2024

## Summary

- **Week´s Objectives:**
    1. Improve the methods to create custom exercises, sets and workouts.
    2. Implement the methods to list all the exercises, sets and workouts.
    3. Start implementing the methods to manage sessions.
    4. Finish the views to the methods 'reassignTrainer' in the frontend.
    5. Start the implementation of the views to create workouts, sets and exercises.
- **Progress:**
    1. Improved the methods to create custom exercises, sets and workouts.
    2. Implemented the method to list all the exercises with filters.
    3. Finish the views to the methods 'reassignTrainer' in the frontend.
    4. Start the implementation of the views to create workouts, sets and exercises.

---

## Frontend changes

The reassignTrainer view was implemented with a form to reassign a trainer to a trainee.

This week were implemented the views to the methods 'assignTrainer' 'reassignTrainer', also was started the implementation of the Exercises and Create exercises views.

The Exercises view was implemented with a list of exercises that can be filtered by name, tags, etc. and the CreateExercise view was implemented with a form to create a new exercise.

---

## Backend changes

The method to create custom exercises was improved to receive the tags of the exercise a list of 3 tags that can identify the the muscle group that the exercise works, this tags can be used to filter the exercises when listing them.

In the method to list all the exercises was implemented a filter to search the exercises by name for search the exercises by name, muscle group this filter can be used to search an exercise by the tags that were added when creating the exercise and modalities that can be used to filter the exercises by the modality that the exercise belongs like *BODYWEIGHT*, *WEIGHTLIFT*, etc.

Were added an anotation to identify the method or class and respective method that needs a especific role to be accessed, this anotation is used by the Authorization Interceptor to check if the user has the role to access the method.

Some bugs in RequestBody elements validation were fixed, now this validation is working correctly.

---

## Planning for Next Week

- **Objectives:**
    1. Implement the methods to list all sets and workouts.
    2. Start implementing the methods to manage sessions.
    3. Finish the view CreateExercise and his connection with the backend.
    4. Implement the Create workout view.
