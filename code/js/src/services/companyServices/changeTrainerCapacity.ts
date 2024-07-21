import UpdateCapacity from "../../views/user/companiesViews/models/UpdateCapacity.ts"
import { apiBaseUri } from "../utils/envUtils.ts"

export default async function changeTrainerCapacity(trainerId: string, newCapacity: number): Promise<void> {
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
