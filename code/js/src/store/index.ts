import { createStore } from "vuex"
import UserData from "../models/UserData.ts"
import VuexPersistence from "vuex-persist"

interface State {
    userData: UserData
    errorType: ErrorType
    is_mobile_view: boolean
    userBio: string
}

const vuexLocal = new VuexPersistence({
    storage: window.localStorage,
})

// Create a new store instance.
const store = createStore<State>({
    state() {
        return {
            userData: {
                id: undefined,
                role: undefined,
                token: undefined,
                refreshToken: undefined,
            },
            errorType: {
                type: "",
                message: "",
            },
            is_mobile_view: false,
            userBio: "",
        }
    },
    mutations: {
        setUserData(state: State, userData: UserData) {
            state.userData = userData
        },
        setErrorType(state: State, errorType: ErrorType) {
            state.errorType = errorType
        },
        setMobileView(state: State, is_mobile_view: boolean) {
            state.is_mobile_view = is_mobile_view
        },
        logout(state: State) {
            state.userData = {
                id: undefined,
                role: undefined,
                token: undefined,
                refreshToken: undefined,
            }
        },
        setUserBio(state: State, bio: string) {
            state.userBio = bio
        }
    },
    actions: {
        setAuthentication({ commit }: any, userData: UserData) {
            console.log("setAuthentication", commit, userData)
            commit("setUserData", userData)
        },
        setMobile(context: any, is_mobile_view: boolean) {
            context.commit("setMobileView", is_mobile_view)
        },
        userBio(context: any, bio: string) {
            console.log("userBio", bio)
            context.commit("setUserBio", bio)
        }
    },
    getters: {
        userData: (state: State) => state.userData,
        errorType: (state: State) => state.errorType,
        is_mobile_view: (state: State) => state.is_mobile_view,
        userBio: (state: State) => state.userBio,
    },
    plugins: [vuexLocal.plugin],
})

export default store
