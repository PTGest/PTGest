import AssignTrainerRequest from "../../views/user/CompaniesViews/models/AssignTrainerRequest.ts"
import ReassignTrainerRequest from "../../views/user/CompaniesViews/models/ReassignTrainerRequest.ts"
import { apiBaseUri } from "../utils/envUtils.ts"
import UpdateCapacity from "../../views/user/CompaniesViews/models/capacity.ts";
import CompanyTrainers from "../../views/user/CompaniesViews/models/CompanyTrainers.ts";
import CompanyTrainees from "../../views/user/CompaniesViews/models/CompanyTrainees.ts";

async function assignOrReassignTrainer(traineeId: string, trainerId: string, isReassign: boolean): Promise<void> {
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
            case 400:
                throw new Error("Bad request")
            case 401:
                throw new Error("Unauthorized")
            default:
                throw new Error("Something went wrong")
        }
    })
}

async function changeTrainerCapacity(trainerId: string, newCapacity: number): Promise<void> {
    // Logic to sign up
    fetch(`${apiBaseUri}/api/company/trainer/${trainerId}/update-capacity`, {
        method: "PUT",
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify(new UpdateCapacity(newCapacity)),
        credentials: "include",
    }).then(async (response) => {
        switch (response.status) {
            case 200:
                return
            case 400:
                throw new Error("Bad request")
            case 401:
                throw new Error("Unauthorized")
            default:
                throw new Error("Something went wrong")
        }
    })
}

async function getCompanyTrainersOrTrainees(
    skip: number | null,
    limit: number | null,
    availability: string | null,
    gender: string | null,
    isTrainees: boolean,
    name: string | null
): Promise<CompanyTrainers | CompanyTrainees> {
    let url = `${apiBaseUri}/api/company/`
    if (isTrainees) {
        url += `trainees`
    } else {
        url += `trainers`
    }
    // Construct URL with skip and limit parameters
    if (skip != null) {
        url += `?skip=${skip}`
    }
    if (limit != null) {
        url += `&limit=${limit}`
    }
    if (availability != null) {
        url += `&availability=${availability}`
    }
    if (gender != null) {
        url += `&gender=${gender}`
    }
    if (name != null) {
        url += `&name=${name}`
    }

    // Make the API call
    return fetch(url, {
        method: "GET",
        headers: {
            "Content-Type": "application/json",
        },
        credentials: "include",
    }).then(async (response) => {
        switch (response.status) {
            case 200:
                return response.json().then((data) => {
                    console.log(data)

                    if (isTrainees) {
                        console.log(data.details.trainees)
                        console.log(data.details.total)
                        const trainees = new CompanyTrainees(data.details.items, data.details.total)
                        console.log(trainees)
                        return trainees
                    } else {
                        const trainers = new CompanyTrainers(data.details.items, data.details.total)
                        console.log(trainers)
                        return trainers
                    }
                })
            case 400:
                throw new Error("Bad request")
            default:
                throw new Error("Failed to sign up")
        }
    })
}

export {
    assignOrReassignTrainer,
    changeTrainerCapacity,
    getCompanyTrainersOrTrainees
}
