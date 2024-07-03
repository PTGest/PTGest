import {apiBaseUri} from "../../main.ts";

export async function verifyToken(token: string | string[]): Promise<boolean> {
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
                    console.log(response.details)
                    return true
                })
                break
            case 401:
                console.log("Alguma coisa deu Merda")
                return false

            default:
                response.json().then(() => {
                    console.log("Alguma coisa deu Merda")
                    return false
                })
                break
        }
    })
    return false
}
