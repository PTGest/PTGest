begin work;
truncate
    dev.company,
    dev.company_pt,
    dev.exercise,
    dev.feedback,
    dev.password_reset_token,
    dev.personal_trainer,
    dev.pt_trainee,
    dev.refresh_token,
    dev.report,
    dev.session,
    dev.session_feedback,
    dev.token,
    dev.trainee,
    dev.trainee_data,
    dev.trainer_favorite_exercise,
    dev.trainer_favorite_workout,
    dev."user",
    dev.workout_exercise_feedback,
    dev.workout_plan,
    dev.workout_plan_exercise;

alter sequence dev.exercise_id_seq restart with 1;
alter sequence dev.feedback_id_seq restart with 1;
alter sequence dev.workout_plan_id_seq restart with 1;
end work;
