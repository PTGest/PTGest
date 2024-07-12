import TrainerSessions from "../../views/user/TrainerViews/models/sessions/TrainerSessions.ts"
import { apiBaseUri } from "../utils/envUtils.ts"
import handleFilters from "../utils/fetchUtils/handleFilters.ts"
import fetchData from "../utils/fetchUtils/fetchData.ts"
import TraineeDataHistory from "../../views/user/TrainerViews/models/trainees/TraineeDataHistory.ts"
import Reports from "../../views/user/TrainerViews/models/reports/Reports.ts"
import ReportDetails from "../../views/user/TrainerViews/models/reports/ReportDetails.ts"
import TraineeDataDetails from "../../views/user/TrainerViews/models/traineeData/TraineeDataDetails.ts"
import CreateFeedbackRequest from "../../views/user/TrainerViews/models/sessions/CreateFeedbackRequest.ts"
import TrainerSessionDetails from "../../views/user/TrainerViews/models/sessions/TrainerSessionDetails.ts"
import CancelSessionRequest from "../../views/user/TrainerViews/models/sessions/CancelSessionRequest.ts"
import router from "../../plugins/router.ts"
import WorkoutDetails from "../../views/user/TrainerViews/models/workouts/WorkoutDetails.ts"
import SetSessionFeedback from "../../views/user/TrainerViews/models/sessions/SetSessionFeedbacks.ts";

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
        console.error("Error fetching trainee sessions:", error)
        throw error
    }
}

async function getTTraineeData() {
    const uri = `${apiBaseUri}/api/trainee/data`
    try {
        const response = await fetchData(uri, "GET", null)
        return new TraineeDataHistory(response.details.items, response.details.total)
    } catch (error) {
        console.error("Error fetching trainee body data:", error)
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
        console.error("Error fetching trainee reports:", error)
        throw error
    }
}

async function getTraineeReportDetails(reportId: number): Promise<ReportDetails> {
    const uri = `${apiBaseUri}/api/trainee/report/${reportId}`
    try {
        const response = await fetchData(uri, "GET", null)
        return response.details
    } catch (error) {
        console.error("Error fetching trainee report details:", error)
        throw error
    }
}

async function getTTraineeDataDetails(dataId: string) {
    const uri = `${apiBaseUri}/api/trainee/data/${dataId}`

    try {
        const response = await fetchData(uri, "GET", null)
        return new TraineeDataDetails(response.details.date, response.details.bodyData)
    } catch (error) {
        console.error("Error fetching trainee body data details:", error)
        throw error
    }
}

async function getTraineeSessionDetails(sessionId: number): Promise<TrainerSessionDetails> {
    const uri = `${apiBaseUri}/api/trainee/session/${sessionId}`
    try {
        const response = await fetchData(uri, "GET", null)
        return response.details
    } catch (error) {
        console.error("Error fetching trainee session details:", error)
        throw error
    }
}

async function cancelTraineeSession(sessionId: number, reason: CancelSessionRequest): Promise<void> {
    const uri = `${apiBaseUri}/api/trainee/session/${sessionId}/cancel`
    try {
        const response = await fetchData(uri, "POST", reason)
        router.back()
        return response
    } catch (error) {
        console.error("Error canceling session:", error)
        throw error
    }
}

async function addTraineeSessionsFeedback(feedback: string, sessionId: string): Promise<void> {
    const uri = `${apiBaseUri}/api/trainee/session/${sessionId}/feedback`

    try {
        await fetchData(uri, "POST", new CreateFeedbackRequest(feedback))
        return
    } catch (error) {
        console.error("Error creating session feedback:", error)
        throw error
    }
}

async function getTraineeWorkoutDetails(workoutId: string): Promise<WorkoutDetails> {
    const uri = `${apiBaseUri}/api/trainee/workout/` + workoutId

    try {
        const response = await fetchData(uri, "GET", null)
        return new WorkoutDetails(response.details.id, response.details.name, response.details.description, response.details.muscleGroup, response.details.sets)
    } catch (error) {
        console.error("Error fetching workout details:", error)
        throw error
    }
}

async function addTraineeSessionsSetFeedback(feedback: string, sessionId: number, setId: number, setOrderId: number): Promise<void> {
    const uri = `${apiBaseUri}/api/trainee/session/${sessionId}/set/${setOrderId}/${setId}/feedback/create`
    try {
        await fetchData(uri, "POST", new CreateFeedbackRequest(feedback))
        return
    } catch (error) {
        console.error("Error creating trainee session set feedback:", error)
        throw error
    }
}
async function getTraineeSetFeedback(sessionId: string): Promise<SetSessionFeedback[]> {
    const uri = `${apiBaseUri}/api/trainee/session/${sessionId}/sets/feedback`
    try {
        const response = await fetchData(uri, "GET", null)
        return response.details.feedbacks
    } catch (error) {
        console.error("Error fetching trainee session set feedback:", error)
        throw error
    }
}


async function editTraineeSetSessionsFeedback(feedback: string, sessionId: string, feedbackId: number, setId: number, setOrderId: number): Promise<void> {
    const uri = `${apiBaseUri}/api/trainee/session/${sessionId}/set/${setOrderId}/${setId}/edit-feedback/${feedbackId}`
    try {
        await fetchData(uri, "PUT", new CreateFeedbackRequest(feedback))
        return
    } catch (error) {
        console.error("Error editing trainee session set feedback:", error)
        throw error
    }
}




export {
    getTraineeSessions,
    getTTraineeData,
    getTraineeReports,
    getTraineeReportDetails,
    getTTraineeDataDetails,
    getTraineeSessionDetails,
    cancelTraineeSession,
    addTraineeSessionsFeedback,
    getTraineeWorkoutDetails,
    editTraineeSetSessionsFeedback,
    addTraineeSessionsSetFeedback,
    getTraineeSetFeedback
}
