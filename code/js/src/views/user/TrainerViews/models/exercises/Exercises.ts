import Exercise from "./Exercise.ts"

class Exercises {
    exercises: Exercise[]
    nOfExercises: number

    constructor(exercises: Exercise[], nOfExercises: number) {
        this.exercises = exercises
        this.nOfExercises = nOfExercises
    }
}

export default Exercises
