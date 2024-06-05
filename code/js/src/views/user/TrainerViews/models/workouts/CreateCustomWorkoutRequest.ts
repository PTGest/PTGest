class CreateCustomWorkoutRequest {
    name: string | null
    description: string | null
    muscleGroup: string[]
    sets: number[]

    constructor(name: string | null, muscleGroup: string[], description: string | null, sets: number[]) {
        this.name = name;
        this.muscleGroup = muscleGroup;
        this.description = description;
        this.sets = sets;
    }
}
export default CreateCustomWorkoutRequest;
