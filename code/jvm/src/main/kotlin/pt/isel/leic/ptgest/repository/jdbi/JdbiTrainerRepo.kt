package pt.isel.leic.ptgest.repository.jdbi

import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.kotlin.mapTo
import pt.isel.leic.ptgest.domain.session.SessionType
import pt.isel.leic.ptgest.domain.report.model.Report
import pt.isel.leic.ptgest.domain.report.model.ReportDetails
import pt.isel.leic.ptgest.domain.workout.Modality
import pt.isel.leic.ptgest.domain.workout.MuscleGroup
import pt.isel.leic.ptgest.domain.workout.SetType
import pt.isel.leic.ptgest.domain.workout.model.Exercise
import pt.isel.leic.ptgest.domain.workout.model.ExerciseDetails
import pt.isel.leic.ptgest.domain.workout.model.Set
import pt.isel.leic.ptgest.domain.workout.model.SetExerciseDetails
import pt.isel.leic.ptgest.domain.workout.model.Workout
import pt.isel.leic.ptgest.repository.TrainerRepo
import java.util.Date
import java.util.UUID

class JdbiTrainerRepo(private val handle: Handle) : TrainerRepo {
    override fun addTraineeData(traineeId: UUID, date: Date, bodyData: String): Int =
        handle.createUpdate(
            """
            insert into trainee_data (trainee_id, date, body_data)
            values (:traineeId, :date, :bodyData)
            """
        )
            .bindMap(
                mapOf(
                    "traineeId" to traineeId,
                    "date" to date,
                    "bodyData" to bodyData
                )
            )
            .executeAndReturnGeneratedKeys("id")
            .mapTo<Int>()
            .one()

    override fun getCompanyAssignedTrainer(trainerId: UUID): UUID? =
        handle.createQuery(
            """
            select company_id
            from company_trainer
            where trainer_id = :trainerId
            """.trimIndent()
        )
            .bind("trainerId", trainerId)
            .mapTo<UUID>()
            .firstOrNull()

    override fun createReport(traineeId: UUID, date: Date, report: String, visibility: Boolean): Int =
        handle.createUpdate(
            """
            insert into report (trainee_id, date, report, visibility)
            values (:traineeId, :date, :report, :visibility)
            """
        )
            .bindMap(
                mapOf(
                    "traineeId" to traineeId,
                    "date" to date,
                    "report" to report,
                    "visibility" to visibility
                )
            )
            .executeAndReturnGeneratedKeys("id")
            .mapTo<Int>()
            .one()

    override fun associateTrainerToReport(trainerId: UUID, reportId: Int) {
        handle.createUpdate(
            """
            insert into report_trainer (report_id, trainer_id)
            values (:reportId, :trainerId)
            """
        )
            .bindMap(
                mapOf(
                    "reportId" to reportId,
                    "trainerId" to trainerId
                )
            )
            .execute()
    }

    override fun getTraineeIdByName(name: String): UUID? {
        return handle.createQuery(
            """
            select id
            from "user" join trainee on id = trainee_id
            where name = :name
            """.trimIndent()
        )
            .bind("name", name)
            .mapTo<UUID>()
            .firstOrNull()
    }

    override fun getReports(trainerId: UUID, skip: Int, limit: Int?, traineeId: UUID?): List<Report> {
        val traineeCondition = if (traineeId != null) "and trainee_id = :traineeId" else ""

        return handle.createQuery(
            """
            select id, date, report, visibility
            from report_trainer rt join report r on rt.report_id = r.id
            where trainer_id = :trainerId $traineeCondition
            limit :limit offset :skip
            """.trimIndent()
        )
            .bindMap(
                mapOf(
                    "trainerId" to trainerId,
                    "skip" to skip,
                    "limit" to limit,
                    "traineeId" to traineeId
                )
            )
            .mapTo<Report>()
            .list()
    }

