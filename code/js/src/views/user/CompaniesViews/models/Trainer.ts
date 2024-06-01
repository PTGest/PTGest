class Trainer {
    id: string
    name: string
    gender: string
    assignedTrainees: number
    capacity: number


    constructor(id: string, name: string, gender: string, assignedTrainees: number,capacity: number, ) {
        this.id = id;
        this.name = name;
        this.gender = gender;
        this.assignedTrainees = assignedTrainees;
        this.capacity = capacity;
    }
}


export default Trainer;
