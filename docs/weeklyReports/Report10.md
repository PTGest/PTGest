# Weekly Report 9

## Week: 13/05/2024 - 19/05/2024

## Summary

- **Week´s Objectives:**
    1. Implement the methods to list all sets and workouts.
    2. Start implementing the methods to manage sessions.
    3. Finish the view CreateExercise and his connection with the backend.
    4. Implement the Create workout view.
- **Progress:**
    1. Improved the methods to create custom exercises, sets and workouts.
    2. Implemented the method to list all the exercises with filters.
    3. Finish the views to the methods that list all exercises and sets in the frontend.
    4. Finish the views to the methods `create exercise` and `create set` in the frontend.
    5. Start the implementation of the view to list all workouts and to create a workout.

---

## Frontend changes

The views to list all exercises and sets were implemented, these views have a list of exercises that can be filtered by name, muscle group and modality, and a list of sets that can be filtered by name and type.

The views to create exercises and sets were implemented, these views have a form to create a new exercise or set, respectively.

The createExercise view form has the following fields:
- **Name:** Name of the exercise.
- **Notes:** Description of the exercise.
- **Modality:** Modality that the exercise belongs.
- **Muscle Group** that the exercise works.
- **Category:** Category of the exercise.

The createSet view form has the following fields:
- **Name:** Name of the set.
- **Notes:** Description of the set.
- **Type:** Type of the set.
- **Exercise:** Exercise that the set belongs.

The views to list all workouts and to create a workout were started to be implemented, these views will have a list of workouts and a form to create a new workout.

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
    3. Finish the connection between the ViewExercises, ViewSets, CreateExercise and CreateSet with the backend .
    4. Finish the views ViewWorkout and CreateWorkout and his connection with the backend.
