class UserData {
    id: number | undefined
    token: string | undefined
    refreshToken: string | undefined
    profileImage: string | undefined
    constructor(id: number | undefined, token: string | undefined, refreshToken: string | undefined = undefined, profileImage: string = "") {
        this.id = id
        this.token = token
        this.refreshToken = refreshToken
        this.profileImage = profileImage
    }
}

export default UserData
