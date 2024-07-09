class ChangePasswordRequest {
    currentPassword: string
    newPassword: string
    constructor(currentPassword: string, newPassword: string) {
        this.currentPassword = currentPassword
        this.newPassword = newPassword
    }
}
export default ChangePasswordRequest
