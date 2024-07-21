class EditReportRequest {
    traineeId: string
    report: string
    visibility: string

    constructor(traineeId: string, report: string, visibility: string) {
        this.traineeId = traineeId
        this.report = report
        this.visibility = visibility
    }
}
export default EditReportRequest
