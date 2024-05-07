import { createStore } from "vuex"
import UserData from "../models/UserData.ts"
import VuexPersistence from "vuex-persist"
import { UserInfo } from "../views/user/UserProfile/Models/UserInfo.ts"

interface State {
    userData: UserData
    errorType: ErrorType
    is_mobile_view: boolean
    userBio: string
    userInfo: UserInfo
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
            userInfo: {
                name: "",
                email: "",
                phone: "",
            },
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
        setUserInfo(state: State, userInfo: UserInfo) {
            state.userInfo = userInfo
        },
    },
    actions: {
        setAuthentication({ commit }: any, userData: UserData) {
            console.log("setAuthentication", commit, userData)
            commit("setUserData", userData)
        },
        setMobile({ commit }: any, is_mobile_view: boolean) {
            commit("setMobileView", is_mobile_view)
        },
        userInfo({ commit }: any, userInfo: UserInfo) {
            console.log("userBio", userInfo)
            commit("setUserInfo", userInfo)
        },
    },
    getters: {
        userData: (state: State) => state.userData,
        errorType: (state: State) => state.errorType,
        is_mobile_view: (state: State) => state.is_mobile_view,
        userInfo: (state: State) => state.userInfo,
    },
    plugins: [vuexLocal.plugin],
})

export default store
