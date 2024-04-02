class SignupPT {
    name: string;
    email: string;
    password: string;
    gender: string;
    phoneNumber: string;
    user_type: string;

    constructor(name: string, email: string, password: string, gender: string, phoneNumber: string, user_type: string) {
        this.name = name;
        this.email = email;
        this.password = password
        this.gender = gender
        this.phoneNumber = phoneNumber
        this.user_type = user_type
    }
}

export default SignupPT;