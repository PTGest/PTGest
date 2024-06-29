import {createApp, nextTick} from "vue"
import "./style.css"
import router from "../src/plugins/router.ts"
import store from "../src/store/index.ts"
import PrimeVue from 'primevue/config';
import 'primevue/resources/themes/aura-light-green/theme.css'
import { createVuetify } from 'vuetify'
import * as components from 'vuetify/components'
import * as directives from 'vuetify/directives'
import ToastService from 'primevue/toastservice';
import App from "./App.vue";

const vuetify = createVuetify({
    components,
    directives,
})

 // const app =
 createApp(App).use(store).use(router).use(PrimeVue).use(vuetify).use(ToastService).mount("#app");

// export function errorToast(title: string = 'I am title', body: string = 'I am body'): void {
//     nextTick(() => {
//         app.config.globalProperties.$toast.add({
//             severity: 'error',
//             summary: title,
//             detail: body,
//             life: 3000
//         });
//     });
// }

