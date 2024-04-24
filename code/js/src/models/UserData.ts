class UserData {
    id: number | undefined
    role: string | undefined
    token: string | undefined
    refreshToken: string | undefined
    constructor(id: number | undefined, role: string | undefined ,token: string | undefined, refreshToken: string | undefined = undefined) {
        this.id = id
        this.role = role
        this.token = token
        this.refreshToken = refreshToken
    }
}

export default UserData
