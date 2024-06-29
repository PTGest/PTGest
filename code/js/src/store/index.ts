import { createStore } from "vuex"
import UserData from "../models/UserData.ts"
import VuexPersistence from "vuex-persist"
import { UserInfo } from "../views/user/UserProfile/Models/UserInfo.ts"
import TrainerSessionDetails from "../views/user/TrainerViews/models/sessions/TrainerSessionDetails.ts"
import TraineeInfo from "../views/user/TrainerViews/models/trainees/TraineeInfo.ts"

interface State {
    userData: UserData
    is_mobile_view: boolean
    userBio: string
    userInfo: UserInfo
    traineeInfo: TraineeInfo
    sessionDetails: TrainerSessionDetails
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
            is_mobile_view: false,
            userInfo: {
                name: "",
                email: "",
                phone: "",
            },
            traineeInfo: {
                id: "",
                name: "",
            },
            sessionDetails: {
                id: -1,
                traineeName: "",
                workoutId: -1,
                beginDate: "",
                endDate: "",
                location: "",
                type: "",
                notes: "",
                cancelled: false,
                reason: "",
                source: "",
            },
        }
    },
    mutations: {
        setUserData(state: State, userData: UserData) {
            state.userData = userData
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
        setTraineeInfo(state: State, traineeInfo: TraineeInfo) {
            state.traineeInfo = traineeInfo
        },
        setSessionDetails(state: State, sessionDetails: TrainerSessionDetails) {
            state.sessionDetails = sessionDetails
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
        logout({ commit }: any) {
            commit("logout")
        },
        setTraineeInfo({ commit }: any, traineeInfo: TraineeInfo) {
            commit("setTraineeInfo", traineeInfo)
        },
        setSessionDetails({ commit }: any, sessionDetails: TrainerSessionDetails) {
            commit("setSessionDetails", sessionDetails)
        },
    },
    getters: {
        userData: (state: State) => state.userData,
        is_mobile_view: (state: State) => state.is_mobile_view,
        userInfo: (state: State) => state.userInfo,
        traineeInfo: (state: State) => state.traineeInfo,
        sessionDetails: (state: State) => state.sessionDetails,
    },
    plugins: [vuexLocal.plugin],
})

export default store
