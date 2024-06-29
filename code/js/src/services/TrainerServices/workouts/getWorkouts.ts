import fetchData from "../../utils/fetchData.ts"
import Workouts from "../../../views/user/TrainerViews/models/workouts/Workouts.ts"

async function getWorkouts(): Promise<Workouts> {
    try {
        const response = await fetchData(`http://localhost:8080/api/trainer/workouts`, "GET", null)
        return new Workouts(response.details.items, response.details.total)
    } catch (error) {
        console.error("Error fetching workouts:", error)
        throw error
    }
}
export default getWorkouts
