class HiredTrainerRegisterData {
    name: string;
    email: string;
    gender: string;
    capacity: number;
    phone: string;

    constructor(email: string, name: string, gender: string, capacity: number, phone: string) {
        this.email = email;
        this.name = name;
        this.gender = gender;
        this.capacity = capacity;
        this.phone = phone;
    }
}
