import {createRouter, createWebHistory} from 'vue-router';
import Login from '../views/Login.vue';
import About from '.././views/AboutPage.vue'
import Home from '../views/HomePage.vue';
import Signup from '../views/Signup.vue';
import ForgetPassword from "../views/ForgetPassword.vue";
import Error from "../views/Error.vue";
import ResetPassword from "../views/ResetPassword.vue";

const routes = [
    {path: '/', name: 'home', component: Home},
    {path: '/about', name: 'about', component: About},
    {path: '/signup', name: 'signup', component: Signup},
    {path: '/login', name: 'login', component: Login},
    {path: '/forgetPassword', name: 'forgetPassword', component: ForgetPassword},
    {path: '/resetPassword', name: 'resetPassword', component: ResetPassword, props: true},
    {path: '/error', name: 'error', component: Error}
]

const router = createRouter({
    routes,
    history: createWebHistory(),
})

export default router