    override fun getTotalReports(trainerId: UUID, traineeId: UUID?): Int {
        val traineeCondition = if (traineeId != null) "and trainee_id = :traineeId" else ""

        return handle.createQuery(
            """
            select count(*)
            from report_trainer rt join report r on rt.report_id = r.id
            where trainer_id = :trainerId $traineeCondition
            """.trimIndent()
        )
            .bindMap(
                mapOf(
                    "trainerId" to trainerId,
                    "traineeId" to traineeId
                )
            )
            .mapTo<Int>()
            .one()
    }

    override fun getReportDetails(trainerId: UUID, reportId: Int): ReportDetails? =
        handle.createQuery(
            """
            select u.name as trainee, r.date, r.report, r.visibility
            from report_trainer rt 
            join report r on rt.report_id = r.id
            join "user" u on r.trainee_id = u.id
            where trainer_id = :trainerId and report_id = :reportId
            """.trimIndent()
        )
            .bindMap(
                mapOf(
                    "trainerId" to trainerId,
                    "reportId" to reportId
                )
            )
            .mapTo<ReportDetails>()
            .firstOrNull()

    override fun editReport(reportId: Int, date: Date, report: String, visibility: Boolean) {
        handle.createUpdate(
            """
            update report
            set date = :date, report = :report, visibility = :visibility
            where id = :reportId
            """
        )
            .bindMap(
                mapOf(
                    "date" to date,
                    "reportId" to reportId,
                    "report" to report,
                    "visibility" to visibility
                )
            )
            .execute()
    }

    override fun deleteReport(reportId: Int) {
        handle.createUpdate(
            """
            delete from report
            where id = :reportId
            """
        )
            .bind("reportId", reportId)
            .execute()
    }

    //  Exercise related methods
    override fun getExercises(
        trainerId: UUID,
        skip: Int,
        limit: Int?,
        name: String?,
        muscleGroup: MuscleGroup?,
        modality: Modality?
    ): List<Exercise> {
        val nameCondition = if (name != null) "and name like :name" else ""
        val muscleGroupCondition = if (muscleGroup != null) "and :muscleGroup = any(muscle_group)" else ""
        val modalityCondition = if (modality != null) "and type = :modality" else ""

        return handle.createQuery(
            """
            select id, name, muscle_group, modality
            from exercise_trainer ec join exercise e on ec.exercise_id = e.id
            where trainer_id = :trainerId $nameCondition $muscleGroupCondition $modalityCondition
            limit :limit offset :skip
            """.trimIndent()
        )
            .bindMap(
                mapOf(
                    "trainerId" to trainerId,
                    "skip" to skip,
                    "limit" to limit,
                    "name" to "%$name%",
                    "muscleGroup" to muscleGroup?.name,
                    "modality" to modality?.name
                )
            )
            .mapTo<Exercise>()
            .list()
    }

    override fun getTotalExercises(
        trainerId: UUID,
        name: String?,
        muscleGroup: MuscleGroup?,
        modality: Modality?
    ): Int {
        val nameCondition = if (name != null) "and name like :name" else ""
        val muscleGroupCondition = if (muscleGroup != null) "and :muscleGroup = any(muscle_group)" else ""
        val modalityCondition = if (modality != null) "and type = :modality" else ""

        return handle.createQuery(
            """
            select count(*)
            from exercise_trainer ec join exercise e on ec.exercise_id = e.id
            where trainer_id = :trainerId $nameCondition $muscleGroupCondition $modalityCondition
            """.trimIndent()
        )
            .bindMap(
                mapOf(
                    "trainerId" to trainerId,
                    "name" to "%$name%",
                    "muscleGroup" to muscleGroup?.name,
                    "modality" to modality?.name
                )
            )
            .mapTo<Int>()
            .one()
    }

