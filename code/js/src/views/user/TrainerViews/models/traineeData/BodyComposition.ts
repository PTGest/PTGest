class BodyComposition {
    bmi: number
    bodyFatPercentage: number | null = null
    bodyFatMass: number | null = null
    fatFreeMass: number | null = null

    constructor(bmi: number, bodyFatPercentage: number | null, bodyFatMass: number | null, fatFreeMass: number | null) {
        this.bmi = bmi
        this.bodyFatPercentage = bodyFatPercentage
        this.bodyFatMass = bodyFatMass
        this.fatFreeMass = fatFreeMass
    }
}
export default BodyComposition
