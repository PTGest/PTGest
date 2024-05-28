class CreateCustomSetRequest{
    name: string
    notes: string
    type: string
    setExercise: SetExercise


    constructor(name: string, notes: string, type: string, setExercises: SetExercise){
        this.name = name;
        this.notes = notes;
        this.type = type;
        this.setExercise = setExercises;
    }
}

export default CreateCustomSetRequest;

