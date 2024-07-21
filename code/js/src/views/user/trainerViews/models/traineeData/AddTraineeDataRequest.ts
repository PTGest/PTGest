import BodyCircumferences from "./BodyCircumferences.ts"

class AddTraineeDataRequest {
    traineeId: string
    gender: string
    weight: number
    height: number
    bodyCircumferences: BodyCircumferences
    bodyFatPercentage: number | null
    skinFold: Map<string, number> | null

    constructor(traineeId: string, gender: string, weight: number, height: number, bodyCircumferences: BodyCircumferences, bodyFatPercentage: number | null, skinFold: Map<string, number> | null) {
        this.traineeId = traineeId
        this.gender = gender
        this.weight = weight
        this.height = height
        this.bodyCircumferences = bodyCircumferences
        this.bodyFatPercentage = bodyFatPercentage
        this.skinFold = skinFold
    }
}
export default AddTraineeDataRequest
