class Session {
    id: number
    beginDate: string | null
    endDate: string | null
    type: string
    cancelled: boolean = false

    constructor(id: number, beginDate: string | null, endDate: string | null, type: string, cancelled: boolean) {
        this.id = id
        this.beginDate = beginDate
        this.endDate = endDate
        this.type = type
        this.cancelled = cancelled
    }
}
export default Session
