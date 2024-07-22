package pt.isel.leic.ptgest.services

import org.springframework.stereotype.Service
import pt.isel.leic.ptgest.domain.common.Order
import pt.isel.leic.ptgest.domain.common.Source
import pt.isel.leic.ptgest.domain.exercise.model.Exercise
import pt.isel.leic.ptgest.domain.exercise.model.ExerciseDetails
import pt.isel.leic.ptgest.domain.trainee.model.Trainee
import pt.isel.leic.ptgest.domain.trainer.model.Trainer
import pt.isel.leic.ptgest.domain.user.Gender
import pt.isel.leic.ptgest.domain.workout.Modality
import pt.isel.leic.ptgest.domain.workout.MuscleGroup
import pt.isel.leic.ptgest.repository.transaction.TransactionManager
import pt.isel.leic.ptgest.services.errors.CompanyError
import pt.isel.leic.ptgest.services.errors.ResourceNotFoundError
import pt.isel.leic.ptgest.services.errors.UserError
import pt.isel.leic.ptgest.services.utils.Validators
import java.util.*

@Service
class CompanyService(
    private val mailService: MailService,
    private val transactionManager: TransactionManager
) {
    fun getCompanyTrainers(
        companyId: UUID,
        gender: Gender?,
        availability: Order,
        name: String?,
        excludeTraineeTrainer: UUID?,
        skip: Int?,
        limit: Int?
    ): Pair<List<Trainer>, Int> {
        Validators.validate(
            Validators.ValidationRequest(skip, "Skip must be a positive number.") { it as Int >= 0 },
            Validators.ValidationRequest(limit, "Limit must be a positive number.") { it as Int > 0 },
            Validators.ValidationRequest(name, "Name must not be blank.") { (it as String).isNotBlank() }
        )

        return transactionManager.run {
            val companyRepo = it.companyRepo

            val trainers = companyRepo.getTrainers(
                companyId,
                skip ?: 0,
                limit,
                gender,
                availability,
                name,
                excludeTraineeTrainer
            )
            val totalResults = companyRepo.getTotalTrainers(companyId, gender, name, excludeTraineeTrainer)

            return@run Pair(trainers, totalResults)
        }
    }

    fun getCompanyTrainees(
        companyId: UUID,
        gender: Gender?,
        name: String?,
        skip: Int?,
        limit: Int?
    ): Pair<List<Trainee>, Int> {
        Validators.validate(
            Validators.ValidationRequest(skip, "Skip must be a positive number.") { it as Int >= 0 },
            Validators.ValidationRequest(limit, "Limit must be a positive number.") { it as Int > 0 },
            Validators.ValidationRequest(name, "Name must not be blank.") { (it as String).isNotBlank() }
        )

        return transactionManager.run {
            val companyRepo = it.companyRepo

            val trainees = companyRepo.getTrainees(companyId, skip ?: 0, limit, gender, name)
            val totalTrainees = companyRepo.getTotalTrainees(companyId, gender, name)

            return@run Pair(trainees, totalTrainees)
        }
    }

    fun assignTrainerToTrainee(
        companyId: UUID,
        trainerId: UUID,
        traineeId: UUID
    ) {
        transactionManager.run {
            val companyRepo = it.companyRepo

            val trainer = companyRepo.getTrainer(trainerId, companyId)
                ?: throw CompanyError.TrainerNotFound

            if (!companyRepo.isTraineeFromCompany(traineeId, companyId)) {
                throw CompanyError.TraineeNotFromCompany
            }

            if (trainer.assignedTrainees >= trainer.capacity) {
                throw CompanyError.TrainerCapacityReached
            }

            companyRepo.assignTrainerToTrainee(trainerId, traineeId)
        }
    }

    fun reassignTrainer(
        companyId: UUID,
        newTrainerId: UUID,
        traineeId: UUID
    ) {
        val traineeMail = transactionManager.run {
            val companyRepo = it.companyRepo
            val traineeRepo = it.traineeRepo
            val sessionRepo = it.sessionRepo
            val userRepo = it.userRepo

            val traineeDetails = userRepo.getUserDetails(traineeId)
                ?: throw UserError.UserNotFound

            if (!companyRepo.isTraineeFromCompany(traineeId, companyId)) {
                throw CompanyError.TraineeNotFromCompany
            }

            val newTrainer = companyRepo.getTrainer(newTrainerId, companyId)
                ?: throw CompanyError.TrainerNotFound

            if (newTrainer.assignedTrainees >= newTrainer.capacity) {
                throw CompanyError.TrainerCapacityReached
            }

            if (traineeRepo.isTraineeAssignedToTrainer(traineeId, newTrainerId)) {
                throw CompanyError.TrainerAlreadyAssociatedToTrainee
            }

            sessionRepo.getTraineeSessions(traineeId, Date())
                .forEach { session ->
                    sessionRepo.cancelSession(session, Source.COMPANY, "Trainer reassigned.")
                }
            companyRepo.reassignTrainer(newTrainerId, traineeId)

            return@run traineeDetails.email
        }

        mailService.sendMail(
            traineeMail,
            "Trainer reassigned",
            "Your trainer has been reassigned. All sessions with the previous trainer have been cancelled. \n" +
                "You can now schedule new sessions with your new trainer."
        )
    }

    fun updateTrainerCapacity(
        companyId: UUID,
        trainerId: UUID,
        capacity: Int
    ) {
        require(capacity >= 0) { "Capacity must be a positive number." }

        transactionManager.run {
            val companyRepo = it.companyRepo

            val trainer = companyRepo.getTrainer(trainerId, companyId)
                ?: throw CompanyError.TrainerNotFound

            require(capacity >= trainer.assignedTrainees) { "Capacity must be greater than the number of assigned trainees." }

            companyRepo.updateTrainerCapacity(companyId, trainerId, capacity)
        }
    }

    fun createCustomExercise(
        companyId: UUID,
        name: String,
        description: String?,
        muscleGroup: List<MuscleGroup>,
        modality: Modality,
        ref: String?
    ): Int {
        Validators.validate(
            Validators.ValidationRequest(description, "Description must not be empty.") { (it as String).isNotEmpty() },
            Validators.ValidationRequest(
                ref,
                "Reference must be a valid YouTube URL."
            ) { Validators.isYoutubeUrl(it as String) }
        )

        return transactionManager.run {
            val companyRepo = it.companyRepo
            val exerciseRepo = it.exerciseRepo

            val exerciseId = exerciseRepo.createExercise(
                name,
                description,
                muscleGroup,
                modality,
                ref
            )

            companyRepo.associateCompanyToExercise(companyId, exerciseId)

            return@run exerciseId
        }
    }

    fun getExercises(
        companyId: UUID,
        name: String?,
        muscleGroup: MuscleGroup?,
        modality: Modality?,
        skip: Int?,
        limit: Int?
    ): Pair<List<Exercise>, Int> {
        Validators.validate(
            Validators.ValidationRequest(skip, "Skip must be a positive number.") { it as Int >= 0 },
            Validators.ValidationRequest(limit, "Limit must be a positive number.") { it as Int > 0 },
            Validators.ValidationRequest(name, "Name must not be blank.") { (it as String).isNotBlank() }
        )

        return transactionManager.run {
            val exerciseRepo = it.exerciseRepo

            val exercises = exerciseRepo.getCompanyExercises(
                companyId,
                name,
                muscleGroup,
                modality,
                skip ?: 0,
                limit
            )

            val totalExercises = exerciseRepo.getTotalCompanyExercises(
                companyId,
                name,
                muscleGroup,
                modality
            )

            return@run Pair(exercises, totalExercises)
        }
    }

    fun getExerciseDetails(
        companyId: UUID,
        exerciseId: Int
    ): ExerciseDetails =
        transactionManager.run {
            val exerciseRepo = it.exerciseRepo

            return@run exerciseRepo.getCompanyExerciseDetails(companyId, exerciseId)
                ?: throw ResourceNotFoundError
        }
}
