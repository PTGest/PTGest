package pt.isel.leic.ptgest.repository

import pt.isel.leic.ptgest.domain.common.SessionType
import pt.isel.leic.ptgest.domain.trainer.model.Report
import pt.isel.leic.ptgest.domain.trainer.model.ReportDetails
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

    fun addTraineeData(traineeId: UUID, date: Date, bodyData: String): Int

    fun getCompanyAssignedTrainer(trainerId: UUID): UUID?

    fun createReport(
        traineeId: UUID,
        date: Date,
        report: String,
        visibility: Boolean
    ): Int

    fun associateTrainerToReport(trainerId: UUID, reportId: Int)

    fun getTraineeIdByName(name: String): UUID?

    fun getReports(trainerId: UUID, skip: Int, limit: Int?, traineeId: UUID?): List<Report>

    fun getTotalReports(trainerId: UUID, traineeId: UUID?): Int

    fun getReportDetails(trainerId: UUID, reportId: Int): ReportDetails?

    fun editReport(reportId: Int, date: Date, report: String, visibility: Boolean)

    fun deleteReport(reportId: Int)

//  Exercise related methods

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

    fun getFavoriteExercises(
        trainerId: UUID,
        skip: Int,
        limit: Int?,
        name: String?,
        muscleGroup: MuscleGroup?,
        modality: Modality?
    ): List<Exercise>

    fun getTotalFavoriteExercises(
        trainerId: UUID,
        name: String?,
        muscleGroup: MuscleGroup?,
        modality: Modality?
    ): Int

    fun getExerciseDetails(trainerId: UUID, exerciseId: Int): ExerciseDetails?

    fun associateTrainerToExercise(trainerId: UUID, exerciseId: Int)

    fun getFavoriteExercisesByTrainerId(trainerId: UUID): List<Int>

    fun favoriteExercise(trainerId: UUID, exerciseId: Int)

    fun unfavoriteExercise(trainerId: UUID, exerciseId: Int)

//  Set related methods

    fun getLastSetNameId(trainerId: UUID): Int

    fun getSets(trainerId: UUID, skip: Int, limit: Int?, type: SetType?, name: String?): List<Set>

    fun getTotalSets(trainerId: UUID, type: SetType?, name: String?): Int

    fun getFavoriteSets(trainerId: UUID, skip: Int, limit: Int?, type: SetType?, name: String?): List<Set>

    fun getTotalFavoriteSets(trainerId: UUID, type: SetType?, name: String?): Int

    fun getSet(trainerId: UUID, setId: Int): Set?

    fun getSetExercises(setId: Int): List<SetExerciseDetails>

    fun getFavoriteSetsByTrainerId(trainerId: UUID): List<Int>

    fun favoriteSet(trainerId: UUID, setId: Int)

    fun unfavoriteSet(trainerId: UUID, setId: Int)

//  Workout related methods

    fun getLastWorkoutNameId(trainerId: UUID): Int

    fun getWorkouts(trainerId: UUID, skip: Int, limit: Int?, name: String?, muscleGroup: MuscleGroup?): List<Workout>

    fun getTotalWorkouts(trainerId: UUID, name: String?, muscleGroup: MuscleGroup?): Int

    fun getFavoriteWorkouts(trainerId: UUID, skip: Int, limit: Int?, name: String?, muscleGroup: MuscleGroup?): List<Workout>

    fun getTotalFavoriteWorkouts(trainerId: UUID, name: String?, muscleGroup: MuscleGroup?): Int

    fun getWorkoutDetails(trainerId: UUID, workoutId: Int): Workout?

    fun getWorkoutSetIds(workoutId: Int): List<Int>

    fun getFavoriteWorkoutsByTrainerId(trainerId: UUID): List<Int>

    fun favoriteWorkout(trainerId: UUID, workoutId: Int)

    fun unfavoriteWorkout(trainerId: UUID, workoutId: Int)

//  Session related methods

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
