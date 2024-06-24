class Exercise{
    id: number;
    name: string;
    muscleGroup: string[];
    modality: string;
    isFavorite: boolean = false;
    constructor(id: number, name: string, muscleGroup: string[], modality: string, isFavorite: boolean = false) {
        this.id = id;
        this.name = name;
        this.muscleGroup = muscleGroup;
        this.modality = modality;
        this.isFavorite = isFavorite;
    }
}

export default Exercise;
