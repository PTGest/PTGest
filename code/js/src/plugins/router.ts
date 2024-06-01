import { createRouter, createWebHistory, RouteRecordRaw } from "vue-router"
import Login from "../views/auth/login/Login.vue"
import About from ".././views/AboutPage.vue"
import Home from "../views/home/HomePage.vue"
import Signup from "../views/auth/signUp/Signup.vue"
import ForgetPassword from "../views/auth/forgetPassword/ForgetPassword.vue"
import Error from "../views/Error.vue"
import ResetPassword from "../views/auth/resetPassword/ResetPassword.vue"
import UserProfile from "../views/user/UserProfile/UserProfile.vue"
import RegisterTrainee from "../views/user/UserRegister/RegisterTrainee.vue"
import Students from "../views/user/IndependentTrainerViews/Trainees.vue"
import Trainers from "../views/user/CompaniesViews/Trainers.vue";
import RBAC from "../services/utils/RBAC/RBAC.ts";
import store from "../store";
import AssignTrainer from "../views/user/CompaniesViews/AssignTrainer.vue";
import Exercises from "../views/user/TrainerViews/Exercises.vue";
import Sets from "../views/user/TrainerViews/Sets.vue";


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
    { path: "/user/profile/:userId", name: "userProfile", component: UserProfile, props: true, meta: { requiresAuth: true,
            roleNeeded : ['TRAINEE', 'COMPANY', 'TRAINER', 'HIRED_TRAINER'] } },
    //Company and Independent Trainer Views
    { path: "/trainees", name: "trainees", component: Students, meta: { requiresAuth: true ,
        roleNeeded : ['COMPANY', 'HIRED_TRAINER', 'INDEPENDENT_TRAINER'], canEdit:['COMPANY', 'INDEPENDENT_TRAINER']}
    },
    { path: "/trainers", name: "trainers", component: Trainers, meta: { requiresAuth: true,
            roleNeeded : ['COMPANY']}
    },
    { path: "/trainee/:traineeId/:assignTrainer", name: "assignTrainer", component: AssignTrainer, meta: { requiresAuth: true,
            roleNeeded : ['COMPANY']}
    },
    { path: "/register-trainee/:isTrainee", name: "registerTrainee", component: RegisterTrainee, meta: { requiresAuth: true,
            roleNeeded : ['COMPANY', 'TRAINER']}
    },
    { path: "/register-trainers/:isTrainee", name: "registerTrainer", component: RegisterTrainee, meta: { requiresAuth: true,
        roleNeeded : ['COMPANY']}
    },
    //Trainers Views
    { path: "/exercises", name: "exercises", component: Exercises, meta: { requiresAuth: true,
            roleNeeded : ['TRAINER', 'HIRED_TRAINER']}
    },
    { path: "/sets", name: "sets", component: Sets, meta: { requiresAuth: true,
            roleNeeded : ['TRAINER', 'HIRED_TRAINER']}
    },

    //Error Views
    { path: "/error", name: "error", component: Error },
    //{ path: "/:pathMatch(.*)*", redirect: { name: "error" }, name: "not-found" },
]

const router = createRouter({
    routes,
    history: createWebHistory(),
})

router.beforeEach((to , from) => {
    console.log("to",to)
    console.log("from",from)
    if (to.meta.requiresAuth) {
        if (document.cookie.includes('accessToken') == undefined) {
            return {name: "login"}
        }
        // else {
        //     // if (!document.cookie.includes('access_token')) {
        //     //     store.commit('logout')
        //     // }
        // }
    }
    if ((to.name === "login" || to.name === 'signup') && store.getters.userData.token) {
        return {name: "home"}
    }

})

router.beforeEach((to) => {
    if (to.meta.roleNeeded) {
        const roles = to.meta.roleNeeded as string[]
        const rbac = RBAC
        const userRole = rbac.getUserRole()
        if (!roles.includes(userRole)) {
            return {name: "error"}
        }
    }
})

export default router