    override fun getFavoriteExercises(
        trainerId: UUID,
        skip: Int,
        limit: Int?,
        name: String?,
        muscleGroup: MuscleGroup?,
        modality: Modality?
    ): List<Exercise> {
        val nameCondition = if (name != null) "and name like :name" else ""
        val muscleGroupCondition = if (muscleGroup != null) "and :muscleGroup = any(muscle_group)" else ""
        val modalityCondition = if (modality != null) "and type = :modality" else ""

        return handle.createQuery(
            """
            select e.id, e.name, e.muscle_group, e.modality
            from exercise e join trainer_favorite_exercise tfe on e.id = tfe.exercise_id
            where trainer_id = :trainerId $nameCondition $muscleGroupCondition $modalityCondition
            limit :limit offset :skip
            """.trimIndent()
        )
            .bindMap(
                mapOf(
                    "trainerId" to trainerId,
                    "skip" to skip,
                    "limit" to limit,
                    "name" to "%$name%",
                    "muscleGroup" to muscleGroup?.name,
                    "modality" to modality?.name
                )
            )
            .mapTo<Exercise>()
            .list()
    }

    override fun getTotalFavoriteExercises(
        trainerId: UUID,
        name: String?,
        muscleGroup: MuscleGroup?,
        modality: Modality?
    ): Int {
        val nameCondition = if (name != null) "and name like :name" else ""
        val muscleGroupCondition = if (muscleGroup != null) "and :muscleGroup = any(muscle_group)" else ""
        val modalityCondition = if (modality != null) "and type = :modality" else ""

        return handle.createQuery(
            """
            select count(*)
            from exercise e join trainer_favorite_exercise tfe on e.id = tfe.exercise_id
            where trainer_id = :trainerId $nameCondition $muscleGroupCondition $modalityCondition
            """.trimIndent()
        )
            .bindMap(
                mapOf(
                    "trainerId" to trainerId,
                    "name" to "%$name%",
                    "muscleGroup" to muscleGroup?.name,
                    "modality" to modality?.name
                )
            )
            .mapTo<Int>()
            .one()
    }

    override fun getExerciseDetails(trainerId: UUID, exerciseId: Int): ExerciseDetails? =
        handle.createQuery(
            """
            select id, name, description, muscle_group, modality, ref
            from exercise e join exercise_trainer et on e.id = et.exercise_id
            where id = :id and trainer_id = :trainer_id
            """.trimIndent()
        )
            .bindMap(
                mapOf(
                    "id" to exerciseId,
                    "trainer_id" to trainerId
                )
            )
            .mapTo<ExerciseDetails>()
            .firstOrNull()

    override fun associateTrainerToExercise(trainerId: UUID, exerciseId: Int) {
        handle.createUpdate(
            """
            insert into exercise_trainer (trainer_id, exercise_id)
            values (:trainerId, :exerciseId)
            """
        )
            .bindMap(
                mapOf(
                    "trainerId" to trainerId,
                    "exerciseId" to exerciseId
                )
            )
            .execute()
    }

    override fun getFavoriteExercisesByTrainerId(trainerId: UUID): List<Int> {
        return handle.createQuery(
            """
            select exercise_id
            from trainer_favorite_exercise
            where trainer_id = :trainerId
            """.trimIndent()
        )
            .bind("trainerId", trainerId)
            .mapTo<Int>()
            .list()
    }

    override fun favoriteExercise(trainerId: UUID, exerciseId: Int) {
        handle.createUpdate(
            """
            insert into trainer_favorite_exercise (trainer_id, exercise_id)
            values (:trainerId, :exerciseId)
            """
        )
            .bindMap(
                mapOf(
                    "trainerId" to trainerId,
                    "exerciseId" to exerciseId
                )
            )
            .execute()
    }

    override fun unfavoriteExercise(trainerId: UUID, exerciseId: Int) {
        handle.createUpdate(
            """
            delete from trainer_favorite_exercise
            where trainer_id = :trainerId and exercise_id = :exerciseId
            """
        )
            .bindMap(
                mapOf(
                    "trainerId" to trainerId,
                    "exerciseId" to exerciseId
                )
            )
            .execute()
    }

//  Set related methods

