import {apiBaseUri} from "../utils/envUtils.ts";
import fetchData from "../utils/fetchUtils/fetchData.ts";
import ChangePasswordRequest from "../../views/auth/models/ChangePasswordRequest.ts";

async function changeUserPassword(currentPassword: string, newPassword: string) {
    const uri = `${apiBaseUri}/api/auth/change-password`;
    return await fetchData(uri, "GET", new ChangePasswordRequest(currentPassword, newPassword));
}
export {changeUserPassword};
