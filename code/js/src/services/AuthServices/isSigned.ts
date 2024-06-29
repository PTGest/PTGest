import fetchData from "../utils/fetchData.ts";
import store from "../../store";



async function isSigned() {
    const uri = "http://localhost:8080/api/auth/validate";

    const response = await fetchData(uri, "GET", null);
    store.commit("setLogin", true);
    return response;
}
export default isSigned;
