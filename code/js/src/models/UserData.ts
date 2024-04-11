class UserData {
    id: number | undefined;
    token: string | undefined;
    refreshToken: string | undefined;
    constructor(id : number | undefined, token: string | undefined, refreshToken: string | undefined = undefined) {
        this.id = id;
        this.token = token;
        this.refreshToken = refreshToken;
    }
}