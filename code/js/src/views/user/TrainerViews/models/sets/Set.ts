
class Set {

    id: number;
    name: string;
    notes: string;
    type: string;
    reps: string;


    constructor(id: number, name: string, notes: string, type: string,reps: string) {
        this.id = id;
        this.name = name;
        this.notes = notes;
        this.type = type;
        this.reps = reps;
    }
}

export default Set;
