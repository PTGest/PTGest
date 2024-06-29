class Report{
    id: number
    trainee: string
    date: string
    visibility: boolean

    constructor(id: number, trainee: string, date: string, visibility: boolean) {
        this.id = id
        this.trainee = trainee
        this.date = date
        this.visibility = visibility
    }
}
export default Report

