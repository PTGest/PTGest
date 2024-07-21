class UserInfo {
    name: string
    email: string
    gender: string
    birthdate: Date
    phoneNumber: string

    constructor(name: string, email: string, gender: string, birthdate: Date, phoneNumber: string) {
        this.name = name
        this.email = email
        this.gender = gender
        this.birthdate = birthdate
        this.phoneNumber = phoneNumber
    }
}
export default UserInfo