    override fun getLastSetNameId(trainerId: UUID): Int =
        handle.createQuery(
            """
            select cast(substring(name FROM '#([0-9]+)$') as int) as set_number
            from set
            where name like 'Set #%' and trainer_id = :trainerId
            order by cast(substring(name FROM '#([0-9]+)$') as int) desc
            limit 1
            """
        )
            .bind("trainerId", trainerId)
            .mapTo<Int>()
            .one()

    override fun getSets(trainerId: UUID, skip: Int, limit: Int?, type: SetType?, name: String?): List<Set> {
        val typeCondition = if (type != null) "and type = :type" else ""
        val nameCondition = if (name != null) "and name like :name" else ""

        return handle.createQuery(
            """
            select id, name, notes, type
            from set
            where trainer_id = :trainerId $typeCondition $nameCondition
            limit :limit offset :skip
            """.trimIndent()
        )
            .bindMap(
                mapOf(
                    "trainerId" to trainerId,
                    "skip" to skip,
                    "limit" to limit,
                    "type" to type?.name,
                    "name" to "%$name%"
                )
            )
            .mapTo<Set>()
            .list()
    }

    override fun getTotalSets(trainerId: UUID, type: SetType?, name: String?): Int {
        val typeCondition = if (type != null) "and type = :type" else ""
        val nameCondition = if (name != null) "and name like :name" else ""

        return handle.createQuery(
            """
            select count(*)
            from set
            where trainer_id = :trainerId $typeCondition $nameCondition
            """.trimIndent()
        )
            .bindMap(
                mapOf(
                    "trainerId" to trainerId,
                    "type" to type?.name,
                    "name" to "%$name%"
                )
            )
            .mapTo<Int>()
            .one()
    }

    override fun getFavoriteSets(trainerId: UUID, skip: Int, limit: Int?, type: SetType?, name: String?): List<Set> {
        val typeCondition = if (type != null) "and type = :type" else ""
        val nameCondition = if (name != null) "and name like :name" else ""

        return handle.createQuery(
            """
            select s.id, s.name, s.notes, s.type
            from set s join trainer_favorite_set tfs on s.id = tfs.set_id
            where trainer_id = :trainerId $typeCondition $nameCondition
            limit :limit offset :skip
            """.trimIndent()
        )
            .bindMap(
                mapOf(
                    "trainerId" to trainerId,
                    "skip" to skip,
                    "limit" to limit,
                    "type" to type?.name,
                    "name" to "%$name%"
                )
            )
            .mapTo<Set>()
            .list()
    }

    override fun getTotalFavoriteSets(trainerId: UUID, type: SetType?, name: String?): Int {
        val typeCondition = if (type != null) "and type = :type" else ""
        val nameCondition = if (name != null) "and name like :name" else ""

        return handle.createQuery(
            """
            select count(*)
            from set s join trainer_favorite_set tfs on s.id = tfs.set_id
            where trainer_id = :trainerId $typeCondition $nameCondition
            """.trimIndent()
        )
            .bindMap(
                mapOf(
                    "trainerId" to trainerId,
                    "type" to type?.name,
                    "name" to "%$name%"
                )
            )
            .mapTo<Int>()
            .one()
    }

    override fun getSet(trainerId: UUID, setId: Int): Set? =
        handle.createQuery(
            """
            select id, name, notes, type
            from set
            where id = :setId and trainer_id = :trainerId
            """.trimIndent()
        )
            .bindMap(
                mapOf(
                    "setId" to setId,
                    "trainerId" to trainerId
                )
            )
            .mapTo<Set>()
            .firstOrNull()

    override fun getSetExercises(setId: Int): List<SetExerciseDetails> =
        handle.createQuery(
            """
            select e.id, e.name, e.muscle_group, e.modality, se.details
            from exercise e join set_exercise se on e.id = se.exercise_id 
            where set_id = :setId
            order by se.order_id
            """.trimIndent()
        )
            .bind("setId", setId)
            .mapTo<SetExerciseDetails>()
            .list()

