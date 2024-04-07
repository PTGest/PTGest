import {createRouter, createWebHistory} from 'vue-router'
import Home from '../views/Home.vue'
import About from '../views/About.vue'
import Login from '../views/Login.vue'
import Signup from '../views/Signup.vue'
import ForgetPassword from "../views/ForgetPassword.vue";

const routes = [
    {path: '/', name: 'home', component: Home},
    {path: '/about', name: 'about', component: About},
    {path: '/signup', name: 'signup', component: Signup},
    {path: '/login', name: 'login', component: Login},
    {path: '/forgetPassword', name: 'forgetPassword', component: ForgetPassword}
]

const router = createRouter({
    routes,
    history: createWebHistory(),
})

export default router