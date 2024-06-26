class CreateReportRequest{
    traineeId: string
    report: string
    visibility: boolean

    constructor(traineeId: string, report: string, visibility: boolean){
        this.traineeId = traineeId
        this.report = report
        this.visibility = visibility
    }
}
export default CreateReportRequest
