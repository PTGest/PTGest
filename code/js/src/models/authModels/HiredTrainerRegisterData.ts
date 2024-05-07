class HiredTrainerRegisterData {
    name: string
    email: string
    gender: string
    capacity: number
    phone: string
    user_type: string = "hired_trainer"

    constructor(name: string, email: string, gender: string, capacity: number, phone: string, user_type: string = "hired_trainer") {
        this.email = email
        this.name = name
        this.gender = gender
        this.capacity = capacity
        this.phone = phone
        this.user_type = user_type
    }
}

export default HiredTrainerRegisterData;
