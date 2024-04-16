class ResetPasswordData {
    public password: string
    public token: string

    constructor(password: string, token: string) {
        this.password = password
        this.token = token
    }
}

export default ResetPasswordData
