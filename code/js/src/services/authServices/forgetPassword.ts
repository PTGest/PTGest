
export default async function forgetPasswordServices(email: string) : Promise<void> {
    // Logic to sign up
    fetch('http://localhost:8080/api/', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(email),
    }).then(response => {
        if (response.ok) {
            console.log('Deu Bom familia nao dei fumble da bag')
            return response.json();
        } else {
            throw new Error('Failed to send email');
        }
    })
    return;
}