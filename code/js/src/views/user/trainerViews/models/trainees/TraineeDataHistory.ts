import TraineeData from "./TraineeData.ts"

class TraineeDataHistory {
    traineeData: TraineeData[]
    totalData: number
    constructor(traineeData: TraineeData[], totalData: number) {
        this.traineeData = traineeData
        this.totalData = totalData
    }
}
export default TraineeDataHistory
