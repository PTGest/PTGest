class TraineeRegisterData {
    name: string;
    email: string;
    birthDate: string;
    gender: string;
    phone: string;

    constructor(name: string, email: string, birthDate: string, gender: string, phone: string) {
        this.name = name;
        this.email = email;
        this.birthDate = birthDate;
        this.gender = gender;
        this.phone = phone;
    }

}
export default TraineeRegisterData;
