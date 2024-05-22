class UserData {
    id: number | undefined
    role: string | undefined
    refreshToken: string | undefined
    constructor(id: number | undefined, role: string | undefined) {
        this.id = id
        this.role = role
    }
}

export default UserData
