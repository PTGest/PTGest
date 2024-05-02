class TraineeRegisterData {
    name: string;
    email: string;
    birthdate: string;
    gender: string;
    phoneNumber: string;
    user_type: string = "trainee";
    constructor(name: string, email: string, birthdate: string, gender: string, phoneNumber: string, user_type: string = "trainee") {
        this.name = name;
        this.email = email;
        this.birthdate = birthdate;
        this.gender = gender;
        this.phoneNumber = phoneNumber;
        this.user_type = user_type;
    }
}

export default TraineeRegisterData;
