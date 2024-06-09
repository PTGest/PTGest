package pt.isel.leic.ptgest.domain.set

enum class ExerciseDetailsType(val validator: (String) -> Number) {
    REPS({ value: String ->
        val intValue = requireNotNull(value.toIntOrNull()) { "Reps must be an integer" }
        require(intValue > 0) { "Reps must be a positive integer" }
        intValue
    }),
    WEIGHT({ value: String ->
        val doubleValue = requireNotNull(value.toDoubleOrNull()) { "Weight must be a double" }
        require(doubleValue > 0) { "Weight must be a positive double" }
        doubleValue
    }),

//  TODO: Implement the DURATION ExerciseDetails
    DURATION({ value: String ->
        val intValue = requireNotNull(value.toIntOrNull()) { "Duration must be an integer" }
        require(intValue > 0) { "Duration must be a positive integer" }
        intValue
    }),
    DISTANCE({ value: String ->
        val doubleValue = requireNotNull(value.toDoubleOrNull()) { "Distance must be a double" }
        require(doubleValue > 0) { "Distance must be a positive double" }
        doubleValue
    }),
    SPEED({ value: String ->
        val doubleValue = requireNotNull(value.toDoubleOrNull()) { "Speed must be a double" }
        require(doubleValue > 0) { "Speed must be a positive double" }
        doubleValue
    }),
    REST_TIME({ value: String ->
        val doubleValue = requireNotNull(value.toDoubleOrNull()) { "Rest time must be a double" }
        require(doubleValue >= 0) { "Rest time must be a positive double" }
        doubleValue
    }),
    RESISTANCE({ value: String ->
        val doubleValue = requireNotNull(value.toDoubleOrNull()) { "Resistance must be a double" }
        require(doubleValue >= 0) { "Resistance must be a positive double" }
        doubleValue
    });

    companion object {
        fun validateSetDetails(details: Map<ExerciseDetailsType, String>): Map<ExerciseDetailsType, Any> =
            details.map { (key, value) ->
                val validator = key.validator
                val validatedValue = validator.invoke(value)
                key to validatedValue
            }.toMap()
    }
}
