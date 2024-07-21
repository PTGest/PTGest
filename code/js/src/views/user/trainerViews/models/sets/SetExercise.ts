class SetExercise {
    exerciseId: number
    details: Map<string, any>

    constructor(exerciseId: number, details: Map<string, any>) {
        this.exerciseId = exerciseId
        this.details = details
    }
}
export default SetExercise
