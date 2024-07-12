import { createRouter, createWebHistory, RouteRecordRaw } from "vue-router"
import Login from "../views/auth/Login.vue"
import About from "../views/utils/AboutPage.vue"
import Home from "../views/home/HomePage.vue"
import Signup from "../views/auth/Signup.vue"
import ForgetPassword from "../views/auth/ForgetPassword.vue"
import Error from "../views/utils/Error.vue"
import ResetPassword from "../views/auth/ResetPassword.vue"
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
import SessionContainer from "../views/user/TrainerViews/components/sessions/SessionContainer.vue"
import EditSessionDetails from "../views/user/TrainerViews/components/sessions/EditSessionDetails.vue"
import TraineeReports from "../views/user/TrainerViews/components/reports/TraineeReports.vue"
import AddReport from "../views/user/TrainerViews/components/reports/createReport.vue"
import Report from "../views/user/TrainerViews/components/reports/Report.vue"
import CancelSession from "../views/user/TrainerViews/components/sessions/CancelSession.vue"
import TraineeProfile from "../views/user/TrainerViews/components/trainees/TraineeProfile.vue"
import TraineeDataHistory from "../views/user/UserProfile/components/TraineeDataHistory.vue"
import TraineeDataHistoryDetails from "../views/user/UserProfile/components/TraineeDataHistoryDetails.vue"
import TraineeAddDataHistory from "../views/user/UserProfile/components/TraineeAddDataHistory.vue"
import Profile from "../views/user/TrainerViews/Profile.vue"
import { isSigned } from "../services/authServices/authServices.ts"
import EmailSuccessPage from "../views/utils/EmailSuccessPage.vue";

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
    //Company and Independent Trainer Views
    {
        path: "/trainees",
        name: "trainees",
        component: Students,
        meta: { requiresAuth: true, roleNeeded: ["COMPANY", "HIRED_TRAINER", "INDEPENDENT_TRAINER"], canEdit: ["COMPANY", "INDEPENDENT_TRAINER"] },
    },
    { path: "/trainers", name: "trainers", component: Trainers, meta: { requiresAuth: true, roleNeeded: ["COMPANY"] } },
    { path: "/trainees/:traineeId/:assignTrainer", name: "assignTrainer", component: AssignTrainer, meta: { requiresAuth: true, roleNeeded: ["COMPANY"] } },
    { path: "/register-trainees?trainee=:isTrainee", name: "registerTrainee", component: RegisterTrainee, meta: { requiresAuth: true, roleNeeded: ["COMPANY", "INDEPENDENT_TRAINER"] } },
    { path: "/register-trainers?trainee=:isTrainee", name: "registerTrainer", component: RegisterTrainee, meta: { requiresAuth: true, roleNeeded: ["COMPANY"] } },
    //Trainers Views
    { path: "/exercises", name: "exercises", component: Exercises, meta: { requiresAuth: true, roleNeeded: ["INDEPENDENT_TRAINER", "HIRED_TRAINER"] } },
    { path: "/exercises/create", name: "addExercise", component: AddExercise, meta: { requiresAuth: true, roleNeeded: ["INDEPENDENT_TRAINER", "HIRED_TRAINER"] } },
    { path: "/sets", name: "sets", component: Sets, meta: { requiresAuth: true, roleNeeded: ["INDEPENDENT_TRAINER", "HIRED_TRAINER"] } },
    { path: "/sets/create", name: "addSet", component: AddSet, meta: { requiresAuth: true, roleNeeded: ["INDEPENDENT_TRAINER", "HIRED_TRAINER"] } },
    { path: "/workouts", name: "workouts", component: Workouts, meta: { requiresAuth: true, roleNeeded: ["INDEPENDENT_TRAINER", "HIRED_TRAINER"] } },
    { path: "/workouts/create", name: "addWorkout", component: AddWorkout, meta: { requiresAuth: true, roleNeeded: ["INDEPENDENT_TRAINER", "HIRED_TRAINER"] } },

    { path: "/sets/setDetails/:setId", name: "setDetails", component: SetDetails, meta: { requiresAuth: true, roleNeeded: ["INDEPENDENT_TRAINER", "HIRED_TRAINER"] } },

    { path: "/sessions", name: "sessions", component: Sessions, meta: { requiresAuth: true, roleNeeded: ["INDEPENDENT_TRAINER", "HIRED_TRAINER", "TRAINEE"] } },
    { path: "/sessions/:traineeId", name: "traineeSessions", component: TraineeSessions, meta: { requiresAuth: true, roleNeeded: ["INDEPENDENT_TRAINER", "HIRED_TRAINER"] } },
    { path: "/sessions/:traineeId/create", name: "addTraineeSessions", component: AddTraineeSession, meta: { requiresAuth: true, roleNeeded: ["INDEPENDENT_TRAINER", "HIRED_TRAINER"] } },
    { path: "/sessions/session/:sessionId", name: " sessionDetails", component: SessionContainer, meta: { requiresAuth: true, roleNeeded: ["INDEPENDENT_TRAINER", "HIRED_TRAINER", "TRAINEE"] } },
    { path: "/sessions/session/:sessionId/edit", name: " editSessionDetails", component: EditSessionDetails, meta: { requiresAuth: true, roleNeeded: ["INDEPENDENT_TRAINER", "HIRED_TRAINER"] } },
    { path: "/sessions/:sessionId/cancel", name: "cancelSession", component: CancelSession, meta: { requiresAuth: true, roleNeeded: ["INDEPENDENT_TRAINER", "HIRED_TRAINER", "TRAINEE"] } },
    { path: "/reports/:traineeId", name: "traineeReports", component: TraineeReports, meta: { requiresAuth: true, roleNeeded: ["INDEPENDENT_TRAINER", "HIRED_TRAINER", "TRAINEE"] } },
    { path: "/reports/:traineeId/report/create", name: "addReport", component: AddReport, meta: { requiresAuth: true, roleNeeded: ["INDEPENDENT_TRAINER", "HIRED_TRAINER"] } },
    { path: "/reports/:traineeId/:reportId", name: "report", component: Report, meta: { requiresAuth: true, roleNeeded: ["INDEPENDENT_TRAINER", "HIRED_TRAINER", "TRAINEE"] } },
    { path: "/:traineeId/profile", name: "traineeProfile", component: TraineeProfile, meta: { requiresAuth: true, roleNeeded: ["INDEPENDENT_TRAINER", "HIRED_TRAINER", "TRAINEE"] } },

    { path: "/trainee/:traineeId/data-history", name: "dataHistory", component: TraineeDataHistory, meta: { requiresAuth: true, roleNeeded: ["INDEPENDENT_TRAINER", "HIRED_TRAINER", "TRAINEE"] } },
    {
        path: "/trainee/:traineeId/data-history/:dataId",
        name: "dataHistoryDetails",
        component: TraineeDataHistoryDetails,
        meta: { requiresAuth: true, roleNeeded: ["INDEPENDENT_TRAINER", "HIRED_TRAINER", "TRAINEE"] },
    },
    { path: "/trainee/:traineeId/data-history/create", name: "addDataHistory", component: TraineeAddDataHistory, meta: { requiresAuth: true, roleNeeded: ["INDEPENDENT_TRAINER", "HIRED_TRAINER"] } },

    { path: "/profile", name: "profile", component: Profile, meta: { requiresAuth: true, roleNeeded: ["INDEPENDENT_TRAINER", "HIRED_TRAINER"] } },
    //Error Views
    { path: "/error", name: "error", component: Error },
    {path: "/:pathMatch(.*)*", redirect: {name: "notFound"}, name: "not-found"},

        {path: "/email-sucess",name: "emailSucessPage", component:EmailSuccessPage},

]

const router = createRouter({
    routes,
    history: createWebHistory(),
})

router.beforeEach(async (to) => {
    if (to.meta.requiresAuth) {
        await isSigned()
        if (!store.getters.isLogged) {
            return { name: "login" }
        }
    }
    if ((to.name === "login" || to.name === "signup") && store.getters.isLogged) {
        return { name: "home" }
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
