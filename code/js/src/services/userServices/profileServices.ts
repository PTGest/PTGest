import { apiBaseUri } from "../utils/envUtils.ts"
import fetchData from "../utils/fetchUtils/fetchData.ts"
import UserInfo from "../../views/user/userProfile/models/UserInfo.ts"

export async function getUserInfo(userId: string): Promise<UserInfo> {
    const uri = `${apiBaseUri}/api/user/${userId}`
    try {
        const response = await fetchData(uri, "GET", null)
        return response.details
    } catch (error: any) {
        throw error
    }
}
