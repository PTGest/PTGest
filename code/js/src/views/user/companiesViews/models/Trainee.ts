class Trainee {
    traineeId: string
    traineeName: string
    gender: string
    trainerId: string
    trainerName: string

    constructor(traineeId: string, traineeName: string, gender: string, trainerId: string, trainerName: string) {
        this.traineeId = traineeId
        this.traineeName = traineeName
        this.gender = gender
        this.trainerId = trainerId
        this.trainerName = trainerName
    }
}

export default Trainee
