import SetExerciseDetails from "./SetExerciseDetails.ts"

class SetDetails {
    id: number
    name: string
    notes: string | null
    type: string
    orderId: number
    setExerciseDetails: SetExerciseDetails[]

    constructor(id: number, name: string, notes: string, type: string, orderId: number,setExerciseDetails: SetExerciseDetails[]) {
        this.id = id
        this.name = name
        this.notes = notes
        this.type = type
        this.orderId = orderId
        this.setExerciseDetails = setExerciseDetails
    }
}

export default SetDetails
