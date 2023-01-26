package kr.co.smartbank.app.util

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import kr.co.everspin.eversafe.EversafeHelper
import kr.co.smartbank.app.config.Constants
import kr.co.smartbank.app.process.CommonDTO
import kr.co.smartbank.app.service.ApiService
import kr.co.smartbank.app.service.ServiceGenerator
import kr.co.smartbank.app.solution.eightbyte.util.MotpManger
import kr.co.smartbank.app.solution.eightbyte.util.PinManger
import kr.co.smartbank.app.solution.eightbyte.util.SmartAppManger
import kr.co.smartbank.app.view.BaseActivity
import okhttp3.MediaType
import okhttp3.RequestBody
import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import java.lang.Exception

class ValidationUtil {
    @SuppressLint("StaticFieldLeak")
    fun everSafeTokenCheck(commonDTO: CommonDTO,callback: ((String)->Unit)) {
        val service = ServiceGenerator.createService(ApiService::class.java)
        LoadingUtil.show(commonDTO.customProgressDialog)

        object: EversafeHelper.GetVerificationTokenTask() {
            override fun onAction(verificationToken: ByteArray?, verificationTokenAsBase64:
            String?, result: Int){
            // 검증 토큰이 취득된 상태. 토큰을 사용자 요청과 함께 서버로 전송한다.
            // verificationToken 와 verificationTokenAsBase64 는 둘 다 동일한 정보를binary 형태와 string 형태의 다른 형태로 제공합니다. 사용자 요청의 프로토콜에 맞는 형식을 사용하시면 됩니다.
                Logcat.d("에버세이프 취득값 :  => $verificationToken")
                Logcat.d("에버세이프 취득값2 :  => $verificationTokenAsBase64")
                Logcat.d("에버세이프 취득값3 :  => $result")

                val reqJson = JSONObject().apply {
                    put("encToken",verificationTokenAsBase64)
                }

                Logcat.d("에버세이프 요청 : $reqJson")
                val request = RequestBody.create(MediaType.parse("application/json"), "$reqJson")
                val checkPinValidation = service.everSafeTokenCheck(
                        request
                ) as Call<ResponseBody>

                val inputActivity  = commonDTO.activity as BaseActivity

                checkPinValidation.enqueue(object : Callback<ResponseBody> {
                    //응답결과
                    override fun onResponse(
                            call: Call<ResponseBody>,
                            response: retrofit2.Response<ResponseBody>
                    ) {
                        LoadingUtil.hide(commonDTO.customProgressDialog)

                        if(200==response.code()){
                            //결과 셋팅
                            response.body()?.let {
                                Logcat.d("api success")
                                val resultStr = it.string()
                                Logcat.d("에버세이프 응답 : $resultStr")
                                try {
                                    val jsonObject = JSONObject(resultStr)
                                    if (jsonObject.getBoolean("result")){
                                        callback.invoke("콜백실행")
                                    }else{
                                        jsonObject.getString("msg").toString().let {msg->
                                            if(Constants.EVER_SPIN_YN){
                                                appClose(msg,inputActivity)
                                            }else{
                                                Toast.makeText(inputActivity,"위변조 패스",Toast.LENGTH_LONG).show()
                                                callback.invoke("콜백실행")
                                            }
                                        }
                                    }
                                }catch (e:Exception){
                                    appClose(resultStr,inputActivity)
                                }
                            }
                        }else{
                            response.errorBody()?.let {
                                val jsonObject = JSONObject(it.string()).toString()
                                Logcat.e("검증통신에러 : $jsonObject")
                                appClose(jsonObject,inputActivity)
                            }
                        }
                    }
                    //통신오류
                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                        Logcat.e("에러입니다 [${t.message}]")
                        appClose("보안모듈[통신오류2]",inputActivity)
                    }
                })
            }
        }.setTimeout(1000).execute()
    }

    private fun appClose(msg:String,activity:Activity){
        Toast.makeText(activity, msg,Toast.LENGTH_LONG).show()
        Handler(Looper.getMainLooper()).postDelayed({
            Toast.makeText(activity,"앱이 종료됩니다.",Toast.LENGTH_SHORT).show()
            activity.finishAffinity()
            //  딜레이를 준 후 시작
        },2000)
    }

    fun checkPinValidation(commonDTO: CommonDTO,number:String, type: String) {
        val service = ServiceGenerator.createService(ApiService::class.java)

        Logcat.d("ValidationUtil.checkPinValidation")
        Logcat.d("number = $number")
        Logcat.d("type = $type")

        val reqData = JSONObject().apply {
            put("encData",number)
            put("custNo",commonDTO.sp.getValue("custNo", "00000000023").toString())
        }
        val reqJson = "$reqData"

        val request = RequestBody.create(MediaType.parse("application/json"),  reqJson.toString())
        Logcat.d("reqJson = $reqJson")
        val checkPinValidation = service.checkPinValidation(
                request!!
        ) as Call<ResponseBody>
        checkPinValidation.enqueue(object : Callback<ResponseBody> {
            //응답결과
            override fun onResponse(
                    call: Call<ResponseBody>,
                    response: retrofit2.Response<ResponseBody>
            ) {
                LoadingUtil.hide(commonDTO.customProgressDialog)

                Logcat.d("response : $response")

                var resultCd = "0000"
                var resultParam = JSONObject()

                if (response != null) {
                    try {
                        //결과 셋팅
                        val jsonObject: JSONObject = if (response.isSuccessful) {
                            Logcat.d("api success")
                            JSONObject(response.body()!!.string())
                        } else {
                            Logcat.e("api fail")
                            val body = response.errorBody()!!.string().toString()
                            Logcat.d(body+"")
                            var json = JSONObject()
                            json.put("succYn","N")
                            json.put("errMesg",body)
                            json
                        }
                        Logcat.d("API통신 결과")
                        Logcat.d("jsonObject => $jsonObject")
                        val succYn = jsonObject.optString("succYn", "")
                        val errMesg = jsonObject.optString("errMesg", "유효성 검증에 실패하였습니다.")
                        when (response.code()) {
                            200 -> {
                                if ("Y" == succYn) {
                                    when (type) {
                                        "PIN" -> {
                                                // 간편비밀번호 확인 입력 호출
                                            PinManger().doRegisterPin2(commonDTO)
                                            return
                                        }
                                        "MOTP" -> {
                                            // MOTP 확인 입력 호출
                                            MotpManger().doRegisterMotp2(commonDTO)
                                            return
                                        }
                                        "SMARTAPP" -> {
                                            // 간편비밀번호 확인 입력 호출
                                            SmartAppManger().doRegisterSmartApp2(commonDTO)
                                            return
                                        }
                                    }
                                }else{
                                    Logcat.e("검증결과미스")
                                    resultCd = "9995"
                                    resultParam.put("msg",errMesg)
                                }
                            }
                            else -> {
                                Logcat.e("통신오류")
                                resultCd = "9995"
                                resultParam.put("msg","통신값오류")
                            }
                        }
                    }catch (e:JSONException){
                        Logcat.e("통신값오류")
                        resultCd = "9995"
                        resultParam.put("msg","통신값오류")
                        Logcat.e("에러입니다 [${e.message}]")
                    }catch (e:Exception){
                        Logcat.e("API통신오류")
                        resultCd = "9995"
                        resultParam.put("msg","API통신오류")
                        Logcat.e("에러입니다 [${e.message}]")
                    }
                }else {
                    Logcat.e("응답결과없음")
                    resultCd = "9995"
                    resultParam.put("msg","응답결과없음")
                }

                val callbackName = commonDTO.sp.getValue(Constants.SUCC_FUNC, "")
                DataSend.getInstance().webViewSend(resultCd, resultParam, callbackName!!, commonDTO.webView)
            }

            //통신오류
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                println("문제가 발생하였습니다.")
                Logcat.e("응답결과없음")
                val resultCd = "9995"
                var resultParam = JSONObject()
                resultParam.put("msg","통신솔루션오류")
                val callbackName = commonDTO.sp.getValue(Constants.SUCC_FUNC, "")
                DataSend.getInstance().webViewSend(resultCd, resultParam, callbackName!!, commonDTO.webView)
            }
        })
    }
}