import TraineeRegisterData from "../../views/user/UserRegister/models/TraineeRegisterData.ts";

export default async function authenticatedSignup(userRegisterData: TraineeRegisterData | HiredTrainerRegisterData): Promise<void> {
    // Logic to sign up
    fetch("http://localhost:8080/api/auth/signup", {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
        },
        credentials: "include",
        body: JSON.stringify(userRegisterData),
    }).then((response) => {
        if (response.ok) {
            console.log("Deu Bom familia nao dei fumble da bag")
            return response.json()
        } else {
            throw new Error("Failed to send email")
        }
    })
    return
}
