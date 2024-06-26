class Set {
    id: number
    name: string
    notes: string
    type: string
    reps: string
    isFavorite: boolean

    constructor(id: number, name: string, notes: string, type: string, reps: string, isFavorite: boolean = false) {
        this.id = id
        this.name = name
        this.notes = notes
        this.type = type
        this.reps = reps
        this.isFavorite = isFavorite
    }
}

export default Set
