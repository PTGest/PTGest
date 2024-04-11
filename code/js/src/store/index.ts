import { createStore } from 'vuex'

interface State {
    userData: UserData;
    errorType: ErrorType;
    is_mobile_view: boolean;
}

// Create a new store instance.
const store = createStore<State>({
    state() {
        return {
            userData: {
                id: undefined,
                token: undefined,
                refreshToken: undefined
            },
            errorType:{
                type: "",
                message: ""
            },
            is_mobile_view: false,
        };
    },
    mutations: {
        setUserData(state : State, userData: UserData) {
            state.userData = userData;
        },
        setErrorType(state: State, errorType: ErrorType) {
            state.errorType = errorType;
        },
        setMobileView(state: State, is_mobile_view: boolean) {
            state.is_mobile_view = is_mobile_view;
        }
    },
    // actions: {
    //     setAuthentication( ) {
    //         commit('setAuthentication');
    //     },
    // },

});

export default store;