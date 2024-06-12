import Trainee from "../../../CompaniesViews/models/Trainee.ts";

class TrainerTrainees {

    trainees : Trainee[]
    nofTrainees : number
    constructor(trainees: Trainee[], nofTrainees: number){
        this.trainees = trainees;
        this.nofTrainees = nofTrainees;
    }

}
export default TrainerTrainees;
