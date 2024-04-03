import LoginUserData from "../../models/authModels/LoginUserData.ts";
export async function loginUserServices(userLoginData : LoginUserData) : Promise<void> {
    // Logic to sign up
    fetch('http://localhost:8080/api/login', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(userLoginData),
    }).then(response => {
        if (response.ok) {
            console.log('Deu Bom familia nao dei fumble da bag')
            return response.json();
        } else {
            throw new Error('Failed to login');
        }
    })
    return;
}