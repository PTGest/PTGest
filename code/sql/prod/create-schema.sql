begin work;

create schema if not exists "prod";

create extension if not exists "uuid-ossp";

create type prod.role as enum ('COMPANY', 'HIRED_TRAINER', 'INDEPENDENT_TRAINER', 'TRAINEE');
create type prod.gender as enum ('MALE', 'FEMALE', 'OTHER', 'UNDEFINED');
create type prod.set_type as enum ('SIMPLESET', 'DROPSET', 'SUPERSET');
create type prod.muscle_group as enum ('BICEPS', 'CHEST', 'CORE', 'FOREARMS', 'FULL_BODY',
    'GLUTES', 'HAMSTRINGS', 'HIP_GROIN', 'LOWER_BACK', 'LOWER_BODY', 'LOWER_LEG', 'MID_BACK',
    'QUADS', 'SHOULDERS', 'TRIPEPS', 'UPPER_BACK_NECK', 'UPPER_BODY');
create type prod.modality as enum ('BODYWEIGHT', 'WEIGHTLIFT', 'RUNNING', 'CYCLING', 'OTHER');
create type prod.session_type as enum ('TRAINER_GUIDED', 'PLAN_BASED');
create type prod.source as enum ('TRAINER', 'TRAINEE');

create table if not exists prod."user"
(
    id            uuid default uuid_generate_v4() primary key,
    name          varchar(40) check (name <> '')                                    not null,
    email         varchar(50)
        check ( email ~ '^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$' ) unique not null,
    password_hash varchar(256) check (password_hash <> '')                          not null,
    role          prod.role                                                          not null,
    active        boolean default true                                              not null
);

create table if not exists prod.token_version
(
    user_id  uuid primary key references prod."user" (id) on delete cascade,
    version int default 1 not null
);

create table if not exists prod.forget_password_request
(
    token_hash varchar(256) check (token_hash <> '') primary key,
    user_id    uuid references prod."user" (id) on delete cascade,
    expiration timestamp check ( expiration > now() ) not null
);

-- User's personal data
create table if not exists prod.company
(
    id uuid primary key references prod."user" (id) on delete cascade
);

create table if not exists prod.trainer
(
    id           uuid primary key references prod."user" (id) on delete cascade,
    gender       prod.gender not null,
    phone_number varchar(20) check ( phone_number ~ '^[+]{1}(?:[0-9\\-\\(\\)\\/\\.]\s?){6,15}[0-9]{1}$' )
);

create table if not exists prod.trainee
(
    id           uuid primary key references prod."user" (id) on delete cascade,
    gender       prod.gender not null,
    birthdate    date       not null,
    phone_number varchar(20) check ( phone_number ~ '^[+]{1}(?:[0-9\\-\\(\\)\\/\\.]\s?){6,15}[0-9]{1}$' )
);

-- User's relationships
create table if not exists prod.company_trainer
(
    company_id uuid references prod.company (id) on delete cascade,
    trainer_id uuid references prod.trainer (id) on delete cascade,
    capacity   int check ( capacity > 0 ) not null,
    primary key (company_id, trainer_id)
);

create table if not exists prod.company_trainee
(
    company_id uuid references prod.company (id) on delete cascade,
    trainee_id uuid references prod.trainee (id) on delete cascade,
    primary key (company_id, trainee_id)
);

create table if not exists prod.trainer_trainee
(
    trainer_id uuid references prod.trainer (id) on delete cascade,
    trainee_id uuid references prod.trainee (id) on delete cascade,
    primary key (trainer_id, trainee_id)
);

create table if not exists prod.trainee_data
(
    id         serial primary key,
    trainee_id uuid references prod.trainee (id) on delete cascade,
    date       date  not null,
    body_data  jsonb not null
);

create table if not exists prod.report
(
    id         serial primary key,
    trainee_id uuid references prod.trainee (id) on delete cascade,
    date       date                  not null,
    report     text                  not null,
    visibility boolean default false not null
);

create table if not exists prod.report_trainer
(
    report_id   int references prod.report (id) on delete cascade,
    trainer_id uuid references prod.trainer (id) on delete cascade,
    primary key (report_id, trainer_id)
);

