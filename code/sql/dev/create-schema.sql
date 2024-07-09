begin work;

create schema if not exists "dev";

create extension if not exists "uuid-ossp";

create type dev.role as enum ('COMPANY', 'HIRED_TRAINER', 'INDEPENDENT_TRAINER', 'TRAINEE');
create type dev.gender as enum ('MALE', 'FEMALE', 'OTHER', 'UNDEFINED');
create type dev.set_type as enum ('SIMPLESET', 'DROPSET', 'SUPERSET');
create type dev.muscle_group as enum ('BICEPS', 'CHEST', 'CORE', 'FOREARMS', 'FULL_BODY',
    'GLUTES', 'HAMSTRINGS', 'HIP_GROIN', 'LOWER_BACK', 'LOWER_BODY', 'LOWER_LEG', 'MID_BACK',
    'QUADS', 'SHOULDERS', 'TRIPEPS', 'UPPER_BACK_NECK', 'UPPER_BODY');
create type dev.modality as enum ('BODYWEIGHT', 'WEIGHTLIFT', 'RUNNING', 'CYCLING', 'OTHER');
create type dev.session_type as enum ('TRAINER_GUIDED', 'PLAN_BASED');
create type dev.source_feedback as enum ('TRAINER', 'TRAINEE');
create type dev.source_cancel_session as enum ('TRAINER', 'TRAINEE', 'COMPANY');

create table if not exists dev."user"
(
    id            uuid    default uuid_generate_v4() primary key,
    name          varchar(40) check (name <> '')                                    not null,
    email         varchar(50)
        check ( email ~ '^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$' ) unique not null,
    password_hash varchar(256) check (password_hash <> '')                          not null,
    role          dev.role                                                          not null,
    active        boolean default true                                              not null
);

create table if not exists dev.token_version
(
    user_id uuid primary key references dev."user" (id) on delete cascade,
    version int default 1 not null
);

create table if not exists dev.forget_password_request
(
    token_hash varchar(256) check (token_hash <> '') primary key,
    user_id    uuid references dev."user" (id) on delete cascade,
    expiration timestamp not null
);

-- User's personal data
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

create table if not exists dev.trainee
(
    id           uuid primary key references dev."user" (id) on delete cascade,
    gender       dev.gender not null,
    birthdate    date       not null,
    phone_number varchar(20) check ( phone_number ~ '^[+]{1}(?:[0-9\\-\\(\\)\\/\\.]\s?){6,15}[0-9]{1}$' )
);

-- User's relationships
create table if not exists dev.company_trainer
(
    company_id uuid references dev.company (id) on delete cascade,
    trainer_id uuid references dev.trainer (id) on delete cascade,
    capacity   int check ( capacity > 0 ) not null,
    primary key (company_id, trainer_id)
);

create table if not exists dev.company_trainee
(
    company_id uuid references dev.company (id) on delete cascade,
    trainee_id uuid references dev.trainee (id) on delete cascade,
    primary key (company_id, trainee_id)
);

create table if not exists dev.trainer_trainee
(
    trainer_id uuid references dev.trainer (id) on delete cascade,
    trainee_id uuid references dev.trainee (id) on delete cascade,
    primary key (trainer_id, trainee_id)
);

create table if not exists dev.trainee_data
(
    id         serial primary key,
    trainee_id uuid references dev.trainee (id) on delete cascade,
    date       date  not null,
    body_data  jsonb not null
);

create table if not exists dev.report
(
    id         serial primary key,
    trainee_id uuid references dev.trainee (id) on delete cascade,
    date       date                  not null,
    report     text                  not null,
    visibility boolean default false not null
);

create table if not exists dev.report_trainer
(
    report_id  int references dev.report (id) on delete cascade,
    trainer_id uuid references dev.trainer (id) on delete cascade,
    primary key (report_id, trainer_id)
);

-- Trainee's workout plan
create table if not exists dev.workout
(
    id           serial primary key,
    name         varchar(50)        not null,
    description  text,
    muscle_group dev.muscle_group[] not null
);

create table if not exists dev.set
(
    id    serial primary key,
    name  varchar(50)  not null,
    notes text,
    type  dev.set_type not null
);

create table if not exists dev.exercise
(
    id           serial primary key,
    name         varchar(50)        not null,
    description  text,
    muscle_group dev.muscle_group[] not null,
    modality     dev.modality       not null,
    ref          varchar(256)
);

create table if not exists dev.session
(
    id         serial primary key,
    trainee_id uuid references dev.trainee (id) on delete cascade,
    workout_id int references dev.workout (id) on delete cascade,
    begin_date timestamp check ( begin_date < end_date ) not null,
    end_date   timestamp check ( end_date > begin_date ),
    location   varchar(50),
    type       dev.session_type                          not null,
    notes      text
);

create table if not exists dev.cancelled_session
(
    session_id int primary key references dev.session (id) on delete cascade,
    source     dev.source_cancel_session not null,
    reason     text
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
    trainer_id uuid references dev.trainer (id) on delete cascade,
    set_id     int references dev.set (id) on delete cascade,
    primary key (trainer_id, set_id)
);

create table if not exists dev.workout_trainer
(
    trainer_id uuid references dev.trainer (id) on delete cascade,
    workout_id int references dev.workout (id) on delete cascade,
    primary key (trainer_id, workout_id)
);

create table if not exists dev.session_trainer
(
    trainer_id uuid references dev.trainer (id) on delete cascade,
    session_id int references dev.session (id) on delete cascade,
    primary key (trainer_id, session_id)
);

create table if not exists dev.workout_set
(
    order_id   int not null,
    workout_id int references dev.workout (id) on delete cascade,
    set_id     int references dev.set (id) on delete cascade,
    primary key (order_id, workout_id, set_id)
);

create table if not exists dev.set_exercise
(
    order_id    int   not null,
    set_id      int references dev.set (id) on delete cascade,
    exercise_id int references dev.exercise (id) on delete cascade,
    details     jsonb not null,
    primary key (order_id, set_id, exercise_id, details)
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
    source   dev.source_feedback not null,
    feedback text                not null,
    date     timestamp           not null
);

create table if not exists dev.session_feedback
(
    feedback_id int references dev.feedback (id) on delete cascade,
    session_id  int references dev.session (id) on delete cascade,
    primary key (feedback_id, session_id)
);

create table if not exists dev.session_set_feedback
(
    feedback_id  int references dev.feedback (id) on delete cascade,
    session_id   int,
    workout_id   int,
    set_order_id int,
    set_id       int,
    foreign key (set_order_id, workout_id, set_id)
        references dev.workout_set (order_id, workout_id, set_id) on delete cascade,
    primary key (feedback_id, session_id, workout_id, set_order_id, set_id)
);

end work
