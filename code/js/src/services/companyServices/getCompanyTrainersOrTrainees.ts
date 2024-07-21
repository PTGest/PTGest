import CompanyTrainers from "../../views/user/companiesViews/models/CompanyTrainers.ts"
import CompanyTrainees from "../../views/user/companiesViews/models/CompanyTrainees.ts"
import { apiBaseUri } from "../utils/envUtils.ts"

export default async function getCompanyTrainersOrTrainees(
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
                    if (isTrainees) {
                        return new CompanyTrainees(data.details.items, data.details.total)
                    } else {
                        return  new CompanyTrainers(data.details.items, data.details.total)
                    }
                })
            case 400:
                throw new Error("Bad request")
            default:
                throw new Error("Failed to sign up")
        }
    })
}
