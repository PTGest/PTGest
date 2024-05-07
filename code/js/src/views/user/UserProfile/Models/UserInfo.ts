export class UserInfo {
    public name: string = ""
    public email: string = ""
    public phone: string = ""
    //public address: string = '';

    constructor(name: string, email: string, phone: string) {
        this.name = name
        this.email = email
        this.phone = phone
    }
}
