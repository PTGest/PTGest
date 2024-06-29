import LoginUserData from "../../models/authModels/LoginUserData.ts"
import router from "../../plugins/router.ts"
import store from "../../store"

export async function loginUserServices(userLoginData: LoginUserData): Promise<void> {
    try {
        await fetch("http://localhost:8080/api/login", {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
            },
            credentials: "include",
            body: JSON.stringify(userLoginData),
        }).then((response) => {
            switch (response.status) {
                case 200:
                    response.json().then((response) => {
                        console.log(response.details)
                        store.dispatch("setAuthentication", {
                            id: response.details.userId,
                            role: response.details.role,
                        })
                        store.commit("setLogin", true)
                        router.push('/')
                        return response
                    })
                    break
                case 401:
                    if (!document.querySelector(".error-message")) {
                        const element = document.createElement("div")
                        element.innerHTML = "Invalid email or password"
                        element.classList.add("error-message")
                        element.style.color = "red"
                        element.style.padding = "0.5em"
                        document.getElementById("login-inputs-containers")?.appendChild(element)
                    }
                    break
                default:
                    response.json().then((response) => {
                        console.log(response.type)
                        console.log(response.title)
                        store.commit("setErrorType", { errorType: response.type, message: response.title })
                        router.push({ name: "error" })
                    })
                    break
            }
        })
    } catch (error: any) {
        await router.push({ name: "error", params: { errorMessage: "Failed to login", message: "error.message" } })
    }
}
