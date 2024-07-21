import Workout from "./Workout.ts"

class Workouts {
    workouts: Workout[]
    nOfWorkouts: number

    constructor(workouts: Workout[], nOfWorkouts: number) {
        this.workouts = workouts
        this.nOfWorkouts = nOfWorkouts
    }
}
export default Workouts
