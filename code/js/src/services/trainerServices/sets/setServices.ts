import fetchData from "../../utils/fetchUtils/fetchData.ts"
import CreateCustomSetRequest from "../../../views/user/trainerViews/models/sets/CreateCustomSetRequest.ts"
import mapToObject from "../../utils/fetchUtils/mapToObject.ts"
import router from "../../../plugins/router.ts"
import SetDetails from "../../../views/user/trainerViews/models/sets/SetDetails.ts"
import Sets from "../../../views/user/trainerViews/models/sets/Sets.ts"
import handleFilters from "../../utils/fetchUtils/handleFilters.ts"
import { apiBaseUri } from "../../utils/envUtils.ts"

// Function to convert Map to Object

async function createSet(setData: CreateCustomSetRequest): Promise<number> {
    try {
        // Convert setData to Object
        setData.setExercises.map((setExercise) => {
            setExercise.details = mapToObject(setExercise.details)
        })
        const response = await fetchData(`${apiBaseUri}/api/trainer/set/create`, "POST", setData)
        router.go(0)
        return response.resourceId
    } catch (error) {
        console.error("Error creating set:", error)
        throw error
    }
}

async function getSetDetails(setId: number): Promise<SetDetails> {
    const uri = `${apiBaseUri}/api/trainer/set/` + setId

    try {
        const response = await fetchData(uri, "GET", null)
        return response.details
    } catch (error) {
        console.error("Error fetching set details:", error)
        throw error
    }
}

async function getSets(filters: Map<string, any> | null): Promise<Sets> {
    const uri = `${apiBaseUri}/api/trainer/sets`
    let postFiltersUri = uri

    if (filters != null) {
        postFiltersUri = handleFilters(uri, filters)
    }
    try {
        const response = await fetchData(postFiltersUri, "GET", null)
        return new Sets(response.details.items, response.details.total)
    } catch (error) {
        console.error("Error fetching set:", error)
        throw error
    }
}

async function likeSet(setId: string): Promise<void> {
    const uri = `${apiBaseUri}/api/trainer/set/${setId}/favorite`
    try {
        await fetchData(uri, "POST", null)
        return
    } catch (error) {
        console.error("Error fetching set details:", error)
        throw error
    }
}

async function unlikeSet(setId: string): Promise<void> {
    const uri = `${apiBaseUri}/api/trainer/set/${setId}/unfavorite`

    try {
        await fetchData(uri, "DELETE", null)
        return
    } catch (error) {
        console.error("Error fetching set details:", error)
        throw error
    }
}

export { createSet, getSetDetails, getSets, likeSet, unlikeSet }
