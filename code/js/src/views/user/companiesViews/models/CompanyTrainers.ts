import Trainer from "./Trainer.ts";

class CompanyTrainers {
    trainers: Trainer[]
    total: number

    constructor(trainers: Trainer[], total: number) {
        this.trainers = trainers
        this.total = total
    }
}

export default CompanyTrainers
