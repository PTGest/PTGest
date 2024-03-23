package pt.isel.leic.ptgest.repository

import org.springframework.boot.test.context.SpringBootTest
import pt.isel.leic.ptgest.PtgestApplication

@SpringBootTest(classes = [PtgestApplication::class])
class UserRepoTests {

    val jdbi = getDevJdbi()


}