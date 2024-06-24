// package pt.isel.leic.ptgest.service
//
// import org.jdbi.v3.core.transaction.TransactionIsolationLevel
// import org.mockito.Mockito.mock
// import pt.isel.leic.ptgest.domain.auth.AuthDomain
// import pt.isel.leic.ptgest.domain.auth.model.JWTSecret
// import pt.isel.leic.ptgest.repository.AuthRepo
// import pt.isel.leic.ptgest.repository.CompanyRepo
// import pt.isel.leic.ptgest.repository.ExerciseRepo
// import pt.isel.leic.ptgest.repository.ReportRepo
// import pt.isel.leic.ptgest.repository.SessionRepo
// import pt.isel.leic.ptgest.repository.SetRepo
// import pt.isel.leic.ptgest.repository.TraineeDataRepo
// import pt.isel.leic.ptgest.repository.TraineeRepo
// import pt.isel.leic.ptgest.repository.TrainerRepo
// import pt.isel.leic.ptgest.repository.UserRepo
// import pt.isel.leic.ptgest.repository.WorkoutRepo
// import pt.isel.leic.ptgest.repository.transaction.Transaction
// import pt.isel.leic.ptgest.repository.transaction.TransactionManager
// import pt.isel.leic.ptgest.services.MailService
// import pt.isel.leic.ptgest.services.auth.AuthService
// import pt.isel.leic.ptgest.services.auth.JwtService
//
// object MockRepos {
//    val mockAuthRepo: AuthRepo = mock(AuthRepo::class.java)
//    val mockUserRepo: UserRepo = mock(UserRepo::class.java)
//    val mockWorkoutRepo: WorkoutRepo = mock(WorkoutRepo::class.java)
//    val mockCompanyRepo: CompanyRepo = mock(CompanyRepo::class.java)
//    val mockTrainerRepo: TrainerRepo = mock(TrainerRepo::class.java)
//    val mockTraineeRepo: TraineeRepo = mock(TraineeRepo::class.java)
//    val mockTraineeDataRepo: TraineeDataRepo = mock(TraineeDataRepo::class.java)
//    val mockSessionRepo: SessionRepo = mock(SessionRepo::class.java)
//    val mockSetRepo: SetRepo = mock(SetRepo::class.java)
//    val mockExerciseRepo: ExerciseRepo = mock(ExerciseRepo::class.java)
//    val mockReportRepo: ReportRepo = mock(ReportRepo::class.java)
// }
//
// object MockServices {
//
//    val mockTransaction = object : Transaction {
//        override val authRepo: AuthRepo = MockRepos.mockAuthRepo
//        override val userRepo: UserRepo = MockRepos.mockUserRepo
//        override val workoutRepo: WorkoutRepo = MockRepos.mockWorkoutRepo
//        override val companyRepo: CompanyRepo = MockRepos.mockCompanyRepo
//        override val exerciseRepo: ExerciseRepo = MockRepos.mockExerciseRepo
//        override val reportRepo: ReportRepo = MockRepos.mockReportRepo
//        override val sessionRepo: SessionRepo = MockRepos.mockSessionRepo
//        override val setRepo: SetRepo = MockRepos.mockSetRepo
//        override val traineeDataRepo: TraineeDataRepo = MockRepos.mockTraineeDataRepo
//        override val trainerRepo: TrainerRepo = MockRepos.mockTrainerRepo
//        override val traineeRepo: TraineeRepo = MockRepos.mockTraineeRepo
//
//        override fun commit() {}
//        override fun rollback() {}
//    }
//
//    private val mockTransactionManager = object : TransactionManager {
//        override fun <R> run(block: (Transaction) -> R): R =
//            (mockTransaction).let(block)
//
//        override fun <R> runWithLevel(level: TransactionIsolationLevel, block: (Transaction) -> R): R =
//            (mockTransaction).let(block)
//    }
//
//    fun buildMockJwtService(jwtSecret: JWTSecret): JwtService =
//        JwtService(
//            jwtSecret,
//            mockTransactionManager
//        )
//
//    fun buildMockAuthServices(
//        authDomain: AuthDomain,
//        jwtService: JwtService,
//        mailService: MailService
//    ): AuthService =
//        AuthService(
//            authDomain,
//            jwtService,
//            mailService,
//            mockTransactionManager
//        )
// }
