import Report from "./Report"

class Reports {
    reports: Report[]
    total: number
    constructor(reports: Report[], total: number) {
        this.reports = reports
        this.total = total
    }
}
export default Reports
