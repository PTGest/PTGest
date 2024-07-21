class Workout {
    id: number
    name: string
    description: string | null
    muscleGroup: string[]
    isFavorite: boolean

    constructor(id: number, name: string, description: string | null, muscleGroup: string[], isFavorite: boolean) {
        this.id = id
        this.name = name
        this.description = description
        this.muscleGroup = muscleGroup
        this.isFavorite = isFavorite
    }
}
export default Workout
