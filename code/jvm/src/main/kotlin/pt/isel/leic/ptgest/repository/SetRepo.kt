package pt.isel.leic.ptgest.repository

import pt.isel.leic.ptgest.domain.workout.SetType
import pt.isel.leic.ptgest.domain.workout.model.Set
import pt.isel.leic.ptgest.domain.workout.model.SetExerciseDetails
import java.util.*

interface SetRepo {

    fun createSet(trainerId: UUID, name: String, notes: String?, type: SetType): Int

    fun associateExerciseToSet(orderId: Int, exerciseId: Int, setId: Int, details: String)

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
}
