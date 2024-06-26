class Workout {
    id: number
    name: string
    description: string | null
    muscleGroup: string[]

    constructor(id: number, name: string, description: string | null, muscleGroup: string[]) {
        this.id = id
        this.name = name
        this.description = description
        this.muscleGroup = muscleGroup
    }
}
export default Workout
