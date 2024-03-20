package pt.isel.leic.ptgest.repository.jdbi

import org.jdbi.v3.core.Handle
import pt.isel.leic.ptgest.repository.AuthRepo

class JdbiAuthRepo(handle: Handle) : AuthRepo