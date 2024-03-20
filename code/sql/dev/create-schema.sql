begin work;

create schema if not exists "dev";

create extension if not exists "uuid-ossp";

create table if not exists dev."user"
(
    id            uuid default uuid_generate_v4() primary key,
    name          varchar(15)                                                       not null,
    email         varchar(50)
        check ( email ~ '^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$' ) unique not null,
    password_hash varchar(256)                                                      not null
);

create table if not exists dev.token
(
    token        varchar(256) primary key                                                            not null,
    user_id      uuid references dev."user" (id) on delete cascade                                   not null,
    role         varchar(20)  check (role in ('COMPANY', 'HIRED_TRAINER', 'INDEPENDENT_TRAINER', 'TRAINEE')) not null,
    last_used_at timestamp                                                                           not null
);

create table if not exists dev.company
(
    id uuid primary key references dev."user" (id) on delete cascade
);

create table if not exists dev.personal_trainer
(
    id      uuid primary key references dev."user" (id) on delete cascade,
    gender  char check (gender in ('M', 'F', 'O', 'U')) not null,
    contact varchar(20) check ( contact ~ '^\+?(\d[\d-. ]+)?(\([\d-. ]+\))?[\d-. ]+\d$' )
);

create table if not exists dev.company_pt
(
    company_id uuid references dev.company (id) on delete cascade,
    pt_id      uuid references dev.personal_trainer (id) on delete cascade,
    primary key (company_id, pt_id)
);

create table if not exists dev.trainee
(
    id        uuid primary key references dev."user" (id) on delete cascade,
    gender    char check (gender in ('M', 'F', 'O', 'U')) not null,
    birthdate date                                        not null,
    contact   varchar(20) check ( contact ~ '^\+?(\d[\d-. ]+)?(\([\d-. ]+\))?[\d-. ]+\d$' )
);

create table if not exists dev.pt_trainee
(
    pt_id      uuid references dev.personal_trainer (id) on delete cascade,
    trainee_id uuid references dev.trainee (id) on delete cascade,
    primary key (pt_id, trainee_id)
);

create table if not exists dev.trainee_data
(
    trainee_id uuid references dev.trainee (id) on delete cascade,
    date       date  not null,
    body_data  jsonb not null,
    primary key (trainee_id, date)
);

create table if not exists dev.report
(
    trainee_id uuid references dev.trainee (id) on delete cascade,
    pt_id      uuid references dev.personal_trainer (id) on delete cascade,
    date       date                  not null,
    report     text                  not null,
    visibility boolean default false not null,
    primary key (trainee_id, pt_id, date)
);

create table if not exists dev.session
(
    trainee_id uuid references dev.trainee (id) on delete cascade,
    pt_id      uuid references dev.personal_trainer (id) on delete cascade,
    date       date                                not null,
    category   char check (category in ('P', 'A')) not null,
    notes      text,
    primary key (trainee_id, pt_id, date)
);

create table if not exists dev.exercise
(
    id          uuid default uuid_generate_v4() primary key,
    pt_id       uuid references dev.personal_trainer (id) on delete cascade,
    name        varchar(50) not null,
    description text,
    url         varchar(256)
);

create table if not exists dev.category
(
    id   uuid default uuid_generate_v4() primary key,
    name varchar(50) not null
);

create table if not exists dev.exercise_category
(
    exercise_id uuid references dev.exercise (id) on delete cascade,
    category_id uuid references dev.category (id) on delete cascade,
    primary key (exercise_id, category_id)
);

create table if not exists dev.trainer_favorite_exercise
(
    pt_id       uuid references dev.personal_trainer (id) on delete cascade,
    exercise_id uuid references dev.exercise (id) on delete cascade,
    primary key (pt_id, exercise_id)
);

create table if not exists dev.session_exercise
(
    session_trainee_id uuid,
    session_pt_id      uuid,
    session_date       date,
    exercise_id        uuid references dev.exercise (id) on delete cascade,
    sets               integer check ( sets > 0 )   not null,
    reps               integer check ( reps > 0 )   not null,
    weight             integer check ( weight > 0 ) not null,
    primary key (session_trainee_id, session_pt_id, session_date, exercise_id),
    foreign key (session_trainee_id, session_pt_id, session_date)
        references dev.session (trainee_id, pt_id, date) on delete cascade
);

create table if not exists dev.feedback
(
    id       uuid default uuid_generate_v4() primary key,
    source   char check (source in ('T', 'P')) not null,
    feedback text
);

create table if not exists dev.session_feedback
(
    feedback_id        uuid references dev.feedback (id) on delete cascade,
    session_trainee_id uuid,
    session_pt_id      uuid,
    session_date       date,
    primary key (feedback_id, session_trainee_id, session_pt_id, session_date),
    foreign key (session_trainee_id, session_pt_id, session_date)
        references dev.session (trainee_id, pt_id, date) on delete cascade
);

create table if not exists dev.session_exercise_feedback
(
    feedback_id         uuid references dev.feedback (id) on delete cascade,
    session_trainee_id  uuid,
    session_pt_id       uuid,
    session_date        date,
    session_exercise_id uuid,
    primary key (feedback_id, session_trainee_id, session_pt_id, session_date, session_exercise_id),
    foreign key (session_trainee_id, session_pt_id, session_date, session_exercise_id)
        references dev.session_exercise (session_trainee_id, session_pt_id, session_date, exercise_id) on delete cascade
);

end work
