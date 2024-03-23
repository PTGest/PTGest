import {createRouter, createWebHistory} from 'vue-router'
import Home from '@/views/Home.vue'
import About from '@/views/About.vue'
import Signup from "../views/Signup.vue";

const routes = [
    {path: '/', name: 'home', component: Home},
    {path: '/about', name: 'about', component: About},
    {path: '/signup', name: 'signup', component: Signup}
]

const router = createRouter({
    routes,
    history: createWebHistory(),
})

export default router