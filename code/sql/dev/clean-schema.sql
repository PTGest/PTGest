begin work;
truncate
    dev.company_pt,
    dev.company,
    dev.pt_trainee,
    dev.trainee_data,
    dev.report,
    dev.trainer_favorite_exercise,
    dev.session_feedback,
    dev.session_exercise_feedback,
    dev.session_exercise,
    dev.session,
    dev.trainee,
    dev.exercise,
    dev.personal_trainer,
    dev."user",
    dev.refresh_token,
    dev.feedback cascade;
end work;
