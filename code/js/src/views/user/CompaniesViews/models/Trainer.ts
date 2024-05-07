class Trainer {
    id: number
    name: string
    gender: string
    totalTrainees: number
    capacity: number


    constructor(id: number, name: string, gender: string, totalTrainees: number, capacity: number) {
        this.id = id;
        this.name = name;
        this.gender = gender;
        this.totalTrainees = totalTrainees;
        this.capacity = capacity;
    }
}


export default Trainer;
