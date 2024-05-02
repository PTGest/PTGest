import {createRouter, createWebHistory, RouteRecordRaw} from 'vue-router';
import Login from '../views/auth/login/Login.vue';
import About from '.././views/AboutPage.vue'
import Home from '../views/home/HomePage.vue';
import Signup from '../views/auth/signUp/Signup.vue';
import ForgetPassword from "../views/auth/forgetPassword/ForgetPassword.vue";
import Error from "../views/Error.vue";
import ResetPassword from "../views/auth/resetPassword/ResetPassword.vue";
import UserProfile from "../views/user/UserProfile/UserProfile.vue";
import RegisterTrainee from "../views/user/UserRegister/RegisterTrainee.vue";
import Students from "../views/user/IndependentTrainerViews/Students.vue";
// import {verifyToken} from "../services/AuthServices/verifyToken.ts";


const routes:RouteRecordRaw[] = [
    {path: '/', name: 'home', component: Home , meta: {requiresAuth: false}},
    {path: '/about', name: 'about', component: About, meta: {requiresAuth: false}},
    //Auth Views
    {path: '/signup', name: 'signup', component: Signup, meta: {requiresAuth: false}},
    {path: '/login', name: 'login', component: Login , meta: {requiresAuth: false}},
    {path: '/forgetPassword', name: 'forgetPassword', component: ForgetPassword , meta: {requiresAuth: false}},
    {
        path: '/resetPassword/:token',
        name: 'resetPassword',
        component: ResetPassword,
        props: (route) => ({token: route.params.token}),
        meta: {requiresAuth: false},
        // beforeEnter: async (to, from, next) => {
        //     if (to.params.token) {
        //         const token : string | string[] = to.params.token;
        //         if (await verifyToken(token)) {
        //             next()
        //         } else {
        //             next({name: 'error'})
        //         }
        //     }
        // }
    },
    //UserServices Views
    {path: '/user/profile/:userId', name: 'userProfile', component: UserProfile, props: true , meta: {requiresAuth: true}},
    {path: '/trainees', name: 'trainees', component: Students},
    {path: '/register-trainee', name: 'registerTrainee', component: RegisterTrainee , meta: {requiresAuth: true}},
    //Error Views
    {path: '/error', name: 'error', component: Error},
    {path: '/:pathMatch(.*)*', redirect: {name: 'error'}, name: 'not-found'}
]



const router = createRouter({
    routes,
    history: createWebHistory(),
})

// router.beforeEach((to : any, next) => {
//     if (to.meta.requiresAuth) {
//         const token = store.getters.userData.token;
//         if (token) {
//             next();
//         } else {
//             next({name: 'login'});
//         }
//     } else {
//         next({name: 'login'});
//     }
// })

export default router
