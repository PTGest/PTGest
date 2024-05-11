begin work;

do
$$
    declare
        table_name text;
        seq_name   text;
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

end work;
