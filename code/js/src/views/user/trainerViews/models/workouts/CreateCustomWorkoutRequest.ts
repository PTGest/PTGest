class CreateCustomWorkoutRequest {
    name: string | null
    description: string | null
    muscleGroup: string[]
    sets: number[]

    constructor(name: string | null, description: string | null, muscleGroup: string[], sets: number[]) {
        this.name = name
        this.description = description
        this.muscleGroup = muscleGroup
        this.sets = sets
    }
}
export default CreateCustomWorkoutRequest