-- Trainee's workout plan
create table if not exists prod.workout
(
    id           serial primary key,
    name         varchar(50)        not null,
    description  text,
    muscle_group prod.muscle_group[] not null
);

create table if not exists prod.set
(
    id    serial primary key,
    name  varchar(50)  not null,
    notes text,
    type  prod.set_type not null
);

create table if not exists prod.exercise
(
    id           serial primary key,
    name         varchar(50)        not null,
    description  text,
    muscle_group prod.muscle_group[] not null,
    modality     prod.modality       not null,
    ref          varchar(256)
);

create table if not exists prod.session
(
    id         serial primary key,
    trainee_id uuid references prod.trainee (id) on delete cascade,
    workout_id int references prod.workout (id) on delete cascade,
    begin_date timestamp check ( begin_date < end_date and begin_date > now() ) not null,
    end_date   timestamp check ( end_date > begin_date and end_date > now() ) ,
    location   varchar(50),
    type       prod.session_type                                                 not null,
    notes      text
);

create table if not exists prod.cancelled_session
(
    session_id int primary key references prod.session (id) on delete cascade,
    source     prod.source not null,
    reason     text
);

create table if not exists prod.exercise_company
(
    company_id  uuid references prod.company (id) on delete cascade,
    exercise_id int references prod.exercise (id) on delete cascade,
    primary key (company_id, exercise_id)
);

create table if not exists prod.exercise_trainer
(
    trainer_id  uuid references prod.trainer (id) on delete cascade,
    exercise_id int references prod.exercise (id) on delete cascade,
    primary key (trainer_id, exercise_id)
);

create table if not exists prod.set_trainer
(
    trainer_id uuid references prod.trainer (id) on delete cascade,
    set_id     int references prod.set (id) on delete cascade,
    primary key (trainer_id, set_id)
);

create table if not exists prod.workout_trainer
(
    trainer_id uuid references prod.trainer (id) on delete cascade,
    workout_id int references prod.workout (id) on delete cascade,
    primary key (trainer_id, workout_id)
);

create table if not exists prod.session_trainer
(
    trainer_id uuid references prod.trainer (id) on delete cascade,
    session_id int references prod.session (id) on delete cascade,
    primary key (trainer_id, session_id)
);

create table if not exists prod.workout_set
(
    order_id   int not null,
    workout_id int references prod.workout (id) on delete cascade,
    set_id     int references prod.set (id) on delete cascade,
    primary key (order_id, workout_id, set_id)
);

create table if not exists prod.set_exercise
(
    order_id    int   not null,
    set_id      int references prod.set (id) on delete cascade,
    exercise_id int references prod.exercise (id) on delete cascade,
    details     jsonb not null,
    primary key (order_id, set_id, exercise_id, details)
);

create table if not exists prod.trainer_favorite_workout
(
    trainer_id uuid references prod.trainer (id) on delete cascade,
    workout_id int references prod.workout (id) on delete cascade,
    primary key (trainer_id, workout_id)
);

create table if not exists prod.trainer_favorite_set
(
    trainer_id uuid references prod.trainer (id) on delete cascade,
    set_id     int references prod.set (id) on delete cascade,
    primary key (trainer_id, set_id)
);

create table if not exists prod.trainer_favorite_exercise
(
    trainer_id  uuid references prod.trainer (id) on delete cascade,
    exercise_id int references prod.exercise (id) on delete cascade,
    primary key (trainer_id, exercise_id)
);

create table if not exists prod.feedback
(
    id       serial primary key,
    source   prod.source                       not null,
    feedback text                             not null,
    date     timestamp check ( date < now() ) not null
);

create table if not exists prod.session_feedback
(
    feedback_id int references prod.feedback (id) on delete cascade,
    session_id  int references prod.session (id) on delete cascade,
    primary key (feedback_id, session_id)
);

create table if not exists prod.session_set_feedback
(
    feedback_id  int references prod.feedback (id) on delete cascade,
    session_id   int,
    workout_id   int,
    set_order_id int,
    set_id       int,
    foreign key (set_order_id, workout_id, set_id)
        references prod.workout_set (order_id, workout_id, set_id) on delete cascade,
    primary key (feedback_id, session_id, workout_id, set_order_id, set_id)
);

end work
