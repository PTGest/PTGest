import store from "../../../store"
import router from "../../../plugins/router.ts"

async function fetchData(uri: string, method: string, bodyData: any | null): Promise<any> {
    console.log("fetchData uri", uri)
    console.log("BODY", bodyData)

    const body = bodyData ? JSON.stringify(bodyData) : null
    const response = await fetch(uri, {
        method: method.trim(),
        headers: {
            "Content-Type": "application/json",
        },
        body: body,
        credentials: "include",
    })

    if (response.ok) {
        console.log("Operation successful")
        return await response.json()
    }

    if (!response.ok) {
        if (response.status === 400) {
            throw new Error("Bad request")
        } else if (response.status === 401) {
            store.commit("setLogin", false)
            await router.push({ name: "login" })
            return response
        }
    }
}
export default fetchData
