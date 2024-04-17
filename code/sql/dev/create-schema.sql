begin work;

create schema if not exists "dev";

create extension if not exists "uuid-ossp";

create type dev.role as enum ('COMPANY', 'HIRED_TRAINER', 'INDEPENDENT_TRAINER', 'TRAINEE');

create table if not exists dev."user"
(
    id            uuid default uuid_generate_v4() primary key,
    name          varchar(40) check ("user".name <> '')                             not null,
    email         varchar(50)
        check ( email ~ '^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$' ) unique not null,
    password_hash varchar(256) check ("user".password_hash <> '')                   not null,
    role          dev.role                                                          not null
);

create table if not exists dev.token
(
    token_hash varchar(256) primary key,
    user_id    uuid references dev."user" (id) on delete cascade,
    expiration timestamp check ( expiration > now() ) not null
);

create table if not exists dev.password_reset_token
(
    token_hash varchar(256) primary key references dev.token (token_hash) on delete cascade
);

create table if not exists dev.refresh_token
(
    token_hash varchar(256) primary key references dev.token (token_hash) on delete cascade
);

create table if not exists dev.company
(
    id uuid primary key references dev."user" (id) on delete cascade
);

create type dev.gender as enum ('M', 'F', 'O', 'U');

create table if not exists dev.personal_trainer
(
    id      uuid primary key references dev."user" (id) on delete cascade,
    gender  dev.gender not null,
    contact varchar(20) check ( contact ~ '^[+]{1}(?:[0-9\\-\\(\\)\\/\\.]\s?){6,15}[0-9]{1}$' )
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
    gender    dev.gender not null,
    birthdate date       not null,
    contact   varchar(20) check ( contact ~ '^[+]{1}(?:[0-9\\-\\(\\)\\/\\.]\s?){6,15}[0-9]{1}$' )
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

create table if not exists dev.workout_plan
(
    id    serial primary key,
    pt_id uuid references dev.personal_trainer (id) on delete cascade
);

create type dev.exercise_category as enum ('A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J');

create table if not exists dev.exercise
(
    id          serial primary key,
    name        varchar(50)           not null,
    description text,
    category    dev.exercise_category not null,
    url         varchar(256)
);

create table if not exists dev.exercise_company
(
    company_id  uuid references dev.company (id) on delete cascade,
    exercise_id int references dev.exercise (id) on delete cascade,
    primary key (company_id, exercise_id)
);

create table if not exists dev.exercise_pt
(
    pt_id       uuid references dev.personal_trainer (id) on delete cascade,
    exercise_id int references dev.exercise (id) on delete cascade,
    primary key (pt_id, exercise_id)
);

create table if not exists dev.workout_plan_exercise
(
    workout_plan_id int references dev.workout_plan (id) on delete cascade,
    exercise_id     int references dev.exercise (id) on delete cascade,
    notes           text,
    details         jsonb not null,
    primary key (workout_plan_id, exercise_id)
);

create type dev.session_category as enum ('P', 'A');

create table if not exists dev.session
(
    trainee_id uuid references dev.trainee (id) on delete cascade,
    workout_id int references dev.workout_plan (id) on delete cascade,
    date       timestamp            not null,
    category   dev.session_category not null,
    notes      text,
    primary key (trainee_id, workout_id, date)
);

create table if not exists dev.trainer_favorite_exercise
(
    pt_id       uuid references dev.personal_trainer (id) on delete cascade,
    exercise_id int references dev.exercise (id) on delete cascade,
    primary key (pt_id, exercise_id)
);

create table if not exists dev.trainer_favorite_workout
(
    pt_id           uuid references dev.personal_trainer (id) on delete cascade,
    workout_plan_id int references dev.workout_plan (id) on delete cascade,
    primary key (pt_id, workout_plan_id)
);

create type dev.source as enum ('T', 'P');

create table if not exists dev.feedback
(
    id       serial primary key,
    source   dev.source not null,
    feedback text
);

create table if not exists dev.session_feedback
(
    feedback_id        int references dev.feedback (id) on delete cascade,
    session_trainee_id uuid,
    session_workout_id int,
    session_date       timestamp,
    primary key (feedback_id, session_trainee_id, session_workout_id, session_date),
    foreign key (session_trainee_id, session_workout_id, session_date)
        references dev.session (trainee_id, workout_id, date) on delete cascade
);

create table if not exists dev.workout_exercise_feedback
(
    feedback_id     int references dev.feedback (id) on delete cascade,
    workout_plan_id int,
    exercise_id     int,
    primary key (feedback_id, workout_plan_id, exercise_id),
    foreign key (workout_plan_id, exercise_id)
        references dev.workout_plan_exercise (workout_plan_id, exercise_id) on delete cascade
);

end work
