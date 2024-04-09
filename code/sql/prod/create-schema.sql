begin work;

create schema if not exists "prod";

create extension if not exists "uuid-ossp";

create table if not exists prod."user"
(
    id            uuid default uuid_generate_v4() primary key,
    name          varchar(40) check ("user".name <> '')                                       not null,
    email         varchar(50)
        check ( email ~ '^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$' ) unique    not null,
    password_hash varchar(256) check ("user".password_hash <> '')                      not null,
    role          varchar(20)
        check (role in ('COMPANY', 'HIRED_TRAINER', 'INDEPENDENT_TRAINER', 'TRAINEE')) not null
);

create table if not exists prod.token
(
    token_hash  varchar(256) primary key,
    user_id     uuid references prod."user" (id) on delete cascade,
    expiration  timestamp check ( expiration > now() ) not null
);

create table if not exists prod.password_reset_token
(
    token_hash  varchar(256) references prod.token(token_hash) on delete cascade primary key
);

create table if not exists prod.refresh_token
(
    token_hash  varchar(256) references prod.token(token_hash) on delete cascade primary key
);

create table if not exists prod.company
(
    id uuid primary key references prod."user" (id) on delete cascade
);

create table if not exists prod.personal_trainer
(
    id      uuid primary key references prod."user" (id) on delete cascade,
    gender  char check (gender in ('M', 'F', 'O', 'U')) not null,
    contact varchar(20) check ( contact ~ '^[+]{1}(?:[0-9\\-\\(\\)\\/\\.]\s?){6,15}[0-9]{1}$' )
);

create table if not exists prod.company_pt
(
    company_id uuid references prod.company (id) on delete cascade,
    pt_id      uuid references prod.personal_trainer (id) on delete cascade,
    primary key (company_id, pt_id)
);

create table if not exists prod.trainee
(
    id        uuid primary key references prod."user" (id) on delete cascade,
    gender    char check (gender in ('M', 'F', 'O', 'U')) not null,
    birthdate date                                        not null,
    contact   varchar(20) check ( contact ~ '^[+]{1}(?:[0-9\\-\\(\\)\\/\\.]\s?){6,15}[0-9]{1}$' )
);

create table if not exists prod.pt_trainee
(
    pt_id      uuid references prod.personal_trainer (id) on delete cascade,
    trainee_id uuid references prod.trainee (id) on delete cascade,
    primary key (pt_id, trainee_id)
);

create table if not exists prod.trainee_data
(
    trainee_id uuid references prod.trainee (id) on delete cascade,
    date       date  not null,
    body_data  jsonb not null,
    primary key (trainee_id, date)
);

create table if not exists prod.report
(
    trainee_id uuid references prod.trainee (id) on delete cascade,
    pt_id      uuid references prod.personal_trainer (id) on delete cascade,
    date       date                  not null,
    report     text                  not null,
    visibility boolean default false not null,
    primary key (trainee_id, pt_id, date)
);

create table if not exists prod.session
(
    trainee_id uuid references prod.trainee (id) on delete cascade,
    pt_id      uuid references prod.personal_trainer (id) on delete cascade,
    date       timestamp                           not null,
    category   char check (category in ('P', 'A')) not null,
    notes      text,
    primary key (trainee_id, pt_id, date)
);

create table if not exists prod.exercise
(
    id          uuid default uuid_generate_v4() primary key,
    pt_id       uuid references prod.personal_trainer (id) on delete cascade,
    name        varchar(50)                                                                 not null,
    description text,
    -- temporary to be changed
    category    char check (category in ('A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J')) not null,
    url         varchar(256)
);

create table if not exists prod.trainer_favorite_exercise
(
    pt_id       uuid references prod.personal_trainer (id) on delete cascade,
    exercise_id uuid references prod.exercise (id) on delete cascade,
    primary key (pt_id, exercise_id)
);

create table if not exists prod.session_exercise
(
    session_trainee_id uuid,
    session_pt_id      uuid,
    session_date       timestamp,
    exercise_id        uuid references prod.exercise (id) on delete cascade,
    sets               integer check ( sets > 0 )   not null,
    reps               integer check ( reps > 0 )   not null,
    weight             integer check ( weight > 0 ) not null,
    primary key (session_trainee_id, session_pt_id, session_date, exercise_id),
    foreign key (session_trainee_id, session_pt_id, session_date)
        references prod.session (trainee_id, pt_id, date) on delete cascade
);

create table if not exists prod.feedback
(
    id       uuid default uuid_generate_v4() primary key,
    source   char check (source in ('T', 'P')) not null,
    feedback text
);

create table if not exists prod.session_feedback
(
    feedback_id        uuid references prod.feedback (id) on delete cascade,
    session_trainee_id uuid,
    session_pt_id      uuid,
    session_date       timestamp,
    primary key (feedback_id, session_trainee_id, session_pt_id, session_date),
    foreign key (session_trainee_id, session_pt_id, session_date)
        references prod.session (trainee_id, pt_id, date) on delete cascade
);

create table if not exists prod.session_exercise_feedback
(
    feedback_id         uuid references prod.feedback (id) on delete cascade,
    session_trainee_id  uuid,
    session_pt_id       uuid,
    session_date        timestamp,
    session_exercise_id uuid,
    primary key (feedback_id, session_trainee_id, session_pt_id, session_date, session_exercise_id),
    foreign key (session_trainee_id, session_pt_id, session_date, session_exercise_id)
        references prod.session_exercise (session_trainee_id, session_pt_id, session_date, exercise_id) on delete cascade
);

end work
