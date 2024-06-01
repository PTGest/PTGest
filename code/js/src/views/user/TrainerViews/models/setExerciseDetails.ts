class SetExerciseDetails {
    id: number;
    name: string;
    details: Map<string, any>;

    constructor(id: number, name: string, details: Map<string, any>){
        this.id = id;
        this.name = name;
        this.details = details;
    }
}
export default SetExerciseDetails;
