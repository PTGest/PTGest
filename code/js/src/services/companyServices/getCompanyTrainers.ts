import CompanyTrainers from "../../views/user/CompaniesViews/models/CompanyTrainers.ts";


export default async function getCompanyTrainers(): Promise<CompanyTrainers> {
    // Logic to sign up
    return fetch("http://localhost:8080/api/company/trainers", {
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
                    const trainers =  new CompanyTrainers(
                        data.details.trainers,
                        data.details.total)
                    console.log(trainers);
                    return trainers
                })
            case 400:
                throw new Error("Bad request")
            default:
                throw new Error("Failed to sign up")
        }
    })
}

