class Trainer {
    name: string
    gender: string
    ref: string
    capacity: number
    assignedTrainees: number

    constructor(name: string, gender: string, ref: string, capacity: number, assignedTrainees: number) {
        this.name = name;
        this.gender = gender;
        this.ref = ref;
        this.capacity = capacity;
        this.assignedTrainees = assignedTrainees;
    }
}


export default Trainer;
