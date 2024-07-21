class SetSessionFeedback {
    id: number
    setOrderId: number
    setId: number
    source: string
    feedback: string
    date: Date

    constructor(id: number, setOrderId: number, setId: number, source: string, feedback: string, date: Date) {
        this.id = id
        this.setOrderId = setOrderId
        this.setId = setId
        this.source = source
        this.feedback = feedback
        this.date = date
    }
}
export default SetSessionFeedback
