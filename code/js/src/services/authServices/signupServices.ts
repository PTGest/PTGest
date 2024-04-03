import SignupPTData from "../../models/authModels/SignupPTData.ts";


export async function signupUserServices(userData: SignupPTData) : Promise<void> {
    // Logic to sign up
    fetch('http://localhost:8080/api/signup', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(userData),
    }).then(response => {
        if (response.ok) {
            return response.json();
        } else {
            throw new Error('Failed to sign up');
        }
    })
    return;
}