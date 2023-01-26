package kr.co.smartbank.app.process

import android.content.Intent
import eightbyte.safetoken.SafetokenSimpleClient
import kr.co.smartbank.app.config.Constants
import kr.co.smartbank.app.solution.eightbyte.util.*
import kr.co.smartbank.app.util.DataSend
import kr.co.smartbank.app.util.Logcat
import kr.co.smartbank.app.view.LoginModeActivity
import org.json.JSONException
import org.json.JSONObject

class Authorization {
    fun authorization(params: String,commonDTO: CommonDTO) {
        try {
            val jsonObject = JSONObject(params)
            val prcsDvcd = jsonObject.optString("PRCS_DVCD", "")    // T: 토큰등록(변경), R: 인증수단등록, P: 인증수행, M: mOTPemdfhr
            val authType = jsonObject.optString("AUTH_TYPE", "0")    // O: 최종등록타입, 1: 핀번호, 2: 지문, 3: 패턴, 등록순서 1->2->3
            val authToken = jsonObject.optString("AUTH_TOKEN", "")
            val authMesg = jsonObject.optString("AUTH_MESG", "")
            val randomKey = jsonObject.optString("RANDOM_KEY", "")

            when (prcsDvcd) {
                "T" -> {    // 최초등록
                    //초기 토큰값 쉐어에 저장  나중에 재등록 에 씀
                    commonDTO.sp.put(Constants.AUTH_TOKEN,authToken)
                    // PIN번호 등록
                    PinManger().doRegisterPin1(commonDTO)
                }
                "R" -> {    // 인증수단등록
                    when (authType) {
                        "2" -> {    // 지문등록
                            BioManger.getInstance().doRegisterBio(commonDTO)
                        }
                        "3" -> {    // 패턴등록
                            PatternManger().doRegisterPattern(commonDTO)
                        }
                    }
                }
                "P" -> {    // 인증수행

                    Logcat.d("로그인 : $authMesg")
                    Logcat.d("로그인 : $randomKey")
                    commonDTO.sp.put(Constants.AUTH_MESG,authMesg)
                    commonDTO.sp.put(Constants.RANDOM_KEY,randomKey)
                    doSimpleAuth(authType,authMesg,randomKey, commonDTO)
                }
                "M" -> {    //MOTP등록
                    commonDTO.sp.put(Constants.M_AUTH_TOKEN,authToken)
                    // mOTP 등록
                    MotpManger().doRegisterMotp1(commonDTO)
                }
                "MP" -> {   //MOTP인증수행
                    commonDTO.sp.put(Constants.AUTH_MESG,authMesg)
                    commonDTO.sp.put(Constants.RANDOM_KEY,randomKey)
                    MotpManger().doAuthMotp(commonDTO)
                }
                "MD" -> {   //MOTP인증 제거

                    MotpManger().doDeleteMotp(commonDTO)
                }
                "A" -> { //스마트앱 등록
                    commonDTO.sp.put(Constants.A_AUTH_TOKEN,authToken)
                    SmartAppManger().doRegisterSmartApp1(commonDTO)
                }
                "AP" -> { //스마트앱 인증
                    commonDTO.sp.put(Constants.AUTH_MESG,authMesg)
                    commonDTO.sp.put(Constants.RANDOM_KEY,randomKey)
                    SmartAppManger().doAuthSmartApp(commonDTO)
                }
                "AD" -> { //스마트앱 제거
                    SmartAppManger().doDeleteSmartApp(commonDTO)
                }
            }

        } catch (e: JSONException) {
            Logcat.e("에러입니다 [${e.message}]")
        }
    }


    private fun doSimpleAuth(authType: String,authMesg: String,randomKey: String, commonDTO: CommonDTO) {
        // 토큰 클라이언트 생성
        val tc = SafetokenSimpleClient.getInstance(commonDTO.activity)
        // 토큰 체크
        val isExist = tc.isExistToken

        if (!isExist) {
            //Toast.makeText(commonDTO.activity, "토큰이 존재하지 않습니다.", Toast.LENGTH_SHORT).show()

            //토큰 미존
            val resultCd = "9999"
            val resultParam = JSONObject()
            val callbackName = commonDTO.sp.getValue(Constants.SUCC_FUNC, "")
            DataSend.getInstance().webViewSend(resultCd, resultParam, callbackName!!, commonDTO.webView)
            return
        }

        // 토큰 객체
        val tokenRef = tc.token


        // 데이터 체크
        if (!tokenRef.veryfyData()) {
            //Toast.makeText(commonDTO.activity, "토큰 유호성 검사에 실패하였습니다.", Toast.LENGTH_SHORT).show()
            val resultCd = "9999"
            val resultParam = JSONObject()
            val callbackName = commonDTO.sp.getValue(Constants.SUCC_FUNC, "")
            DataSend.getInstance().webViewSend(resultCd, resultParam, callbackName!!, commonDTO.webView)
            return
        }

        when (authType) { // 0: default, 1: 핀, 2: 지문, 3: 패턴
            "0" -> {
                //로그인선택화면 이동
                val intent = Intent(commonDTO.activity, LoginModeActivity::class.java).apply {
                    putExtra("authMesg", authMesg)
                    putExtra("randomKey", randomKey)
                    when(commonDTO.sp.getValue(Constants.LAST_LOGIN_PLAN, "")){
                        "1"->{
                            putExtra("mode", "1")
                        }
                        "2"->{
                            putExtra("mode", "2")
                        }
                        "3"->{
                            putExtra("mode", "3")
                        }
                        else->{
                            putExtra("mode", "0")
                        }
                    }
                }
                commonDTO.activity.startActivityForResult(intent, Constants.LOGIN_MODE)
            }
            "1" -> {
                //PinManger().doAuthPin(commonDTO,randomKey,authMesg)
                val intent = Intent(commonDTO.activity, LoginModeActivity::class.java).apply {
                    putExtra("authMesg", authMesg)
                    putExtra("randomKey", randomKey)
                    putExtra("mode", "1")
                }
                commonDTO.activity.startActivityForResult(intent, Constants.LOGIN_MODE)
            }
            "2" -> {
                //BioManger().doAuthBio(commonDTO,randomKey,authMesg)
                val intent = Intent(commonDTO.activity, LoginModeActivity::class.java).apply {
                    putExtra("authMesg", authMesg)
                    putExtra("randomKey", randomKey)
                    putExtra("mode", "2")
                }
                commonDTO.activity.startActivityForResult(intent, Constants.LOGIN_MODE)
            }
            "3" -> {
                val intent = Intent(commonDTO.activity, LoginModeActivity::class.java).apply {
                    putExtra("authMesg", authMesg)
                    putExtra("randomKey", randomKey)
                    putExtra("mode", "3")
                }
                commonDTO.activity.startActivityForResult(intent, Constants.LOGIN_MODE)
            }
        }
    }
}