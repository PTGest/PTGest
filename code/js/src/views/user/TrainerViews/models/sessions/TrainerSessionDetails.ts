import SessionFeedback from "./SessionFeedback.ts";

class TrainerSessionDetails {
    id: number
    traineeName: string
    workoutId: number
    beginDate: string
    endDate: string | null
    location: string | null
    type: string
    notes: string | null
    cancelled: boolean = false
    reason: string | null
    source: string | null
    feedbacks: SessionFeedback[]

    constructor(
        id: number,
        traineeName: string,
        workoutId: number,
        beginDate: string,
        endDate: string | null,
        location: string | null,
        type: string,
        notes: string | null,
        cancelled: boolean,
        reason: string | null,
        source: string | null,
        feedbacks: SessionFeedback[]
    ) {
        this.id = id
        this.traineeName = traineeName
        this.workoutId = workoutId
        this.beginDate = beginDate
        this.endDate = endDate
        this.location = location
        this.type = type
        this.notes = notes
        this.cancelled = cancelled
        this.reason = reason
        this.source = source
        this.feedbacks = feedbacks
    }
}
export default TrainerSessionDetails
