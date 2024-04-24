import router from "../../plugins/router.ts"
import store from "../../store"
import ResetPasswordData from "../../models/authModels/ResetPasswordData.ts"

export async function resetPasswordServices(resetPasswordData: ResetPasswordData, token : String | String[]): Promise<void> {
    try {
        await fetch(`http://localhost:8080/api/reset-password/${token}`, {
            method: "PUT",
            headers: {
                "Content-Type": "application/json",
            },
            credentials: "include",
            body: JSON.stringify(resetPasswordData),
        }).then((response) => {
            if (response.status === 200) {
                response.json().then((response) => {
                    router.push({ name: "login" })
                    store.commit("setUserData", { id: 1, token: response.token, refreshToken: response.refreshToken })
                    console.log('Deu bom familia nao dei fumble da bag')
                    return response
                })
            }
        })
    } catch (error: any) {
        await router.push({ name: "error", params: { errorMessage: "Failed to login", message: "error.message" } })
    }
}
