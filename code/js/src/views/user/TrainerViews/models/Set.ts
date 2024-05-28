import Exercise from "./Exercise.ts";

class Set {

    id: number;
    name: string;
    notes: string;
    type: string;
    exercise: Exercise;
    reps: string;


    constructor(id: number, name: string, notes: string, type: string,reps: string, exercise: Exercise) {
        this.id = id;
        this.name = name;
        this.notes = notes;
        this.exercise = exercise;
        this.type = type;
        this.reps = reps;
    }
}

export default Set;
