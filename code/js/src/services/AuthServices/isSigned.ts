import fetchData from "../utils/fetchUtils/fetchData.ts";
import store from "../../store";
import {apiBaseUri} from "../utils/envUtils.ts";

async function isSigned() {
    const uri = `${apiBaseUri}/api/auth/validate`;

    const response = await fetchData(uri, "GET", null);
    store.commit("setLogin", true);
    return response;
}
export default isSigned;
