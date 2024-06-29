class ExerciseDetails {
    id: number
    name: string
    description: string | null
    muscleGroup: string[]
    modality: string
    ref: string
    constructor(id: number, name: string, description: string | null, muscleGroup: string[], modality: string, ref: string) {
        this.id = id
        this.name = name
        this.description = description
        this.muscleGroup = muscleGroup
        this.modality = modality
        this.ref = ref
    }
}

export default ExerciseDetails
