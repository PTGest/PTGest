import { createStore } from "vuex"
import UserData from "../models/UserData.ts"
import VuexPersistence from "vuex-persist"
import UserInfo  from "../views/user/userProfile/models/UserInfo.ts"
import TrainerSessionDetails from "../views/user/trainerViews/models/sessions/TrainerSessionDetails.ts"
import TraineeInfo from "../views/user/trainerViews/models/trainees/TraineeInfo.ts"

interface State {
    userData: UserData
    is_mobile_view: boolean
    userBio: string
    userInfo: UserInfo
    traineeInfo: TraineeInfo
    sessionDetails: TrainerSessionDetails
    isLogged: boolean
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
            isLogged: false,
        }
    },
    mutations: {
        setUserData(state: State, userData: UserData) {
            state.userData = userData
        },
        setLogin(state: State, isLogged: boolean) {
            state.isLogged = isLogged
        },
        setMobileView(state: State, is_mobile_view: boolean) {
            state.is_mobile_view = is_mobile_view
        },
        logout(state: State) {
            state.userData = {
                id: undefined,
                role: undefined,
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
        setClearUserData(state: State) {
            state.userData = new UserData(undefined, undefined)
            state.userInfo = new UserInfo("", "", "", new Date(), "")
            state.traineeInfo = new TraineeInfo("", "")
            state.sessionDetails = new TrainerSessionDetails(-1, "", -1, "",
                "", "", "", "", false, "", null, [])
            state.userBio = ""
            state.isLogged = false
        }
    },
    actions: {
        setAuthentication({ commit }: any, userData: UserData) {
            commit("setUserData", userData)
        },
        setLogin({ commit }: any, isLogged: boolean) {
            commit("setLogin", isLogged)
        },
        setMobile({ commit }: any, is_mobile_view: boolean) {
            commit("setMobileView", is_mobile_view)
        },
        userInfo({ commit }: any, userInfo: UserInfo) {
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
        isLogged: (state: State) => state.isLogged,
        is_mobile_view: (state: State) => state.is_mobile_view,
        userInfo: (state: State) => state.userInfo,
        traineeInfo: (state: State) => state.traineeInfo,
        sessionDetails: (state: State) => state.sessionDetails,
    },
    plugins: [vuexLocal.plugin],
})

export default store
