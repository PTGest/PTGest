class EditReportRequest {
    report: string
    visibility: string

    constructor(report: string, visibility: string) {
        this.report = report
        this.visibility = visibility
    }
}
export default EditReportRequest;
