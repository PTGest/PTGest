class Trainer {
    id: string
    name: string
    gender: string
    capacity: number
    assignedTrainees: number

    constructor(id: string, name: string, gender: string,  capacity: number, assignedTrainees: number) {
        this.id = id;
        this.name = name;
        this.gender = gender;
        this.capacity = capacity;
        this.assignedTrainees = assignedTrainees;
    }
}


export default Trainer;
