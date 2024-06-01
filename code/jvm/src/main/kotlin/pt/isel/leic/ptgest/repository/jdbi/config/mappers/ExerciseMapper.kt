package pt.isel.leic.ptgest.repository.jdbi.config.mappers

import org.jdbi.v3.core.mapper.RowMapper
import org.jdbi.v3.core.statement.StatementContext
import pt.isel.leic.ptgest.domain.workout.Modality
import pt.isel.leic.ptgest.domain.workout.MuscleGroup
import pt.isel.leic.ptgest.domain.workout.model.Exercise
import java.sql.ResultSet

class ExerciseMapper : RowMapper<Exercise> {
    override fun map(rs: ResultSet, ctx: StatementContext?): Exercise {
        val id = rs.getInt("id")
        val name = rs.getString("name")
        val muscleGroup = rs.getArray("muscle_group").array as Array<*>
        val modality = Modality.valueOf(rs.getString("modality"))

        val muscleGroupList: MutableList<MuscleGroup> = mutableListOf()
        muscleGroup.forEach {
            if (it is String) {
                muscleGroupList.add(MuscleGroup.valueOf(it))
            }
        }

        return Exercise(id, name, muscleGroupList, modality)
    }
}
