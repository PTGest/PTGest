class HiredTrainerRegisterData {
    name: string
    email: string
    gender: string
    capacity: number
    phoneNumber: string
    user_type: string = "hired_trainer"

    constructor(name: string, email: string, gender: string, capacity: number, phone: string, user_type: string = "hired_trainer") {
        this.email = email
        this.name = name
        this.gender = gender
        this.capacity = capacity
        this.phoneNumber = phone
        this.user_type = user_type
    }
}

export default HiredTrainerRegisterData