    override fun getFavoriteSetsByTrainerId(trainerId: UUID): List<Int> {
        return handle.createQuery(
            """
            select set_id
            from trainer_favorite_set
            where trainer_id = :trainerId
            """.trimIndent()
        )
            .bind("trainerId", trainerId)
            .mapTo<Int>()
            .list()
    }

    override fun favoriteSet(trainerId: UUID, setId: Int) {
        handle.createUpdate(
            """
            insert into trainer_favorite_set (trainer_id, set_id)
            values (:trainerId, :setId)
            """
        )
            .bindMap(
                mapOf(
                    "trainerId" to trainerId,
                    "setId" to setId
                )
            )
            .execute()
    }

    override fun unfavoriteSet(trainerId: UUID, setId: Int) {
        handle.createUpdate(
            """
            delete from trainer_favorite_set
            where trainer_id = :trainerId and set_id = :setId
            """
        )
            .bindMap(
                mapOf(
                    "trainerId" to trainerId,
                    "setId" to setId
                )
            )
            .execute()
    }

//  Workout related methods
    override fun getLastWorkoutNameId(trainerId: UUID): Int =
        handle.createQuery(
            """
            select cast(substring(name FROM '#([0-9]+)${'$'}') as int) as workout_number
            from workout
            where name like 'Workout #%' and trainer_id = :trainerId
            order by cast(substring(name FROM '#([0-9]+)${'$'}') as int) desc
            limit 1
            """
        )
            .bind("trainerId", trainerId)
            .mapTo<Int>()
            .one()

    override fun getWorkouts(
        trainerId: UUID,
        skip: Int,
        limit: Int?,
        name: String?,
        muscleGroup: MuscleGroup?
    ): List<Workout> {
        val nameCondition = if (name != null) "and name like :name" else ""
        val muscleGroupCondition = if (muscleGroup != null) "and :muscleGroup = any(muscle_group)" else ""

        return handle.createQuery(
            """
            select id, name, description, muscle_group
            from workout
            where trainer_id = :trainerId $nameCondition $muscleGroupCondition
            limit :limit offset :skip
            """.trimIndent()
        )
            .bindMap(
                mapOf(
                    "trainerId" to trainerId,
                    "skip" to skip,
                    "limit" to limit,
                    "name" to "%$name%",
                    "muscleGroup" to muscleGroup?.name
                )
            )
            .mapTo<Workout>()
            .list()
    }

    override fun getTotalWorkouts(trainerId: UUID, name: String?, muscleGroup: MuscleGroup?): Int {
        val nameCondition = if (name != null) "and name like :name" else ""
        val muscleGroupCondition = if (muscleGroup != null) "and :muscleGroup = any(muscle_group)" else ""

        return handle.createQuery(
            """
            select count(*)
            from workout
            where trainer_id = :trainerId $nameCondition $muscleGroupCondition
            """.trimIndent()
        )
            .bindMap(
                mapOf(
                    "trainerId" to trainerId,
                    "name" to "%$name%",
                    "muscleGroup" to muscleGroup?.name
                )
            )
            .mapTo<Int>()
            .one()
    }

    override fun getFavoriteWorkouts(
        trainerId: UUID,
        skip: Int,
        limit: Int?,
        name: String?,
        muscleGroup: MuscleGroup?
    ): List<Workout> {
        val nameCondition = if (name != null) "and name like :name" else ""
        val muscleGroupCondition = if (muscleGroup != null) "and :muscleGroup = any(muscle_group)" else ""

        return handle.createQuery(
            """
            select w.id, w.name, w.description, w.muscle_group
            from workout w join trainer_favorite_workout tfw on w.id = tfw.workout_id
            where trainer_id = :trainerId $nameCondition $muscleGroupCondition
            limit :limit offset :skip
            """.trimIndent()
        )
            .bindMap(
                mapOf(
                    "trainerId" to trainerId,
                    "skip" to skip,
                    "limit" to limit,
                    "name" to "%$name%",
                    "muscleGroup" to muscleGroup?.name
                )
            )
            .mapTo<Workout>()
            .list()
    }

