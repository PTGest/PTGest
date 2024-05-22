package pt.isel.leic.ptgest.repository.jdbi.config.mappers

import org.jdbi.v3.core.mapper.RowMapper
import org.jdbi.v3.core.statement.StatementContext
import pt.isel.leic.ptgest.domain.workout.MuscleGroup
import pt.isel.leic.ptgest.domain.workout.model.Workout
import java.sql.ResultSet

class WorkoutMapper : RowMapper<Workout> {
    override fun map(rs: ResultSet, ctx: StatementContext?): Workout {
        val id = rs.getInt("id")
        val name = rs.getString("name")
        val description = rs.getString("description")
        val muscleGroup = rs.getArray("muscle_group").array as List<*>

        val muscleGroupList: MutableList<MuscleGroup> = mutableListOf()
        muscleGroup.forEach {
            if (it is String) {
                muscleGroupList.add(MuscleGroup.valueOf(it))
            }
        }

        return Workout(id, name, description, muscleGroupList)
    }
}
