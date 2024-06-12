import fetchData from "../../utils/fetchData.ts";


async function likeSet(setId: string): Promise<void> {
    const uri = `http://localhost:8080/api/trainer/set/${setId}/favorite`;

    try {
        await fetchData(uri, "POST", null);
        return
    } catch (error) {
        console.error("Error fetching set details:", error);
        throw error;
    }
}
export default likeSet;
