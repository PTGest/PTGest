class SessionFeedback{
    id: number
    source: string
    feedback: string
    date: Date

    constructor(id: number, source: string, feedback: string, date: Date){
        this.id = id
        this.source = source
        this.feedback = feedback
        this.date = date
    }
}
export default SessionFeedback
