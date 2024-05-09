import TraineeRegisterData from "../../views/user/UserRegister/models/TraineeRegisterData.ts"
import router from "../../plugins/router.ts"
import HiredTrainerRegisterData from "../../models/authModels/HiredTrainerRegisterData.ts";

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
                if(userRegisterData instanceof TraineeRegisterData){
                    router.push("/trainees")
                }else{
                    router.push("/trainers")
                }
                return response.json()
            case 409:
                throw new Error("Email already exists")
            default:
                throw new Error("Failed to sign up")
        }
    })
    return
}
