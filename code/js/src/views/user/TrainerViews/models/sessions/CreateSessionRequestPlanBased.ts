class CreateSessionRequestPlanBased {
    traineeId: string
    workoutId: number
    beginDate: string
    notes: string | null
    type: string

    constructor(traineeId: string, workoutId: number, beginDate: string, notes: string | null, type: string) {
        this.traineeId = traineeId
        this.workoutId = workoutId
        this.beginDate = beginDate
        this.notes = notes
        this.type = type
    }
}
export default CreateSessionRequestPlanBased
