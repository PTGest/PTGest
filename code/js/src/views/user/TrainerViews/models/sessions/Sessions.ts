import Session from "./Session.ts"

class Sessions {
    sessions: Session[]
    total: number

    constructor(sessions: Session[], total: number) {
        this.sessions = sessions
        this.total = total
    }
}
export default Sessions
