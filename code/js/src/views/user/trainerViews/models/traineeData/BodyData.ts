import BodyCircumferences from "./BodyCircumferences.ts"
import BodyComposition from "./BodyComposition.ts"

class BodyData {
    weight: number
    height: number
    bodyCircumferences: BodyCircumferences
    bodyComposition: BodyComposition
    skinFolds: Map<string, number> | null = null

    constructor(weight: number, height: number, bodyCircumferences: BodyCircumferences, bodyComposition: BodyComposition, skinFolds: Map<string, number> | null) {
        this.weight = weight
        this.height = height
        this.bodyCircumferences = bodyCircumferences
        this.bodyComposition = bodyComposition
        this.skinFolds = skinFolds
    }
}
export default BodyData
