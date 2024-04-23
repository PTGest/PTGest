package pt.isel.leic.ptgest.repository

import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.transaction.TransactionIsolationLevel
import org.postgresql.ds.PGSimpleDataSource
import pt.isel.leic.ptgest.ServerConfig
import pt.isel.leic.ptgest.repository.jdbi.configureWithAppRequirements

fun <R> asTransaction(jdbi: Jdbi, block: (Handle) -> R): R =
    jdbi.inTransaction<R, Exception> { handle -> block(handle) }

fun <R> asTransactionWithLevel(jdbi: Jdbi, isolationLevel: TransactionIsolationLevel, block: (Handle) -> R): R =
    jdbi.inTransaction<R, Exception>(isolationLevel) { handle -> block(handle) }

fun getDevJdbi(): Jdbi = Jdbi.create(
    PGSimpleDataSource().apply {
        setURL(ServerConfig.dbUrl)
        currentSchema = "dev"
    }
).configureWithAppRequirements()

fun Handle.cleanup() {
    createUpdate(
        """
        do $$
            declare
                table_name text;
                seq_name text;
            begin
                for table_name in (select tablename from pg_tables where schemaname = 'dev')
                    loop
                        execute 'truncate table dev.' || table_name || ' cascade';
                    end loop;
            
                for seq_name in (SELECT sequencename FROM pg_sequences WHERE schemaname = 'dev')
                    loop
                        execute 'alter sequence dev.' || seq_name || ' restart with 1';
                        raise notice 'Sequence % rested to 1 successfully', seq_name;
                    end loop;
            end;
        $$;
        """.trimIndent()
    ).execute()
}
