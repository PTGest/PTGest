import fetchData from "../utils/fetchData.ts";
import store from "../../store";
import {apiBaseUri} from "../../main.ts";



async function isSigned() {
    const uri = `${apiBaseUri}/api/auth/validate`;

    const response = await fetchData(uri, "GET", null);
    store.commit("setLogin", true);
    return response;
}
export default isSigned;
