import { createApp } from "vue"
import "./style.css"
import router from "../src/plugins/router.ts"
import store from "../src/store/index.ts"
import PrimeVue from "primevue/config"
import "primevue/resources/themes/aura-light-green/theme.css"
import { createVuetify } from "vuetify"
import * as components from "vuetify/components"
import * as directives from "vuetify/directives"
import App from "./App.vue"

const vuetify = createVuetify({
    components,
    directives,
})

const initializeApp = () => {
    const app = createApp(App)
    app.use(store)
    app.use(router)
    app.use(PrimeVue)
    app.use(vuetify)
    app.mount("#app")
}

initializeApp()
