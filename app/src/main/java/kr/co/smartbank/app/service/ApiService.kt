package kr.co.smartbank.app.service

import okhttp3.RequestBody
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    /* 앱 위변조 토큰검증 */
    @Headers(*[
        "Content-Type: application/json; charset=utf-8",
        "Accept: application/json"
    ])
    @POST("everSafeTokenCheck.act")
    fun everSafeTokenCheck(@Body params: RequestBody?): Call<ResponseBody?>?

    /* PIN번호 유효성 검증 */

    @Headers(*[
        "Content-Type: application/json; charset=utf-8",
        "Accept: application/json"
    ])
    @POST("pinValidation.act")
   fun checkPinValidation(@Body parameters: RequestBody): Call<ResponseBody?>?
}