import BodyData from "./BodyData.ts"

class TraineeDataDetails {
    date: Date
    bodyData: BodyData

    constructor(date: Date, bodyData: BodyData) {
        this.date = date
        this.bodyData = bodyData
    }
}
export default TraineeDataDetails
