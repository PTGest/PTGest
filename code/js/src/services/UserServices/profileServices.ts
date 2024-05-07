import router from "../../plugins/router.ts"
import store from "../../store"
import { UserInfo } from "../../views/user/UserProfile/Models/UserInfo.ts"

export async function getUserInfo(): Promise<void> {
    // Logic to sign up
    fetch("http://localhost:8080/api/profile", {
        method: "GET",
        headers: {
            "Content-Type": "application/json",
        },
        credentials: "include",
    }).then((response) => {
        switch (response.status) {
            case 200:
                response.json().then((response) => {
                    console.log(response)
                    store.dispatch("userInfo", new UserInfo(response.details.name, response.details.email, response.details.phoneNumber))
                })
                break

            default:
                response.json().then((response) => {
                    store.commit("setErrorType", { type: response.type, message: response.title })
                    router.push({ name: "error" })
                })
                break
        }
    })
    return
}
