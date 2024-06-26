import TraineeRegisterData from "../../views/user/UserRegister/models/TraineeRegisterData.ts"
import router from "../../plugins/router.ts"
import HiredTrainerRegisterData from "../../models/authModels/HiredTrainerRegisterData.ts"
import RBAC from "../utils/RBAC/RBAC.ts"

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
    return
}
