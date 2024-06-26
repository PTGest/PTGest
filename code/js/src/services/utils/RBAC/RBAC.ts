import store from "../../../store"
class RBAC {
    static getUserRole(): string {
        return store.getters.userData.role
    }
    static isCompany(): boolean {
        return this.getUserRole() === "COMPANY"
    }
    static isTrainee(): boolean {
        return this.getUserRole() === "TRAINEE"
    }
    static isTrainer(): boolean {
        return this.getUserRole() === "INDEPENDENT_TRAINER"
    }
    static isHiredTrainer(): boolean {
        return this.getUserRole() === "HIRED_TRAINER"
    }
}

export default RBAC