    override fun getTotalFavoriteWorkouts(trainerId: UUID, name: String?, muscleGroup: MuscleGroup?): Int {
        val nameCondition = if (name != null) "and name like :name" else ""
        val muscleGroupCondition = if (muscleGroup != null) "and :muscleGroup = any(muscle_group)" else ""

        return handle.createQuery(
            """
            select count(*)
            from workout w join trainer_favorite_workout tfw on w.id = tfw.workout_id
            where trainer_id = :trainerId $nameCondition $muscleGroupCondition
            """.trimIndent()
        )
            .bindMap(
                mapOf(
                    "trainerId" to trainerId,
                    "name" to "%$name%",
                    "muscleGroup" to muscleGroup?.name
                )
            )
            .mapTo<Int>()
            .one()
    }

    override fun getWorkoutDetails(trainerId: UUID, workoutId: Int): Workout? =
        handle.createQuery(
            """
            select id, name, description, muscle_group
            from workout
            where id = :workoutId and trainer_id = :trainerId
            """.trimIndent()
        )
            .bindMap(
                mapOf(
                    "workoutId" to workoutId,
                    "trainerId" to trainerId
                )
            )
            .mapTo<Workout>()
            .firstOrNull()

    override fun getWorkoutSetIds(workoutId: Int): List<Int> =
        handle.createQuery(
            """
            select set_id
            from workout_set
            where workout_id = :workoutId
            order by order_id desc
            """.trimIndent()
        )
            .bind("workoutId", workoutId)
            .mapTo<Int>()
            .list()

    override fun getFavoriteWorkoutsByTrainerId(trainerId: UUID): List<Int> {
        return handle.createQuery(
            """
            select workout_id
            from trainer_favorite_workout
            where trainer_id = :trainerId
            """.trimIndent()
        )
            .bind("trainerId", trainerId)
            .mapTo<Int>()
            .list()
    }

    override fun favoriteWorkout(trainerId: UUID, workoutId: Int) {
        handle.createUpdate(
            """
            insert into trainer_favorite_workout (trainer_id, workout_id)
            values (:trainerId, :workoutId)
            """
        )
            .bindMap(
                mapOf(
                    "trainerId" to trainerId,
                    "workoutId" to workoutId
                )
            )
            .execute()
    }

    override fun unfavoriteWorkout(trainerId: UUID, workoutId: Int) {
        handle.createUpdate(
            """
            delete from trainer_favorite_workout
            where trainer_id = :trainerId and workout_id = :workoutId
            """
        )
            .bindMap(
                mapOf(
                    "trainerId" to trainerId,
                    "workoutId" to workoutId
                )
            )
            .execute()
    }

//  Session related methods

    override fun createSession(
        trainerId: UUID,
        traineeId: UUID,
        workoutId: Int,
        beginDate: Date,
        endDate: Date?,
        type: SessionType,
        notes: String?
    ): Int =
        handle.createUpdate(
            """
            insert into session (trainee_id, trainer_id, workout_id, begin_date, end_date, type, notes)
            values (:traineeId, :trainerId, :workoutId, :beginDate, :endDate, :type, :notes)
            returning id
            """
        )
            .bindMap(
                mapOf(
                    "traineeId" to traineeId,
                    "trainerId" to trainerId,
                    "workoutId" to workoutId,
                    "beginDate" to beginDate,
                    "endDate" to endDate,
                    "type" to type.name,
                    "notes" to notes
                )
            )
            .executeAndReturnGeneratedKeys("id")
            .mapTo<Int>()
            .one()
}
