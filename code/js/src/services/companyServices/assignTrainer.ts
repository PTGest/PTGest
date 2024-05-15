import AssignTrainerRequest from "../../views/user/CompaniesViews/models/AssignTrainerRequest.ts";
import router from "../../plugins/router.ts";

export default async function assignTrainer(traineeId: string,trainerId: string): Promise<void> {
    // Logic to sign up
    fetch(`http://localhost:8080/api/company/trainee/${traineeId}/assign-trainer`, {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify(new AssignTrainerRequest(trainerId)),
        credentials: "include",
    }).then(async (response) => {
        switch (response.status) {
            case 201:
                return response.json().then((data) => {
                    console.log("Deu bom guys nao dei fumble",data)
                    router.push({name: "trainees"})
                })
            case 400:
                throw new Error("Bad request")
            default:
                throw new Error("Failed to sign up")
        }
    })
}
