import SetExercise from "./SetExercise.ts";

class CreateCustomSetRequest {
    name: string | null
    notes: string | null
    type: string
    setExercises: SetExercise[]

    constructor(name: string | null, notes: string | null, type: string, setExercises: SetExercise[]){
        this.name = name
        this.notes = notes
        this.type = type
        this.setExercises = setExercises
    }
}
export default CreateCustomSetRequest;
