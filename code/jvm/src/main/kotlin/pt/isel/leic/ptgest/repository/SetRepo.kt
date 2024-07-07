package pt.isel.leic.ptgest.repository

import pt.isel.leic.ptgest.domain.set.model.Set
import pt.isel.leic.ptgest.domain.set.model.SetExerciseDetails
import pt.isel.leic.ptgest.domain.set.model.TrainerSet
import pt.isel.leic.ptgest.domain.workout.SetType
import pt.isel.leic.ptgest.domain.workout.model.WorkoutSet
import java.util.*

interface SetRepo {

    fun createSet(name: String, notes: String?, type: SetType): Int

    fun associateExerciseToSet(orderId: Int, exerciseId: Int, setId: Int, details: String)

    fun getLastSetNameId(trainerId: UUID): Int?

    fun getSets(
        trainerId: UUID,
        name: String?,
        type: SetType?,
        isFavorite: Boolean,
        skip: Int,
        limit: Int?
    ): List<TrainerSet>

    fun getTotalSets(trainerId: UUID, name: String?, type: SetType?, isFavorite: Boolean): Int

    fun isSetFavorite(trainerId: UUID, setId: Int): Boolean

    fun getSet(trainerId: UUID, setId: Int): Set?

    fun getSetDetails(setId: Int): Set?

    fun getWorkoutSet(workoutId: Int, setId: Int): WorkoutSet?

    fun getSetExercises(setId: Int): List<SetExerciseDetails>

    fun favoriteSet(trainerId: UUID, setId: Int)

    fun unfavoriteSet(trainerId: UUID, setId: Int)

    fun getSetByExercises(exerciseIds: List<Int>): List<Int>

    fun isSetOwner(trainerId: UUID, setId: Int): Boolean

    fun validateSetExerciseDetails(setId: Int, exerciseId: Int, details: String): Boolean
}
