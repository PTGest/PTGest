package pt.isel.leic.ptgest.services.utils

import pt.isel.leic.ptgest.domain.workout.Modality
import pt.isel.leic.ptgest.domain.workout.SetType

class ExerciseValidator {

    interface Validator {
        fun validate(details: Map<String, String>): Map<String, Any>
    }

    class SimpleSetValidators {
        class BodyWeightValidator : Validator {
            override fun validate(details: Map<String, String>): Map<String, Any> {
                val resultMap = mutableMapOf<String, Any>()

                val reps = requireNotNull(details["reps"]?.toIntOrNull())
                { "Reps is required and must be an integer" }

                require(reps > 0) { "Reps must be a positive integer" }

                resultMap["reps"] = reps

                return resultMap
            }
        }

        class WeightLiftValidator : Validator {
            override fun validate(details: Map<String, String>): Map<String, Any> {
                val resultMap = mutableMapOf<String, Any>()

                val reps = requireNotNull(details["reps"]?.toIntOrNull())
                { "Reps is required and must be an integer" }
                val weight = requireNotNull(details["weight"]?.toDoubleOrNull())
                { "Weight is required and must be a double" }

                require(reps > 0) { "Reps must be a positive integer" }
                require(weight > 0) { "Reps must be a positive double" }

                resultMap["reps"] = reps
                resultMap["weight"] = weight

                return resultMap
            }
        }

        class OutdoorActivitiesValidator : Validator {
            override fun validate(details: Map<String, String>): Map<String, Any> {
                val resultMap = mutableMapOf<String, Any>()

                val distance = details["distance"]
                val time = details["time"]

                when {
                    distance != null -> {
                        val distanceDouble = requireNotNull(distance.toDoubleOrNull())
                        { "Distance must be a double" }
                        require(distanceDouble > 0) { "Distance must be a positive double" }
                        resultMap["distance"] = distanceDouble
                    }

                    time != null -> {
                        val timeDouble = requireNotNull(time.toDoubleOrNull())
                        { "Time must be a double" }
                        require(timeDouble > 0) { "Time must be a positive double" }
                        resultMap["time"] = timeDouble
                    }

                    else -> throw IllegalArgumentException("Either distance or time must be provided")
                }

                return resultMap
            }
        }

        class RunningInValidator : Validator {
            override fun validate(details: Map<String, String>): Map<String, Any> {
                val resultMap = mutableMapOf<String, Any>()

                val distance = details["distance"]
                val time = details["time"]

                when {
                    distance != null -> {
                        val distanceDouble = requireNotNull(distance.toDoubleOrNull())
                        { "Distance must be a double" }
                        require(distanceDouble > 0) { "Distance must be a positive double" }
                        resultMap["distance"] = distanceDouble
                    }

                    time != null -> {
                        val timeDouble = requireNotNull(time.toDoubleOrNull())
                        { "Time must be a double" }
                        require(timeDouble > 0) { "Time must be a positive double" }
                        resultMap["time"] = timeDouble

                        val speed = requireNotNull(details["speed"]?.toDoubleOrNull())
                        { "Speed is required and must be a double" }
                        require(speed > 0) { "Speed must be a positive double" }
                        resultMap["speed"] = speed

                        if (details.containsKey("incline")) {
                            val incline = requireNotNull(details["incline"]?.toDoubleOrNull())
                            { "Incline is required and must be a double" }
                            require(incline >= 0) { "Incline must be a positive double" }
                            resultMap["incline"] = incline
                        }
                    }

                    else -> throw IllegalArgumentException("Either distance or time must be provided")
                }

                return resultMap
            }
        }

        class CyclingInValidator : Validator {
            override fun validate(details: Map<String, String>): Map<String, Any> {
                val resultMap = mutableMapOf<String, Any>()

                val distance = details["distance"]
                val time = details["time"]

                when {
                    distance != null -> {
                        val distanceDouble = requireNotNull(distance.toDoubleOrNull())
                        { "Distance must be a double" }
                        require(distanceDouble > 0) { "Distance must be a positive double" }
                        resultMap["distance"] = distanceDouble
                    }

                    time != null -> {
                        val timeDouble = requireNotNull(time.toDoubleOrNull())
                        { "Time must be a double" }
                        require(timeDouble > 0) { "Time must be a positive double" }
                        resultMap["time"] = timeDouble

                        val resistance = requireNotNull(details["resistance"]?.toDoubleOrNull())
                        { "Resistance is required and must be a double" }
                        require(resistance >= 0) { "Resistance must be a positive double" }
                    }

                    else -> throw IllegalArgumentException("Either distance or time must be provided")
                }

                return resultMap
            }
        }

        class OtherValidator : Validator {
            override fun validate(details: Map<String, String>): Map<String, Any> {
                return emptyMap()
            }
        }
    }

    class DropSetValidators {
        class BodyWeightValidator : Validator {
            override fun validate(details: Map<String, String>): Map<String, Any> {
                return emptyMap()
            }
        }

        class WeightLiftValidator : Validator {
            override fun validate(details: Map<String, String>): Map<String, Any> {
                val resultMap = mutableMapOf<String, Any>()

                val initialWeight = requireNotNull(details["initialWeight"]?.toDoubleOrNull())
                { "Initial weight is required and must be a double" }
                val finalWeight = requireNotNull(details["finalWeight"]?.toDoubleOrNull())
                { "Final weight is required and must be a double" }

                require(initialWeight > 0) { "Initial weight must be a positive double" }
                require(finalWeight > 0) { "Final weight must be a positive double" }

                resultMap["initialWeight"] = initialWeight
                resultMap["finalWeight"] = finalWeight

                return resultMap
            }
        }
    }

    companion object {
        private val validators: Map<Pair<SetType, Modality>, Validator> = mapOf(
            Pair(SetType.SIMPLESET, Modality.BODYWEIGHT) to SimpleSetValidators.BodyWeightValidator(),
            Pair(SetType.SIMPLESET, Modality.WEIGHTLIFT) to SimpleSetValidators.WeightLiftValidator(),
            Pair(SetType.SIMPLESET, Modality.RUNNING_IN) to SimpleSetValidators.RunningInValidator(),
            Pair(SetType.SIMPLESET, Modality.RUNNING_OUT) to SimpleSetValidators.OutdoorActivitiesValidator(),
            Pair(SetType.SIMPLESET, Modality.CYCLING_IN) to SimpleSetValidators.CyclingInValidator(),
            Pair(SetType.SIMPLESET, Modality.CYCLING_OUT) to SimpleSetValidators.OutdoorActivitiesValidator(),
            Pair(SetType.SIMPLESET, Modality.OTHER) to SimpleSetValidators.OtherValidator(),
            Pair(SetType.DROPSET, Modality.BODYWEIGHT) to DropSetValidators.BodyWeightValidator(),
            Pair(SetType.DROPSET, Modality.WEIGHTLIFT) to DropSetValidators.WeightLiftValidator(),
            Pair(SetType.SUPERSET, Modality.BODYWEIGHT) to SimpleSetValidators.BodyWeightValidator(),
            Pair(SetType.SUPERSET, Modality.WEIGHTLIFT) to SimpleSetValidators.WeightLiftValidator()
        )

        fun getValidator(setType: SetType, modality: Modality): Validator {
            val key = Pair(setType, modality)

            return requireNotNull(validators[key]) { "SetType $setType does not support ExerciseType $modality" }
        }
    }
}
