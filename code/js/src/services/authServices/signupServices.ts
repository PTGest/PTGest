import SignupPTData from "../../models/authModels/SignupPTData.ts";
import router from "../../plugins/router.ts";
import store from "../../store";


export async function signupUserServices(userData: SignupPTData) : Promise<void> {
    // Logic to sign up
    fetch('http://localhost:8080/api/signup', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(userData),
    }).then(response => {

        switch (response.status) {
            case 200 :
                router.push({name: 'login'})
                return response.json();

            default :
                response.json().then( (response) => {
                        store.commit('setErrorType', {type: response.type, message: response.title});
                        router.push({ name: 'error'});
                    }
                );
                break;
        }


        if (response.ok) {

        } else {
            throw new Error('Failed to sign up');
        }
    })
    return;
}