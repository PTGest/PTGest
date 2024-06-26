import fetchData from "../../utils/fetchData.ts"

async function likeExercise(exerciseId: string): Promise<void> {
    const uri = `http://localhost:8080/api/trainer/exercise/${exerciseId}/favorite`

    try {
        await fetchData(uri, "POST", null)
        return
    } catch (error) {
        console.error("Error fetching set details:", error)
        throw error
    }
}
export default likeExercise
