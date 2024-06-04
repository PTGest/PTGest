package pt.isel.leic.ptgest.repository.jdbi.config.mappers

import org.jdbi.v3.core.mapper.ColumnMapper
import org.jdbi.v3.core.statement.StatementContext
import pt.isel.leic.ptgest.domain.workout.MuscleGroup
import java.sql.ResultSet

class MuscleGroupListMapper : ColumnMapper<List<MuscleGroup>> {
    override fun map(r: ResultSet, columnNumber: Int, ctx: StatementContext?): List<MuscleGroup> {
        val muscleGroup = r.getArray("muscle_group").array as Array<*>

        val muscleGroupList: MutableList<MuscleGroup> = mutableListOf()
        muscleGroup.forEach {
            if (it is String) {
                muscleGroupList.add(MuscleGroup.valueOf(it))
            }
        }

        return muscleGroupList
    }
}