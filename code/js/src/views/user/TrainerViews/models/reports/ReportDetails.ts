class ReportDetails {
    trainee: string
    date: string
    report: string
    visibility: boolean
    constructor(trainee: string, date: string, report: string, visibility: boolean) {
        this.trainee = trainee
        this.date = date
        this.report = report
        this.visibility = visibility
    }
}
export default ReportDetails;
