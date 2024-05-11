import CompanyTrainers from "../../views/user/CompaniesViews/models/CompanyTrainers.ts";


export default async function getCompanyTrainers(
    skip: number,
    limit: number | null,
    availability: string | null,
    gender: string | null,
    //name: string | null
): Promise<CompanyTrainers> {
    // Construct URL with skip and limit parameters
    let url = `http://localhost:8080/api/company/trainers?skip=${skip}`;
    if (limit != null) {
        url += `&limit=${limit}`;
    }
    if (availability != null) {
        url += `&availability=${availability}`;
    }
    if (gender != null) {
        url += `&gender=${gender}`;
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
                    console.log(data);
                    const trainers = new CompanyTrainers(
                        data.details.trainers,
                        data.details.total
                    );
                    console.log(trainers);
                    return trainers;
                });
            case 400:
                throw new Error("Bad request");
            default:
                throw new Error("Failed to sign up");
        }
    });
}


