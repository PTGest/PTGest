# Weekly Report 10

## Week: 20/05/2024 - 26/05/2024

## Summary

- **Week´s Objectives:**
    1. Implement the methods to list all sets and workouts.
    2. Start implementing the methods to manage sessions.
    3. Finish the view CreateExercise and his connection with the backend.
    4. Implement the Create workout view.
- **Progress:**
    1. The methods to list all sets and workouts were implemented.
    2. The method to create a session was implemented.
    3. Finished the views to the methods that list all exercises and sets in the frontend.
    4. Finished the views to the methods `create exercise` and `create set` in the frontend.
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

A bug in the method to reassing a trainer to a trainee were the user can reassign the trainer that is already assigned to the trainee was fixed.

The methods to list all sets and workouts were implemented with the following filters:
    - **Workouts:**
        - **name** - filter the workouts by the names that are similar to the passed value. 
        - **muscleGroup** - filter the workouts by the muscle group.
    - **Sets:**
        - **name** - filter the sets by the names that are similar to the passed value.
        - **type** - filter the sets by the type.

The method to create a workout was changed to store in muscleGroup a list of 3 muscle groups instead of a single muscle group to be more flexible and to allow the user to create a workout that works in multiple muscle groups.

Some other methods were improved to return the correct data and to be more efficient.

The method to create a session was implemented, this method creates a new session with the following fields:
    - **Trainee Id:** Trainee Identifier.
    - **Workout Id:** Workout Identifier.
    - **Begin Date:** Date when the session should start.
    - **End Date:** Date when the session should end, this field is optional (this field should be more important when the sessio is trainer guided).
    - **Type:** Type of the session (trainer guided or not).
    - **Notes:** Notes about the session.

---


## Database changes

To achieve the new requirements of the system, some changes were made in the database.

In this way, the tables were changed to the following:

![Database Diagram](./images/dbDiagram7.png)

---

## Planning for Next Week

- **Objectives:**
    1. Continue implementing the methods to manage sessions.
    2. Start implementing the feedback system.
    3. Finish the connection between the ViewExercises, ViewSets, CreateExercise and CreateSet with the backend.
    4. Finish the views ViewWorkout and CreateWorkout and his connection with the backend.
