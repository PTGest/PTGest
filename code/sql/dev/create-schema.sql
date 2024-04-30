begin work;

create schema if not exists "dev";

create extension if not exists "uuid-ossp";

create type dev.role as enum ('COMPANY', 'HIRED_TRAINER', 'INDEPENDENT_TRAINER', 'TRAINEE');
create type dev.gender as enum ('MALE', 'FEMALE', 'OTHER', 'UNDEFINED');
create type dev.set_type as enum ('NORMAL', 'DROPSET', 'SUPERSET');
-- TODO: to be changed
create type dev.exercise_category as enum ('CARDIO', 'ARMS', 'LEGS', 'BACK', 'CHEST', 'SHOULDERS', 'ABS', 'OTHER');
create type dev.session_category as enum ('P', 'A');
create type dev.source as enum ('T', 'P');

create table if not exists dev."user"
(
    id            uuid default uuid_generate_v4() primary key,
    name          varchar(40) check (name <> '')                                    not null,
    email         varchar(50)
        check ( email ~ '^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$' ) unique not null,
    password_hash varchar(256) check (password_hash <> '')                          not null,
    role          dev.role                                                          not null
);

create table if not exists dev.token
(
    token_hash varchar(256) check (token_hash <> '') primary key,
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

create table if not exists dev.trainer
(
    id           uuid primary key references dev."user" (id) on delete cascade,
    gender       dev.gender not null,
    phone_number varchar(20) check ( phone_number ~ '^[+]{1}(?:[0-9\\-\\(\\)\\/\\.]\s?){6,15}[0-9]{1}$' )
);

create table if not exists dev.company_trainer
(
    company_id uuid references dev.company (id) on delete cascade,
    trainer_id uuid references dev.trainer (id) on delete cascade,
    capacity   int check ( capacity > 0 ) not null,
    primary key (company_id, trainer_id)
);

create table if not exists dev.trainee
(
    id           uuid primary key references dev."user" (id) on delete cascade,
    gender       dev.gender not null,
    birthdate    date       not null,
    phone_number varchar(20) check ( phone_number ~ '^[+]{1}(?:[0-9\\-\\(\\)\\/\\.]\s?){6,15}[0-9]{1}$' )
);

create table if not exists dev.trainer_trainee
(
    trainer_id uuid references dev.trainer (id) on delete cascade,
    trainee_id uuid references dev.trainee (id) on delete cascade,
    primary key (trainer_id, trainee_id)
);

-- TODO: to be checked

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
    trainer_id uuid references dev.trainer (id) on delete cascade,
    date       date                  not null,
    report     text                  not null,
    visibility boolean default false not null,
    primary key (trainee_id, trainer_id, date)
);

-- Trainee's workout plan
create table if not exists dev.workout
(
    id         serial primary key,
    trainer_id uuid references dev.trainer (id) on delete cascade,
    name       varchar(50) not null,
    description text
);

create table if not exists dev.set
(
    id      serial primary key,
    name   varchar(50) not null,
    notes   text,
    type    dev.set_type not null,
    details jsonb        not null
);

create table if not exists dev.exercise
(
    id          serial primary key,
    name        varchar(50)           not null,
    description text,
    category    dev.exercise_category not null,
    ref         varchar(256)
);

create table if not exists dev.session
(
    trainee_id uuid references dev.trainee (id) on delete cascade,
    workout_id int references dev.workout (id) on delete cascade,
    date       timestamp            not null,
    category   dev.session_category not null,
    notes      text,
    primary key (trainee_id, workout_id, date)
);

create table if not exists dev.exercise_company
(
    company_id  uuid references dev.company (id) on delete cascade,
    exercise_id int references dev.exercise (id) on delete cascade,
    primary key (company_id, exercise_id)
);

create table if not exists dev.exercise_trainer
(
    trainer_id  uuid references dev.trainer (id) on delete cascade,
    exercise_id int references dev.exercise (id) on delete cascade,
    primary key (trainer_id, exercise_id)
);

create table if not exists dev.set_trainer
(
    trainer_id  uuid references dev.trainer (id) on delete cascade,
    set_id int references dev.exercise (id) on delete cascade,
    primary key (trainer_id, set_id)
);

create table if not exists dev.workout_trainer
(
    trainer_id  uuid references dev.trainer (id) on delete cascade,
    workout_id int references dev.exercise (id) on delete cascade,
    primary key (trainer_id, workout_id)
);


create table if not exists dev.workout_set
(
    workout_id int references dev.workout (id) on delete cascade,
    set_id     int references dev.set (id) on delete cascade,
    primary key (workout_id, set_id)
);

create table if not exists dev.set_exercise
(
    set_id      int references dev.set (id) on delete cascade,
    exercise_id int references dev.exercise (id) on delete cascade,
    primary key (set_id, exercise_id)
);

create table if not exists dev.trainer_favorite_workout
(
    trainer_id uuid references dev.trainer (id) on delete cascade,
    workout_id int references dev.workout (id) on delete cascade,
    primary key (trainer_id, workout_id)
);

create table if not exists dev.trainer_favorite_set
(
    trainer_id uuid references dev.trainer (id) on delete cascade,
    set_id     int references dev.set (id) on delete cascade,
    primary key (trainer_id, set_id)
);

create table if not exists dev.trainer_favorite_exercise
(
    trainer_id  uuid references dev.trainer (id) on delete cascade,
    exercise_id int references dev.exercise (id) on delete cascade,
    primary key (trainer_id, exercise_id)
);

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

-- TODO: implement set feedback

end work
