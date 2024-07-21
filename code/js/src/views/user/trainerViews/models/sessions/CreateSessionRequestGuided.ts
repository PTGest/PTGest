class CreateSessionRequestGuided {
    traineeId: string
    workoutId: number
    beginDate: string
    endDate: string | null
    location: string | null
    notes: string | null
    type: string

    constructor(traineeId: string, workoutId: number, beginDate: string, endDate: string | null, location: string | null, notes: string | null, type: string) {
        this.traineeId = traineeId
        this.workoutId = workoutId
        this.beginDate = beginDate
        this.endDate = endDate
        this.location = location
        this.notes = notes
        this.type = type
    }
}
export default CreateSessionRequestGuided
