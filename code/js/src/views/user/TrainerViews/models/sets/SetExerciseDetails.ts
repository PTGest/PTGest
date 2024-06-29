class SetExerciseDetails {
    id: number
    name: string
    muscleGroup: string[] | null
    modality: string | null
    details: Map<string, any>

    constructor(id: number, name: string, muscleGroup: string[] | null, modality: string | null, details: Map<string, any>) {
        this.id = id
        this.name = name
        this.muscleGroup = muscleGroup
        this.modality = modality
        this.details = details
    }
}
export default SetExerciseDetails
