import store from "../../store"
import router from "../../plugins/router.ts"
import {useToast} from "primevue/usetoast";

export default async function logoutServices(): Promise<void> {
    const toast = useToast();
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
            toast.add({ severity: 'warn', summary: 'Warn Message', detail: 'Message Content', life: 3000 });
        }
    })
    return
}
