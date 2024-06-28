import { createRouter, createWebHistory, RouteRecordRaw } from "vue-router"
import Login from "../views/auth/login/Login.vue"
import About from ".././views/AboutPage.vue"
import Home from "../views/home/HomePage.vue"
import Signup from "../views/auth/signUp/Signup.vue"
import ForgetPassword from "../views/auth/forgetPassword/ForgetPassword.vue"
import Error from "../views/Error.vue"
import ResetPassword from "../views/auth/resetPassword/ResetPassword.vue"
// import UserProfile from "../views/user/UserProfile/UserProfile.vue"
import RegisterTrainee from "../views/user/UserRegister/RegisterTrainee.vue"
import Students from "../views/user/TrainerViews/Trainees.vue"
import Trainers from "../views/user/CompaniesViews/Trainers.vue"
import RBAC from "../services/utils/RBAC/RBAC.ts"
import store from "../store"
import AssignTrainer from "../views/user/CompaniesViews/AssignTrainer.vue"
import Exercises from "../views/user/TrainerViews/Exercises.vue"
import Sets from "../views/user/TrainerViews/Sets.vue"
import Workouts from "../views/user/TrainerViews/Workouts.vue"
import AddSet from "../views/user/TrainerViews/components/sets/AddSet.vue"
import AddExercise from "../views/user/TrainerViews/components/exercises/AddExercise.vue"
import AddWorkout from "../views/user/TrainerViews/components/workouts/AddWorkout.vue"
import SetDetails from "../views/user/TrainerViews/components/sets/SetDetails.vue"
import Sessions from "../views/user/TrainerViews/Sessions.vue"
import TraineeSessions from "../views/user/TrainerViews/components/sessions/TraineeSessions.vue"
import AddTraineeSession from "../views/user/TrainerViews/components/sessions/AddTraineeSession.vue"
import SessionDetails from "../views/user/TrainerViews/components/sessions/SessionDetails.vue"
import EditSessionDetails from "../views/user/TrainerViews/components/sessions/EditSessionDetails.vue"
import TraineeReports from "../views/user/TrainerViews/components/reports/TraineeReports.vue";
import AddReport from "../views/user/TrainerViews/components/reports/createReport.vue";
import Report from "../views/user/TrainerViews/components/reports/Report.vue";
import isSigned from "../services/AuthServices/isSigned.ts";
import CancelSession from "../views/user/TrainerViews/components/sessions/CancelSession.vue";
import TraineeProfile from "../views/user/TrainerViews/components/trainees/TraineeProfile.vue";


