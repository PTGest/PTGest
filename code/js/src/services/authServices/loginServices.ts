import LoginUserData from "../../models/authModels/LoginUserData.ts";
import router from "../../plugins/router.ts";
import {signedIn} from "../../components/SideBar.vue";
export async function loginUserServices(userLoginData: LoginUserData): Promise<void> {
    try{
        const response = await fetch('http://localhost:8080/api/login', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            credentials: 'include',
            body: JSON.stringify(userLoginData),
        });

        if (response.ok) {
            console.log('Login successful');
            await router.push({name: 'home'});
            signedIn()
            return response.json();
        }
        if (response.status === 401) {
            const element = document.createElement("div");
            element.innerHTML = "Invalid email or password";
            element.classList.add("error-message");
            element.style.color = "red";
            element.style.padding = "0.5em";
            document.getElementById("login-inputs-containers") ?.appendChild(element);
        }
    }catch (error: any){
        await router.push({name: 'error',  params: { errorMessage: 'Failed to login' , message: "error.message"}});
    }

}