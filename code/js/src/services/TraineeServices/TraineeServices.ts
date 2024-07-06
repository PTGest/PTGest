import TrainerSessions from "../../views/user/TrainerViews/models/sessions/TrainerSessions.ts";
import {apiBaseUri} from "../utils/envUtils.ts";
import handleFilters from "../utils/fetchUtils/handleFilters.ts";
import fetchData from "../utils/fetchUtils/fetchData.ts";
import TraineeDataHistory from "../../views/user/TrainerViews/models/trainees/TraineeDataHistory.ts";
import Reports from "../../views/user/TrainerViews/models/reports/Reports.ts";
import ReportDetails from "../../views/user/TrainerViews/models/reports/ReportDetails.ts";
import TraineeDataDetails from "../../views/user/TrainerViews/models/traineeData/TraineeDataDetails.ts";

async function getTraineeSessions(filters: Map<string, any> | null): Promise<TrainerSessions> {
    const uri = `${apiBaseUri}/api/trainee/sessions`
    let postFiltersUri = uri

    if (filters != null) {
        postFiltersUri = handleFilters(uri, filters)
    }
    try {
        const response = await fetchData(postFiltersUri, "GET", null)
        return new TrainerSessions(response.details.items, response.details.total)
    } catch (error) {
        console.error("Error fetching set:", error)
        throw error
    }
}

async function getTTraineeData() {
    const uri = `${apiBaseUri}/api/trainee/data`
    try {
        const response = await fetchData(uri, "GET", null)
        return new TraineeDataHistory(response.details.items, response.details.total)
    } catch (error) {
        console.error("Error fetching set details:", error)
        throw error
    }
}

async function getTraineeReports(filters: Map<string, any> | null): Promise<Reports> {
    const uri = `${apiBaseUri}/api/trainee/reports`
    let postFiltersUri = uri

    if (filters != null) {
        postFiltersUri = handleFilters(uri, filters)
    }
    try {
        const response = await fetchData(postFiltersUri, "GET", null)
        return new Reports(response.details.items, response.details.total)
    } catch (error) {
        console.error("Error fetching set:", error)
        throw error
    }
}

async function getTraineeReportDetails(reportId: number): Promise<ReportDetails> {
    const uri = `${apiBaseUri}/api/trainee/report/${reportId}`
    try {
        const response = await fetchData(uri, "GET", null);
        return response.details
    } catch (error) {
        console.error("Error fetching set:", error)
        throw error
    }
}

async function getTTraineeDataDetails(dataId: string) {
    const uri = `${apiBaseUri}/api/trainee/data/${dataId}`

    try {
        const response = await fetchData(uri, "GET", null)
        return new TraineeDataDetails(response.details.date, response.details.bodyData)
    } catch (error) {
        console.error("Error fetching set details:", error)
        throw error
    }
}



export {
    getTraineeSessions,
    getTTraineeData,
    getTraineeReports,
    getTraineeReportDetails,
    getTTraineeDataDetails
}

