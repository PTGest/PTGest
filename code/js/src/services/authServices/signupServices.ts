import SignupPTData from "../../models/authModels/SignupPTData.ts"
import router from "../../plugins/router.ts"
import store from "../../store"
import {apiBaseUri} from "../utils/envUtils.ts";

export async function signupUserServices(userData: SignupPTData): Promise<void> {
    // Logic to sign up
    fetch(`${apiBaseUri}/api/signup`, {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify(userData),
    }).then((response) => {
        if (response.status === 201) {
            router.push({name: "login"})
            return response.json()
        } else if (response.status === 409) {
            const element = document.createElement("div")
            element.innerHTML = "UserServices already exists"
            element.classList.add("error-message")
            element.style.color = "red"
            element.style.padding = "0.5em"
            document.getElementById("signup-container")?.appendChild(element)
        } else {
            response.json().then((response) => {
                store.commit("setErrorType", {type: response.type, message: response.title})
                router.push({name: "error"})
            })
        }
    })
}
