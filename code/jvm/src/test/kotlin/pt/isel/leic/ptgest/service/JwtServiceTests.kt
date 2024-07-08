 package pt.isel.leic.ptgest.service

 import io.jsonwebtoken.ExpiredJwtException
 import io.jsonwebtoken.Jwts
 import io.jsonwebtoken.SignatureAlgorithm
 import org.junit.jupiter.api.AfterEach
 import org.junit.jupiter.api.BeforeAll
 import org.junit.jupiter.api.Nested
 import org.mockito.Mockito.reset
 import org.mockito.Mockito.spy
 import org.mockito.Mockito.`when`
 import org.springframework.beans.factory.annotation.Autowired
 import org.springframework.boot.test.context.SpringBootTest
 import pt.isel.leic.ptgest.domain.auth.AuthDomain
 import pt.isel.leic.ptgest.domain.auth.model.AccessTokenDetails
 import pt.isel.leic.ptgest.domain.auth.model.JWTSecret
 import pt.isel.leic.ptgest.domain.user.Role
 import pt.isel.leic.ptgest.domain.user.model.UserDetails
 import pt.isel.leic.ptgest.service.MockRepos.mockAuthRepo
 import pt.isel.leic.ptgest.service.MockServices.buildMockJwtService
 import pt.isel.leic.ptgest.services.errors.AuthError
 import pt.isel.leic.ptgest.services.JwtService
 import java.util.Calendar
 import java.util.Date
 import java.util.HashMap
 import java.util.UUID
 import kotlin.test.Test
 import kotlin.test.assertEquals
 import kotlin.test.assertFailsWith

 @SpringBootTest
 class JwtServiceTests {

    @Nested
    inner class GenerateTokenTests {

        @Test
        fun `generate token with success`() {
            `when`(mockUserRepo.getUserDetails(accessTokenDetails.userId))
                .then { userDetails }

            `when`(mockAuthRepo.getTokenVersion(accessTokenDetails.userId))
                .then { accessTokenDetails.version }

            val token = createToken()
            val tokenDetails = decryptToken(token)

            assert(token.isNotEmpty())
            assertEquals(accessTokenDetails.userId, tokenDetails.userId)
            assertEquals(accessTokenDetails.role, tokenDetails.role)
            assertEquals(
                generateDateToCompare(accessTokenDetails.expirationDate),
                generateDateToCompare(tokenDetails.expirationDate)
            )
            assertEquals(accessTokenDetails.version, tokenDetails.version)
        }

        @Test
        fun `generate token without any user details`() {
            `when`(mockUserRepo.getUserDetails(accessTokenDetails.userId))
                .then { null }

            assertFailsWith<AuthError.UserAuthenticationError.UserNotFound> { createToken() }
        }

        @Test
        fun `generate token without any token version`() {
            `when`(mockUserRepo.getUserDetails(accessTokenDetails.userId))
                .then { userDetails }

            `when`(mockAuthRepo.getTokenVersion(accessTokenDetails.userId))
                .then { null }

            val token = createToken()
            val tokenDetails = decryptToken(token)

            assert(token.isNotEmpty())
            assertEquals(accessTokenDetails.userId, tokenDetails.userId)
            assertEquals(accessTokenDetails.role, tokenDetails.role)
            assertEquals(
                generateDateToCompare(accessTokenDetails.expirationDate),
                generateDateToCompare(tokenDetails.expirationDate)
            )
            assertEquals(accessTokenDetails.version, tokenDetails.version)
        }

        @Test
        fun `generate token with expiration date before current date`() {
            `when`(mockUserRepo.getUserDetails(accessTokenDetails.userId))
                .then { userDetails }

            assertFailsWith<IllegalArgumentException> {
                createInvalidToken()
            }
        }

        @Test
        fun `generate token with expiration date same as current date`() {
            `when`(mockUserRepo.getUserDetails(accessTokenDetails.userId))
                .then { userDetails }

            assertFailsWith<IllegalArgumentException> {
                mockJwtService.generateToken(
                    accessTokenDetails.userId,
                    accessTokenDetails.role,
                    currentDate,
                    currentDate
                )
            }
        }

        @Test
        fun `generate token with invalid user role`() {
            `when`(mockUserRepo.getUserDetails(accessTokenDetails.userId))
                .then { userDetails.copy(role = Role.INDEPENDENT_TRAINER) }

            assertFailsWith<AuthError.UserAuthenticationError.InvalidUserRoleException> {
                createToken()
            }
        }

        private fun decryptToken(token: String): AccessTokenDetails {
            val claims = Jwts.parser().setSigningKey(testSecret.value).parseClaimsJws(token).body

            val expirationDate = claims.expiration
            val version = claims["version"] as Int

            return AccessTokenDetails(
                userId = UUID.fromString(claims.id),
                role = Role.valueOf(claims.subject),
                expirationDate = Date(expirationDate.time),
                version = version
            )
        }
    }

    @Nested
    inner class ExtractTokenTests {

        @Test
        fun `generate token and extract it successfully`() {
            `when`(mockUserRepo.getUserDetails(accessTokenDetails.userId))
                .then { userDetails }

            val token = createToken()
            val extractedToken = mockJwtService.extractToken(token)

            assertEquals(accessTokenDetails.userId, extractedToken.userId)
            assertEquals(accessTokenDetails.role, extractedToken.role)

            assertEquals(
                generateDateToCompare(accessTokenDetails.expirationDate),
                generateDateToCompare(extractedToken.expirationDate)
            )
        }

        @Test
        fun `generate token and extract it with miss match role`() {
            `when`(mockUserRepo.getUserDetails(accessTokenDetails.userId))
                .then { userDetails }

            val token = createToken()

            `when`(mockUserRepo.getUserDetails(accessTokenDetails.userId))
                .then { userDetails.copy(role = Role.INDEPENDENT_TRAINER) }

            assertFailsWith<AuthError.UserAuthenticationError.InvalidUserRoleException> {
                mockJwtService.extractToken(token)
            }
        }

        @Test
        fun `generate token and extract it when token is expired with expiration date`() {
            `when`(mockUserRepo.getUserDetails(accessTokenDetails.userId))
                .then { userDetails }

            val creationDate = Calendar.getInstance()
                .apply { time = currentDate; add(Calendar.MONTH, -2) }.time
            val expirationDate = Calendar.getInstance()
                .apply { time = currentDate; add(Calendar.MONTH, -1) }.time

            println(creationDate)
            println(expirationDate)

            val token = mockJwtService.generateToken(
                accessTokenDetails.userId,
                accessTokenDetails.role,
                expirationDate,
                creationDate
            )

            assertFailsWith<ExpiredJwtException> {
                mockJwtService.extractToken(token)
            }
        }

        @Test
        fun `generate token and extract it when there are no user details`() {
            `when`(mockUserRepo.getUserDetails(accessTokenDetails.userId))
                .then { userDetails }

            val token = createToken()

            `when`(mockUserRepo.getUserDetails(accessTokenDetails.userId))
                .then { null }

            assertFailsWith<AuthError.UserAuthenticationError.UserNotFound> {
                mockJwtService.extractToken(token)
            }
        }

        private fun createToken(): String {
            val claims = HashMap<String, Any>()
            claims["version"] = accessTokenDetails.version

            return Jwts.builder().setClaims(claims)
                .setId(accessTokenDetails.userId.toString())
                .setSubject(accessTokenDetails.role.name)
                .setExpiration(accessTokenDetails.expirationDate)
                .signWith(SignatureAlgorithm.HS256, testSecret.value).compact()
        }
    }

    @AfterEach
    fun cleanUp() {
        reset(mockUserRepo)
    }

    private val currentDate = Date()

    private val userId = UUID.randomUUID()

    private val userDetails = UserDetails(
        id = userId,
        name = "UserTest",
        email = "usertest@mail.com",
        passwordHash = "passwordHashed",
        role = Role.COMPANY,
        active = true
    )

    private val accessTokenDetails = AccessTokenDetails(
        userId = UUID.randomUUID(),
        role = Role.COMPANY,
        expirationDate = mockAuthDomain.createAccessTokenExpirationDate(currentDate),
        version = 1
    )

    private val mockUserRepo = MockRepos.mockUserRepo

    private fun generateDateToCompare(date: Date): Date =
        Calendar.getInstance().apply { time = date; set(Calendar.MILLISECOND, 0) }.time

    private fun createToken(): String {
        return mockJwtService.generateToken(
            accessTokenDetails.userId,
            accessTokenDetails.role,
            accessTokenDetails.expirationDate,
            currentDate
        )
    }

    private fun createInvalidToken(): String {
        return mockJwtService.generateToken(
            accessTokenDetails.userId,
            accessTokenDetails.role,
            Calendar.getInstance().apply { time = currentDate; add(Calendar.MONTH, -1) }.time,
            currentDate
        )
    }

    companion object {
        lateinit var mockJwtService: JwtService
        lateinit var mockAuthDomain: AuthDomain
        lateinit var testSecret: JWTSecret

        @JvmStatic
        @BeforeAll
        fun setUp(@Autowired authDomain: AuthDomain, @Autowired secret: JWTSecret) {
            mockAuthDomain = spy(authDomain)
            mockJwtService = buildMockJwtService(secret)
            testSecret = secret
        }
    }
 }