const routes: RouteRecordRaw[] = [
    { path: "/", name: "home", component: Home, meta: { requiresAuth: false } },
    { path: "/about", name: "about", component: About, meta: { requiresAuth: false } },
    //Auth Views
    { path: "/signup", name: "signup", component: Signup, meta: { requiresAuth: false } },
    { path: "/login", name: "login", component: Login, meta: { requiresAuth: false } },
    { path: "/forgetPassword", name: "forgetPassword", component: ForgetPassword, meta: { requiresAuth: false } },
    {
        path: "/resetPassword/:token",
        name: "resetPassword",
        component: ResetPassword,
        props: (route) => ({ token: route.params.token }),
        meta: { requiresAuth: false },
    },
    //UserServices Views
    // { path: "/user/profile/:userId", name: "userProfile", component: UserProfile, props: true, meta: { requiresAuth: true,
    //         roleNeeded : ['TRAINEE', 'COMPANY', 'INDEPENDENT_TRAINER', 'HIRED_TRAINER'] } },
    //Company and Independent Trainer Views
    {
        path: "/trainees",
        name: "trainees",
        component: Students,
        meta: { requiresAuth: true, roleNeeded: ["COMPANY", "HIRED_TRAINER", "INDEPENDENT_TRAINER"], canEdit: ["COMPANY", "INDEPENDENT_TRAINER"] },
    },
    { path: "/trainers", name: "trainers", component: Trainers, meta: { requiresAuth: true, roleNeeded: ["COMPANY"] } },
    { path: "/trainees/:traineeId/:assignTrainer", name: "assignTrainer", component: AssignTrainer, meta: { requiresAuth: true, roleNeeded: ["COMPANY"] } },
    { path: "/register-trainees/:isTrainee", name: "registerTrainee", component: RegisterTrainee, meta: { requiresAuth: true, roleNeeded: ["COMPANY", "INDEPENDENT_TRAINER"] } },
    { path: "/register-trainers/:isTrainee", name: "registerTrainer", component: RegisterTrainee, meta: { requiresAuth: true, roleNeeded: ["COMPANY"] } },
    //Trainers Views
    { path: "/exercises", name: "exercises", component: Exercises, meta: { requiresAuth: true, roleNeeded: ["INDEPENDENT_TRAINER", "HIRED_TRAINER"] } },
    { path: "/sets", name: "sets", component: Sets, meta: { requiresAuth: true, roleNeeded: ["INDEPENDENT_TRAINER", "HIRED_TRAINER"] } },
    { path: "/workouts", name: "workouts", component: Workouts, meta: { requiresAuth: true, roleNeeded: ["INDEPENDENT_TRAINER", "HIRED_TRAINER"] } },
    // { path: "/workouts/:workoutId", name: "workouts", component: WorkoutDetails, meta: { requiresAuth: true,
    //         roleNeeded : ['TRAINER', 'HIRED_TRAINER']}
    // },
    { path: "/sets/custom-set", name: "addSet", component: AddSet, meta: { requiresAuth: true, roleNeeded: ["INDEPENDENT_TRAINER", "HIRED_TRAINER"] } },
    { path: "/workouts/add-exercise", name: "addExercise", component: AddExercise, meta: { requiresAuth: true, roleNeeded: ["INDEPENDENT_TRAINER", "HIRED_TRAINER"] } },
    { path: "/workouts/add-workout", name: "addWorkout", component: AddWorkout, meta: { requiresAuth: true, roleNeeded: ["INDEPENDENT_TRAINER", "HIRED_TRAINER"] } },

    { path: "/sets/setDetails/:setId", name: "setDetails", component: SetDetails, meta: { requiresAuth: true, roleNeeded: ["INDEPENDENT_TRAINER", "HIRED_TRAINER"] } },

    { path: "/sessions", name: "sessions", component: Sessions, meta: { requiresAuth: true, roleNeeded: ["INDEPENDENT_TRAINER", "HIRED_TRAINER"] } },
    { path: "/trainer/:traineeId/profile", name: "traineeProfile", component: TraineeProfile, meta: { requiresAuth: true, roleNeeded: ["INDEPENDENT_TRAINER", "HIRED_TRAINER"] } },
    { path: "/sessions/:traineeId", name: "traineeSessions", component: TraineeSessions, meta: { requiresAuth: true, roleNeeded: ["INDEPENDENT_TRAINER", "HIRED_TRAINER"] } },
    { path: "/sessions/:traineeId/add-session", name: "addTraineeSessions", component: AddTraineeSession, meta: { requiresAuth: true, roleNeeded: ["INDEPENDENT_TRAINER", "HIRED_TRAINER"] } },
    { path: "/sessions/session/:sessionId", name: " sessionDetails", component: SessionDetails, meta: { requiresAuth: true, roleNeeded: ["INDEPENDENT_TRAINER", "HIRED_TRAINER"] } },
    { path: "/sessions/session/:sessionId/edit", name: " editSessionDetails", component: EditSessionDetails, meta: { requiresAuth: true, roleNeeded: ["INDEPENDENT_TRAINER", "HIRED_TRAINER"] } },
    { path: "/sessions/:sessionId/cancel", name: "cancelSession", component: CancelSession, meta: { requiresAuth: true, roleNeeded: ["INDEPENDENT_TRAINER", "HIRED_TRAINER", "TRAINEE"] } },
    { path: "/reports/:traineeId", name: "traineeReports", component: TraineeReports, meta: { requiresAuth: true, roleNeeded: ["INDEPENDENT_TRAINER", "HIRED_TRAINER", "TRAINEE"] } },
    { path: "/reports/:traineeId/addReport", name: "addReport", component: AddReport, meta: { requiresAuth: true, roleNeeded: ["INDEPENDENT_TRAINER", "HIRED_TRAINER"] } },
    { path: "/reports/:traineeId/:reportId", name: "report", component: Report, meta: { requiresAuth: true, roleNeeded: ["INDEPENDENT_TRAINER", "HIRED_TRAINER", "TRAINEE"] } },
    //Error Views
    { path: "/error", name: "error", component: Error },
    //{ path: "/:pathMatch(.*)*", redirect: { name: "error" }, name: "not-found" },
]

const router = createRouter({
    routes,
    history: createWebHistory(),
})

router.beforeEach(async (to, from) => {

    if (to.meta.requiresAuth) {
        await isSigned()
        if (!store.getters.isLogged) {
            return {name: "login"}
        }
    }
    if ((to.name === "login" || to.name === 'signup') && store.getters.isLogged) {
        return {name: "home"}
    }
})

router.beforeEach((to) => {
    if (to.meta.roleNeeded) {
        const roles = to.meta.roleNeeded as string[]
        const rbac = RBAC
        const userRole = rbac.getUserRole()
        if (!roles.includes(userRole)) {
            return { name: "error" }
        }
    }
})

export default router
