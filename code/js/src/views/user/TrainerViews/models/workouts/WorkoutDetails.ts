import SetDetails from "../sets/SetDetails.ts";

class WorkoutDetails {
    id: number
    name: string
    description: string | null
    muscleGroup: string[]
    sets: SetDetails[]

    constructor(id: number, name: string, description: string | null, muscleGroup: string[], sets: SetDetails[]) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.muscleGroup = muscleGroup;
        this.sets = sets;
    }
}
export default WorkoutDetails;
