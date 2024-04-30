package pt.isel.leic.ptgest.domain.common

sealed class SetDetails {

    data class DropSet(
        val exercise: Int,
        val initialWeight: Double
    ) : SetDetails() {
        fun toMapDetails(): Map<String, Any> {
            require(initialWeight > 0) { "Initial weight must be greater than 0." }
            return mapOf(
                "initialWeight" to initialWeight
            )
        }
    }

    data class SuperSet(
        val exercises: List<SuperSetExercise>
    ) : SetDetails() {
        fun toMapDetails(): List<Map<String, Any>> {
            return exercises.map { it.toMapDetails() }
        }
    }

    data class SuperSetExercise(
        val exercise: Int,
        val reps: Int,
        val weight: Double?
    ) {
        fun toMapDetails(): Map<String, Any> {
            require(reps > 0) { "Reps must be greater than 0." }

            val properties = mutableMapOf<String, Any>(
                "exercise" to exercise,
                "reps" to reps
            )

            if (weight != null) {
                require(weight > 0) { "Weight must be greater than 0." }
                properties["weight"] = weight
            }

            return properties
        }
    }

    data class Running(
        val exercise: Int,
        val time: Double?,
        val distance: Double?,
        val speed: Double?
    ) : SetDetails() {
        fun toMapDetails(): Map<String, Any> {
            val properties = mutableMapOf<String, Any>()

            if (time != null) {
                require(time > 0) { "Time must be greater than 0." }
                properties["time"] = time
            }

            if (distance != null) {
                require(distance > 0) { "Distance must be greater than 0." }
                properties["distance"] = distance
            }

            if (speed != null) {
                require(speed > 0) { "Speed must be greater than 0." }
                properties["speed"] = speed
            }

            return properties
        }
    }

    data class BodyWeight(
        val exercise: Int,
        val reps: Int,
        val sets: Int
    ) : SetDetails() {
        fun toMapDetails(): Map<String, Any> {
            require(reps > 0) { "Reps must be greater than 0." }
            require(sets > 0) { "Sets must be greater than 0." }
            return mapOf(
                "reps" to reps,
                "sets" to sets
            )
        }
    }

    data class WeightedLift(
        val exercise: Int,
        val reps: Int,
        val sets: Int,
        val weight: Double
    ) : SetDetails() {
        fun toMapDetails(): Map<String, Any> {
            require(reps > 0) { "Reps must be greater than 0." }
            require(sets > 0) { "Sets must be greater than 0." }
            require(weight > 0) { "Weight must be greater than 0." }
            return mapOf(
                "reps" to reps,
                "sets" to sets,
                "weight" to weight
            )
        }
    }
}
