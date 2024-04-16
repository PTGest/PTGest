import store from "../../store";

export default async function logoutServices() : Promise<void> {
    // Logic to sign up
    fetch('http://localhost:8080/api/auth/logout', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        credentials: 'include',
    }).then(response => {
        if (response.ok) {
            console.log('Deu Bom familia nao dei fumble da bag')
            window.localStorage.removeItem('userData');
            store.commit('logout');
            return response.json();
        } else {
            throw new Error('Failed to send email');
        }
    })
    return;
}