import CreateCustomExerciseRequest from "../../../views/user/trainerViews/models/exercises/CreateCustomExerciseRequest.ts"
import fetchData from "../../utils/fetchUtils/fetchData.ts"
import router from "../../../plugins/router.ts"
import ExerciseDetails from "../../../views/user/trainerViews/models/exercises/ExerciseDetails.ts"
import Exercises from "../../../views/user/trainerViews/models/exercises/Exercises.ts"
import handleFilters from "../../utils/fetchUtils/handleFilters.ts"
import { apiBaseUri } from "../../utils/envUtils.ts"

async function createExercise(exercise: CreateCustomExerciseRequest): Promise<void> {
    try {
        const response = await fetchData(`${apiBaseUri}/api/trainer/exercise/create`, "POST", exercise)
        router.go(-1)
        return response.resourceId
    } catch (error) {
        console.error("Error creating exercise:", error)
        throw error
    }
}

async function getExerciseDetails(exerciseId: number): Promise<ExerciseDetails> {
    const uri = `${apiBaseUri}/api/trainer/exercise/` + exerciseId

    try {
        const response = await fetchData(uri, "GET", null)
        return new ExerciseDetails(exerciseId, response.details.name, response.details.description, response.details.muscleGroup, response.details.type, response.details.ref)
    } catch (error) {
        console.error("Error fetching exercise details:", error)
        throw error
    }
}

async function getExercises(filters: Map<string, any> | null): Promise<Exercises> {
    const uri = `${apiBaseUri}/api/trainer/exercises`
    let postFiltersUri = uri
    if (filters != null) {
        postFiltersUri = handleFilters(uri, filters)
    }
    try {
        const response = await fetchData(postFiltersUri, "GET", null)
        return new Exercises(response.details.items, response.details.total)
    } catch (error) {
        throw error
    }
}

async function likeExercise(exerciseId: string): Promise<void> {
    const uri = `${apiBaseUri}/api/trainer/exercise/${exerciseId}/favorite`

    try {
        await fetchData(uri, "POST", null)
        return
    } catch (error) {
        console.error("Error liking exercise:", error)
        throw error
    }
}

async function unlikeExercise(exerciseId: string): Promise<void> {
    const uri = `${apiBaseUri}/api/trainer/exercise/${exerciseId}/unfavorite`

    try {
        await fetchData(uri, "DELETE", null)
        return
    } catch (error) {
        console.error("Error unliking exercise:", error)
        throw error
    }
}

export { createExercise, getExerciseDetails, getExercises, likeExercise, unlikeExercise }
