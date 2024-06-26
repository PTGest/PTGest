import fetchData from "../../utils/fetchData.ts"
import ExerciseDetails from "../../../views/user/TrainerViews/models/exercises/ExerciseDetails.ts"

export default async function getExerciseDetails(exerciseId: number): Promise<ExerciseDetails> {
    const uri = "http://localhost:8080/api/trainer/exercise/" + exerciseId

    try {
        const response = await fetchData(uri, "GET", null)

        return new ExerciseDetails(exerciseId, response.details.name, response.details.description, response.details.muscleGroup, response.details.type, response.details.ref)
    } catch (error) {
        console.error("Error fetching exercises:", error)
        throw error
    }
}
