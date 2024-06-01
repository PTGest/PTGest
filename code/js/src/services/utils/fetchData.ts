
async function fetchData(uri: string, method: string, bodyData: any | null): Promise<any> {
    const body = bodyData ? JSON.stringify(bodyData) : null;
    console.log("BODYDATA", bodyData)
    console.log("BODY",body)

    const response = await fetch(uri, {
        method: method,
        headers: {
            "Content-Type": "application/json",
        },
        body: body,
        credentials: "include",
    });

    if (response.ok) {
        console.log("Operation successful");
        return await response.json();
    }

    if (!response.ok) {
        if (response.status === 400) {
            throw new Error("Bad request");
        } else {
            throw new Error("Failed to fetchData exercises");
        }
    }

}
export default fetchData;
