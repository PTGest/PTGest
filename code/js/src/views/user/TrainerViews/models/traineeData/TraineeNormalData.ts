import BodyCircumferences from "./BodyCircumferences.ts"

class TraineeNormalData {
    name: string
    weight: number | null
    height: number | null
    gender: string
    bodyCircumferences: BodyCircumferences | null

    constructor(name: string, weight: number | null, height: number | null, gender: string, bodyCircumferences: BodyCircumferences | null) {
        this.name = name
        this.weight = weight
        this.height = height
        this.gender = gender
        this.bodyCircumferences = bodyCircumferences
    }
}
export default TraineeNormalData
