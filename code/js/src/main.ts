import { createApp } from "vue"
import "./style.css"
import App from "./App.vue"
import router from "../src/plugins/router.ts"
import store from "../src/store/index.ts"
createApp(App).use(store).use(router).mount("#app")
