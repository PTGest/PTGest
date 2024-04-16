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

            case 409 :
                const element = document.createElement("div");
                element.innerHTML = "User already exists";
                element.classList.add("error-message");
                element.style.color = "red";
                element.style.padding = "0.5em";
                document.getElementById("signup-container") ?.appendChild(element);
                break;
            default :
                response.json().then( (response) => {
                        store.commit('setErrorType', {type: response.type, message: response.title});
                        router.push({ name: 'error'});
                    }
                );
                break;
        }
    })
    return;
}