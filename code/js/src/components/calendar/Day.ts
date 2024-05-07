class Day {
    day: string
    class: string
    is_Workout_Day: boolean = false

    constructor(day: string, className: string, is_Workout_Day: boolean = false) {
        this.day = day
        this.class = className
        this.is_Workout_Day = is_Workout_Day
    }
}

export default Day
