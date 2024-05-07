

export default async function getCompanyTrainers(): Promise<void> {
    // Logic to sign up
    fetch("http://localhost:8080/api/company/trainers", {
        method: "GET",
        headers: {
            "Content-Type": "application/json",
        },
        credentials: "include",
    }).then((response) => {
        switch (response.status) {
            case 201:
                console.log("Trainers fetched successfully")
                console.log(response.json())
                return response.json()
            case 400:
                throw new Error("Bad request")
            default:
                throw new Error("Failed to sign up")
        }
    })
    return
}
