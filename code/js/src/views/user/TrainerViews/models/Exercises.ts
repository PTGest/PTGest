import Exercise from "./Exercise.ts";

class Exercises {
    exercises: Exercise[];
    nOfExercises: number;

    constructor(exercises: Exercise[]){
        this.exercises = exercises;
        this.nOfExercises = exercises.length;
    }
}

export default Exercises;
