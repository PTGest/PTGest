import TraineeRegisterData from "../../views/user/userRegister/models/TraineeRegisterData.ts"
import router from "../../plugins/router.ts"
import HiredTrainerRegisterData from "../../models/authModels/HiredTrainerRegisterData.ts"
import RBAC from "../utils/RBAC/RBAC.ts"
import { apiBaseUri } from "../utils/envUtils.ts"
import fetchData from "../utils/fetchUtils/fetchData.ts"
import ChangePasswordRequest from "../../views/auth/models/ChangePasswordRequest.ts"
import LoginUserData from "../../models/authModels/LoginUserData.ts"
import store from "../../store/index.ts"
import ResetPasswordData from "../../models/authModels/ResetPasswordData.ts"
import SignupPTData from "../../models/authModels/SignupPTData.ts"

async function authenticatedSignup(userRegisterData: TraineeRegisterData | HiredTrainerRegisterData): Promise<void> {
    // Logic to sign up
    fetch(`${apiBaseUri}/api/auth/signup`, {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
        },
        credentials: "include",
        body: JSON.stringify(userRegisterData),
    }).then((response) => {
        switch (response.status) {
            case 201:
                if (userRegisterData instanceof TraineeRegisterData) {
                    if (RBAC.isCompany()) {
                        response.json().then((data) => {
                            const userId = data.details.userId
                            router.push({
                                name: "assignTrainer",
                                params: { traineeId: userId, assignTrainer: "assignTrainer" },
                            })
                        })
                    } else {
                        router.push("/trainees")
                    }
                } else {
                    router.push("/trainers")
                }
                break
            case 409:
                throw new Error("Email already exists")
            default:
                throw new Error("Failed to sign up")
        }
    })
}

async function changeUserPassword(currentPassword: string, newPassword: string) {
    const uri = `${apiBaseUri}/api/auth/change-password`
    return await fetchData(uri, "GET", new ChangePasswordRequest(currentPassword, newPassword))
}

async function forgetPasswordServices(email: string): Promise<void> {
    // Logic to sign up
    return fetch(`${apiBaseUri}/api/forget-password`, {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify({ email: email }),
    }).then((response) => {
        if (response.ok) {
            return response.json()
        } else {
            throw new Error("Failed to send email")
        }
    })
}

async function loginUserServices(userLoginData: LoginUserData): Promise<void> {
    try {
        await fetch(`${apiBaseUri}/api/login`, {
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
                        store.dispatch("setAuthentication", {
                            id: response.details.userId,
                            role: response.details.role,
                        })
                        store.commit("setLogin", true)
                        router.push("/")
                        return response
                    })
                    break
                case 401:
                    router.go(0)
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

async function logoutServices(): Promise<void> {
    // Logic to sign up
    fetch(`${apiBaseUri}/api/auth/logout`, {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
        },
        credentials: "include",
    }).then((response) => {
        if (response.ok) {
            store.commit("logout")
            store.commit("setLogin", false)
            router.push({ name: "login" })
            return response.json()
        } else {
            throw new Error("Erro ao fazer logout")
        }
    })
    return
}

async function resetPasswordServices(resetPasswordData: ResetPasswordData, token: string | string[]): Promise<void> {
    try {
        await fetch(`${apiBaseUri}/api/reset-password/${token}`, {
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
                    return response
                })
            }
        })
    } catch (error: any) {
        await router.push({ name: "error", params: { errorMessage: "Failed to login", message: "error.message" } })
    }
}

async function isSigned() {
    const uri = `${apiBaseUri}/api/auth/validate`
    const response = await fetchData(uri, "GET", null)
     if(response.message === "User authenticated successfully.") {
        store.commit("setLogin", true)
        return response
     }else{
         store.commit("setClearUserData")
     }
}

async function signupUserServices(userData: SignupPTData): Promise<void> {
    // Logic to sign up
    fetch(`${apiBaseUri}/api/signup`, {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify(userData),
    }).then((response) => {
        if (response.status === 201) {
            router.push({ name: "login" })
            return response.json()
        } else if (response.status === 409) {
            const element = document.createElement("div")
            element.innerHTML = "userServices already exists"
            element.classList.add("error-message")
            element.style.color = "red"
            element.style.padding = "0.5em"
            document.getElementById("signup-container")?.appendChild(element)
        } else {
            response.json().then((response) => {
                store.commit("setErrorType", { type: response.type, message: response.title })
                router.push({ name: "error" })
            })
        }
    })
}

async function verifyToken(token: string | string[]): Promise<boolean> {
    await fetch(`${apiBaseUri}/api/validate-password-reset-token/${token}`, {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
        },
        credentials: "include",
        // body: JSON.stringify(token),
    }).then((response) => {
        switch (response.status) {
            case 200:
                response.json().then((response) => {
                    return true
                })
                break
            case 401:
                return false

            default:
                response.json().then(() => {
                    return false
                })
                break
        }
    })
    return false
}

export { authenticatedSignup, changeUserPassword, forgetPasswordServices, loginUserServices, logoutServices, resetPasswordServices, isSigned, signupUserServices, verifyToken }
