class CreateSessionRequest {
    traineeId: string
    workoutId: number
    beginDate: string
    endDate: string | null
    location: string | null
    type: string
    notes: string | null

    constructor(traineeId: string, workoutId: number, beginDate: string, endDate: string | null, location: string | null, type: string, notes: string | null) {
        this.traineeId = traineeId
        this.workoutId = workoutId
        this.beginDate = beginDate
        this.endDate = endDate
        this.location = location
        this.type = type
        this.notes = notes
    }
}
export default CreateSessionRequest
