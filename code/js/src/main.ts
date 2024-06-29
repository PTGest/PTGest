import { createApp } from "vue"
import "./style.css"
import App from "./App.vue"
import router from "../src/plugins/router.ts"
import store from "../src/store/index.ts"
import PrimeVue from "primevue/config"
import "primevue/resources/themes/aura-light-green/theme.css"
import { createVuetify } from "vuetify"
import * as components from "vuetify/components"
import * as directives from "vuetify/directives"

const vuetify = createVuetify({
    components,
    directives,
})

createApp(App).use(store).use(router).use(PrimeVue).use(vuetify).mount("#app")
