import fetchData from "../../utils/fetchUtils/fetchData.ts"
import TraineeDataDetails from "../../../views/user/trainerViews/models/traineeData/TraineeDataDetails.ts"
import { apiBaseUri } from "../../utils/envUtils.ts"
import addTraineeDataRequest from "../../../views/user/trainerViews/models/traineeData/AddTraineeDataRequest.ts"
import mapToObject from "../../utils/fetchUtils/mapToObject.ts"
import TraineeDataHistory from "../../../views/user/trainerViews/models/trainees/TraineeDataHistory.ts"

async function getTraineeData(traineeId: string) {
    const uri = `${apiBaseUri}/api/trainer/trainee/${traineeId}/data`

    try {
        const response = await fetchData(uri, "GET", null)
        return new TraineeDataHistory(response.details.items, response.details.total)
    } catch (error) {
        console.error("Error fetching trainee data:", error)
        throw error
    }
}

async function getTraineeDataDetails(traineeId: string, dataId: string) {
    const uri = `${apiBaseUri}/api/trainer/trainee/${traineeId}/data/${dataId}`

    try {
        const response = await fetchData(uri, "GET", null)
        return new TraineeDataDetails(response.details.date, response.details.bodyData)
    } catch (error) {
        console.error("Error fetching trainee data details:", error)
        throw error
    }
}
async function addTraineeData(data: addTraineeDataRequest) {
    const uri = `${apiBaseUri}/api/trainer/trainee/${data.traineeId}/data`
    if (data.skinFold !== null) {
        data.skinFold = mapToObject(data.skinFold)
    }
    try {
        const response = await fetchData(uri, "POST", data)
        return new TraineeDataDetails(response.details.date, response.details.bodyData)
    } catch (error) {
        console.error("Error creating trainee body data:", error)
        throw error
    }
}

export { getTraineeData, getTraineeDataDetails, addTraineeData }
