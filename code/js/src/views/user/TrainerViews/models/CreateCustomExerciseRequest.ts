class CreateCustomExerciseRequest{
    name: string
    description: string | null
    muscleGroup: string[]
    modality: string
    ref: string| null

    constructor(name: string, modality: string , muscleGroup: string[],description: string | null, ref: string | null) {
        this.name = name;
        this.modality = modality;
        this.muscleGroup = muscleGroup;
        this.description = description;
        this.ref = ref;
    }
}

export default CreateCustomExerciseRequest;

