import TrainerSession from "./TrainerSession.ts"

class TrainerSessions {
    sessions: TrainerSession[]
    total: number

    constructor(sessions: TrainerSession[], total: number) {
        this.sessions = sessions
        this.total = total
    }
}
export default TrainerSessions
