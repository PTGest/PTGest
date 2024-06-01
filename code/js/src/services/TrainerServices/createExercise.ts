import CreateCustomExerciseRequest from "../../views/user/TrainerViews/models/CreateCustomExerciseRequest.ts";
async function createExercise(exercise: CreateCustomExerciseRequest): Promise<void> {

    return fetch(`http://localhost:8080/api/trainer/custom-exercise`, {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify(exercise),
        credentials: "include",
    }).then(async (response) => {
        switch (response.status) {
            case 201:
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

export default createExercise;
