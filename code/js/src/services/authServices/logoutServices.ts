import store from "../../store"
import router from "../../plugins/router.ts"

export default async function logoutServices(): Promise<void> {
    // Logic to sign up
    fetch("http://localhost:8080/api/auth/logout", {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
        },
        credentials: "include",
    }).then((response) => {
        if (response.ok) {
            console.log("Deu Bom familia nao dei fumble da bag")
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
