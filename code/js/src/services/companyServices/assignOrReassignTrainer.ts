import AssignTrainerRequest from "../../views/user/CompaniesViews/models/AssignTrainerRequest.ts"
import router from "../../plugins/router.ts"
import ReassignTrainerRequest from "../../views/user/CompaniesViews/models/ReassignTrainerRequest.ts"
import { apiBaseUri } from "../utils/envUtils.ts"

export default async function assignOrReassignTrainer(traineeId: string, trainerId: string, isReassign: boolean): Promise<void> {
    let url = `${apiBaseUri}/api/company/trainee/${traineeId}/assign-trainer`
    let method = "POST"
    let body: AssignTrainerRequest | ReassignTrainerRequest = new AssignTrainerRequest(trainerId)
    if (isReassign) {
        url = `http://localhost:8080/api/company/trainee/${traineeId}/reassign-trainer`
        method = "PUT"
        body = new ReassignTrainerRequest(trainerId)
    }
    // Logic to sign up
    fetch(url, {
        method: method,
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify(body),
        credentials: "include",
    }).then(async (response) => {
        switch (response.status) {
            case 201:
                return response.json().then(() => {
                    router.push({ name: "trainees" })
                })
            case 400:
                throw new Error("Bad request")
            case 401:
                throw new Error("Unauthorized")
            default:
                throw new Error("Something went wrong")
        }
    })
}
