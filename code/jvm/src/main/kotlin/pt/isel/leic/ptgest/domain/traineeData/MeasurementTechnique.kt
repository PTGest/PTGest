package pt.isel.leic.ptgest.domain.traineeData

import pt.isel.leic.ptgest.domain.user.Gender

enum class MeasurementTechnique(val totalFolds: Int, val bodyFatCalculator: (Gender?, Map<SkinFold, Double>, Int) -> Double) {
    THREE_SKIN_FOLDS(3, { gender, skinFolds, age ->
        when (gender) {
            Gender.MALE -> {
                require(
                    skinFolds.containsKey(SkinFold.PECTORAL) &&
                        skinFolds.containsKey(SkinFold.ABDOMINAL) &&
                        skinFolds.containsKey(SkinFold.THIGH)
                ) { "The skin folds must contain the following keys: PECTORAL, ABDOMINAL and THIGH" }

                require(skinFolds.values.all { it > 0 }) { "The skin folds must contain positive values" }

                val sum = skinFolds.values.sum()
                val dc = 1.1093800 - 0.0008267 * sum + 0.0000016 * (sum * sum) - 0.0002574 * age
                (495 / dc) - 450
            }

            Gender.FEMALE -> {
                require(
                    skinFolds.containsKey(SkinFold.TRICEPS) &&
                        skinFolds.containsKey(SkinFold.SUPRAILIAC) &&
                        skinFolds.containsKey(SkinFold.THIGH)
                ) { "The skin folds must contain the following keys: TRICEPS, SUPRAILIAC and THIGH" }

                require(skinFolds.values.all { it > 0 }) { "The skin folds must contain positive values" }

                val sum = skinFolds.values.sum()
                val dc = 1.0994921 - 0.0009929 * sum + 0.0000023 * (sum * sum) - 0.0001392 * age
                (495 / dc) - 450
            }

            else -> {
                throw IllegalArgumentException("The gender must be either Male or Female")
            }
        }
    }),
    FOUR_SKIN_FOLDS(4, { gender, skinFolds, age ->
        when (gender) {
            Gender.MALE -> {
                require(
                    skinFolds.containsKey(SkinFold.TRICEPS) &&
                        skinFolds.containsKey(SkinFold.THIGH) &&
                        skinFolds.containsKey(SkinFold.ABDOMINAL) &&
                        skinFolds.containsKey(SkinFold.SUPRAILIAC)
                ) { "The skin folds must contain the following keys: TRICEPS, THIGH, ABDOMINAL and SUPRAILIAC" }

                require(skinFolds.values.all { it > 0 }) { "The skin folds must contain positive values" }

                val sum = skinFolds.values.sum()
                val dc = (0.29288 * sum) - (0.0005 * (sum * sum)) + (0.15845 * age) - 5.76377
                (495 / dc) - 450
            }

            Gender.FEMALE -> {
                require(
                    skinFolds.containsKey(SkinFold.THIGH) &&
                        skinFolds.containsKey(SkinFold.ABDOMINAL) &&
                        skinFolds.containsKey(SkinFold.SUPRAILIAC) &&
                        skinFolds.containsKey(SkinFold.TRICEPS)
                ) { "The skin folds must contain the following keys: THIGH, ABDOMINAL, SUPRAILIAC and TRICEPS" }

                require(skinFolds.values.all { it > 0 }) { "The skin folds must contain positive values" }

                val sum = skinFolds.values.sum()
                val dc = (0.29669 * sum) - (0.00043 * (sum * sum)) + (0.02963 * age) + 1.4072
                (495 / dc) - 450
            }

            else -> {
                throw IllegalArgumentException("The gender must be either Male or Female")
            }
        }
    }),
    SEVEN_SKIN_FOLDS(3, { _, skinFolds, age ->
        require(skinFolds.values.all { it > 0 }) { "The skin folds must contain positive values" }

        val sum = skinFolds.values.sum()
        val dc = 1.112 - 0.00043499 * (sum) + 0.00000055 * (sum) * 2 - 0.00028826 * (age)
        (495 / dc) - 450
    });

    companion object {
        fun getTechnique(size: Int): MeasurementTechnique {
            return MeasurementTechnique.entries.find { it.totalFolds == size }
                ?: throw IllegalArgumentException("The number of skin folds must be 3, 4 or 7")
        }
    }
}
