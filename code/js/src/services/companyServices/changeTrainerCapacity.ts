import UpdateCapacity from "../../views/user/CompaniesViews/models/capacity.ts";

export default async function changeTrainerCapacity(trainerId: string, newCapacity: number): Promise<void> {
    console.log("trainerId",trainerId)
    console.log("newCapacity",newCapacity)
    // Logic to sign up
    fetch(`http://localhost:8080/api/company/trainer/${trainerId}/update-capacity`, {
        method: "PUT",
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify(new UpdateCapacity(newCapacity)),
        credentials: "include",
    }).then(async (response) => {
        switch (response.status) {
            case 200:
                return response.json().then((data) => {
                    console.log("Deu bom guys nao dei fumble",data)
                })
            case 400:
                throw new Error("Bad request")
            default:
                throw new Error("Failed to sign up")
        }
    })
}
