import Trainee from "./Trainee.ts";

class CompanyTrainees{
    trainees: Trainee[]
    total: number

    constructor(trainees: Trainee[], total: number){
        this.trainees = trainees;
        this.total = total;
    }
}

export default CompanyTrainees;
