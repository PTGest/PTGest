class TrainerSession {
    id: number
    traineeName: string
    beginDate: string
    endDate: string | null
    type: string
    cancelled: boolean = false

    constructor(id: number, traineeName: string, beginDate: string, endDate: string | null, type: string, cancelled: boolean) {
        this.id = id
        this.traineeName = traineeName
        this.beginDate = beginDate
        this.endDate = endDate
        this.type = type
        this.cancelled = cancelled
    }
}
export default TrainerSession
