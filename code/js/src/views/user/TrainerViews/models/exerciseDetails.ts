class ExerciseDetails {

    id : string;
    name : string;
    description : string;
    muscleGroup : string[];
    modality : string;
    ref : string;
    constructor(id: string, name: string, description: string, muscleGroup: string[], modality: string, ref: string) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.muscleGroup = muscleGroup;
        this.modality = modality;
        this.ref = ref;
    }

}
