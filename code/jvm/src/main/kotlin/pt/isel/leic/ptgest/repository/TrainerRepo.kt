package pt.isel.leic.ptgest.repository

import pt.isel.leic.ptgest.domain.common.model.SessionType
import pt.isel.leic.ptgest.domain.workout.Modality
import pt.isel.leic.ptgest.domain.workout.MuscleGroup
import pt.isel.leic.ptgest.domain.workout.SetType
import pt.isel.leic.ptgest.domain.workout.model.Exercise
import pt.isel.leic.ptgest.domain.workout.model.ExerciseDetails
import pt.isel.leic.ptgest.domain.workout.model.Set
import pt.isel.leic.ptgest.domain.workout.model.SetExerciseDetails
import pt.isel.leic.ptgest.domain.workout.model.Workout
import java.util.*

interface TrainerRepo {

    fun getCompanyAssignedTrainer(trainerId: UUID): UUID?

    fun getExercises(
        trainerId: UUID,
        skip: Int,
        limit: Int?,
        name: String?,
        muscleGroup: MuscleGroup?,
        modality: Modality?
    ): List<Exercise>

    fun getTotalExercises(
        trainerId: UUID,
        name: String?,
        muscleGroup: MuscleGroup?,
        modality: Modality?
    ): Int

    fun getExerciseDetails(trainerId: UUID, exerciseId: Int): ExerciseDetails?

    fun associateTrainerToExercise(trainerId: UUID, exerciseId: Int)

    fun getLastSetNameId(trainerId: UUID): Int

    fun getLastWorkoutNameId(trainerId: UUID): Int

    fun getSets(trainerId: UUID, skip: Int, limit: Int?, type: SetType?, name: String?): List<Set>

    fun getTotalSets(trainerId: UUID, type: SetType?, name: String?): Int

    fun getSet(trainerId: UUID, setId: Int): Set?

    fun getSet(setId: Int): Set

    fun getSetExercises(setId: Int): List<SetExerciseDetails>

    fun getWorkouts(trainerId: UUID, skip: Int, limit: Int?, name: String?, muscleGroup: MuscleGroup?): List<Workout>

    fun getTotalWorkouts(trainerId: UUID, name: String?, muscleGroup: MuscleGroup?): Int

    fun getWorkoutDetails(trainerId: UUID, workoutId: Int): Workout?

    fun getWorkoutSetIds(workoutId: Int): List<Int>

    fun createSession(
        trainerId: UUID,
        traineeId: UUID,
        workoutId: Int,
        beginDate: Date,
        endDate: Date?,
        type: SessionType,
        notes: String?
    ): Int
}
