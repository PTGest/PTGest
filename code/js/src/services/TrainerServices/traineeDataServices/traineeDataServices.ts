import fetchData from "../../utils/fetchData.ts";
import TraineeDataDetails from "../../../views/user/TrainerViews/models/traineeData/TraineeDataDetails.ts";
import {apiBaseUri} from "../../../main.ts";

async function getTraineeDataDetails(traineeId: string, dataId: string) {
    const uri = `${apiBaseUri}/api/trainer/trainee-data/${traineeId}/${dataId}`

    try {
        const response = await fetchData(uri, "GET", null)
        return new TraineeDataDetails(response.details.date, response.details.bodyData)
    } catch (error) {
        console.error("Error fetching set details:", error)
        throw error
    }
}

export {
    getTraineeDataDetails
}
