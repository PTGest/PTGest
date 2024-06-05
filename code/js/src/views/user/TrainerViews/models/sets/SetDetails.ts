import SetExerciseDetails from "./SetExerciseDetails.ts";

class SetDetails{
     name: string
     notes: string| null
     type: string
     setExerciseDetails: SetExerciseDetails[]

        constructor(name: string, notes: string, type: string, setExerciseDetails: SetExerciseDetails[]){
            this.name = name;
            this.notes = notes;
            this.type = type;
            this.setExerciseDetails = setExerciseDetails;
        }

}

export default SetDetails;
