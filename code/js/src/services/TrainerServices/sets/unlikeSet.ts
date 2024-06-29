import fetchData from "../../utils/fetchData.ts"

async function unlikeSet(setId: string): Promise<void> {
    const uri = `http://localhost:8080/api/trainer/set/${setId}/unfavorite`

    try {
        await fetchData(uri, "DELETE", null)
        return
    } catch (error) {
        console.error("Error fetching set details:", error)
        throw error
    }
}
export default unlikeSet
