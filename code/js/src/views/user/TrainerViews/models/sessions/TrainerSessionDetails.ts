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
        source: string | null
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
    }
}
export default TrainerSessionDetails
