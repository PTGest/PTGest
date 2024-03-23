class SignupPT {
    name: string;
    email: string;
    password: string;
    gender: string;
    phoneNumber: string;

    constructor(name: string, email: string, password: string, gender: string, phoneNumber: string) {
        this.name = name;
        this.email = email;
        this.password = password
        this.gender = gender
        this.phoneNumber = phoneNumber
    }
}

export default SignupPT;