class Exercise{
    id: number;
    name: string;
    muscleGroup: string[];
    modality: string;
    constructor(id: number, name: string, muscleGroup: string[], modality: string) {
        this.id = id;
        this.name = name;
        this.muscleGroup = muscleGroup;
        this.modality = modality;
    }
}

export default Exercise;
