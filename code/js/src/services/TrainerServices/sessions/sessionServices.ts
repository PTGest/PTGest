import fetchData from "../../utils/fetchUtils/fetchData.ts";
import CancelSessionRequest from "../../../views/user/TrainerViews/models/sessions/CancelSessionRequest.ts";
import router from "../../../plugins/router.ts";
import CreateSessionRequest from "../../../views/user/TrainerViews/models/sessions/CreateSessionRequest.ts";
import TrainerSessionDetails from "../../../views/user/TrainerViews/models/sessions/TrainerSessionDetails.ts";
import Sessions from "../../../views/user/TrainerViews/models/sessions/Sessions.ts";
import handleFilters from "../../utils/fetchUtils/handleFilters.ts";
import TrainerSessions from "../../../views/user/TrainerViews/models/sessions/TrainerSessions.ts";
import {apiBaseUri} from "../../utils/envUtils.ts";
import CreateFeedbackRequest from "../../../views/user/TrainerViews/models/sessions/CreateFeedbackRequest.ts";
import SetSessionFeedback from "../../../views/user/TrainerViews/models/sessions/SetSessionFeedbacks.ts";


async function cancelSession(sessionId: number, reason: CancelSessionRequest): Promise<void> {
    const uri = `${apiBaseUri}/api/trainer/session/${sessionId}/cancel`
    try {
        const response = await fetchData(uri, "POST", reason)
        router.back()
        return response;

    } catch (error) {
        console.error("Error fetching set:", error)
        throw error
    }
}
async function createSession(sessionData: CreateSessionRequest): Promise<void> {
    const uri = `${apiBaseUri}/api/trainer/session`
    try {
        return await fetchData(uri, "POST", sessionData)
    } catch (error) {
        console.error("Error fetching set:", error)
        throw error
    }
}
async function editSession(sessionId: number, sessionData: CreateSessionRequest): Promise<void> {
    const uri = `${apiBaseUri}/api/trainer/session/${sessionId}/edit`
    try {
        const response = await fetchData(uri, "PUT", sessionData)
        return response.details
    } catch (error) {
        console.error("Error fetching set:", error)
        throw error
    }
}
async function getSessionDetails(sessionId: number): Promise<TrainerSessionDetails> {
    const uri = `${apiBaseUri}/api/trainer/session/${sessionId}`
    try {
        const response = await fetchData(uri, "GET", null)
        return response.details
    } catch (error) {
        console.error("Error fetching set:", error)
        throw error
    }
}
async function getTraineeSessions(traineeId: number, filters: Map<string, any> | null): Promise<Sessions> {
    const uri = `${apiBaseUri}/api/trainer/trainee/${traineeId}/sessions`
    let postFiltersUri = uri
    console.log("filters", filters)
    if (filters != null) {
        postFiltersUri = handleFilters(uri, filters)
    }
    try {
        const response = await fetchData(postFiltersUri, "GET", null)
        return new Sessions(response.details.items, response.details.total)
    } catch (error) {
        console.error("Error fetching set:", error)
        throw error
    }
}
async function getTrainerSessions(filters: Map<string, any> | null): Promise<TrainerSessions> {
    const uri = `${apiBaseUri}/api/trainer/sessions`
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

async function addTrainerSessionsFeedback(feedback: string, sessionId: string): Promise<void> {
    const uri = `${apiBaseUri}/api/trainer/session/${sessionId}/feedback`

    try {
        await fetchData(uri, "POST", new CreateFeedbackRequest(feedback))
        return
    } catch (error) {
        console.error("Error fetching set:", error)
        throw error
    }
}

async function getTrainerSetFeedback(sessionId: string): Promise<SetSessionFeedback[]> {
    const uri = `${apiBaseUri}/api/trainer/session/${sessionId}/sets/feedback`
    try {
        const response = await fetchData(uri, "GET", null)
        return response.details.feedbacks
    } catch (error) {
        console.error("Error fetching set:", error)
        throw error
    }
}





export {
    cancelSession,
    createSession,
    editSession,
    getSessionDetails,
    getTraineeSessions,
    getTrainerSessions,
    addTrainerSessionsFeedback,
    getTrainerSetFeedback
}
