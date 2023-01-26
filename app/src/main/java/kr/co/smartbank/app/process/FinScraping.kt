package kr.co.smartbank.app.process

import android.app.Activity
import android.content.Intent
import android.view.WindowManager
import android.webkit.WebView
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.appcompat.app.AlertDialog
import kr.co.coocon.sasapi.SASManager
import kr.co.smartbank.app.R
import kr.co.smartbank.app.config.Constants
import kr.co.smartbank.app.solution.coocon.view.InputCaptchaActivity
import kr.co.smartbank.app.util.*
import kr.co.smartbank.app.view.MainActivity
import org.json.JSONObject


class FinScraping {
    companion object{
        private var instance : FinScraping? = null
        fun getInstance():FinScraping{
            return instance ?: synchronized(this){
                instance ?: FinScraping().also {
                    instance = it
                }
            }
        }
    }

    /**
     * 1 . 보안문자 이미지 요청
     */
    fun finSecuNumReq(activity: MainActivity,inputData: JSONObject, sasManager: SASManager){
        LoadingUtil.showAndText(
                activity.customProgressDialog,
                activity.getString(R.string.sc_txt_01),
                activity
        )
        val scrapData = JSONObject()
        scrapData.apply {
            put("Module", "MinWon")
            put("Class", "민원신청조회")
            put("Job", "보안문자")
            put("Input", JSONObject())
        }
        sasManager.run(0, scrapData.toString())
    }

    /**
     * 2. 보안문자 입력값 검증요청
     */
    fun finSecuNumRes(activity: MainActivity,inputData: JSONObject, sasManager: SASManager){
        val scrapData : JSONObject = inputData.getJSONObject("MinWon_1")
        scrapData.apply {
            getJSONObject("Input").put("주민등록번호"
                    ,CryptoUtil.getInstace().decrypt(getJSONObject("Input").get("주민등록번호").toString()))
        }
        sasManager.run(1, scrapData.toString())
    }

    /**
     * 금융인증서 로그인 요청  저축은행=>쿠콘
     */
    fun finLoginReq(activity: MainActivity,inputData: JSONObject, sasManager: SASManager){
        val scrapData = JSONObject()
        scrapData.apply {
            put("Module", "MinWon")
            put("Class", "민원신청조회")
            put("Job", "보안문자")
            val input = JSONObject()
            input.put("로그인방식","FIN_CERT")
            put("Input", input.toString())
        }
        sasManager.run(2, scrapData.toString())
    }

    /**
     * 금융인증서 로그인 요청 쿠폰=>공공기관
     */
    fun finLoginRes(activity: MainActivity,inputData: JSONObject, sasManager: SASManager){
//        val params = JSONObject(params)
        val scrapData = JSONObject()
        scrapData.apply {
            put("Module", "MinWon")
            put("Class", "민원신청조회")
            put("Job", "보안문자")
            val input = JSONObject()
            input.put("res",inputData.getJSONArray("res").toString())
            input.put("로그인방식","FIN_CERT")
            put("Input", input.toString())
        }
        sasManager.run(3, scrapData.toString())
    }


    /**
     * 스크래핑 업무 탑재
     */
    fun init(activity: MainActivity, inputData: JSONObject, sasManager: SASManager, webView: WebView,sp: SharedPreferenceHelper){
        sasManager.addSASRunCompletedListener { index, outString ->
            val jsonObject = JSONObject(outString) //결과
            val output = jsonObject.getJSONObject("Output") //결과2
            val errorCode = output.optString("ErrorCode", "")
            val errorMessage = output.optString("ErrorMessage", "")
            Logcat.d("[scraping result] index : $index , errorCode : $errorCode ,errorMessage : $errorMessage  , result : $jsonObject")
            when (index) {
                /**
                 * 1 . 보안문자 이미지 요청 결과
                 */
                0 -> {
                    if ("00000000".equals(errorCode, true)) {
                        //보안문자 입력화면 이동
                        val intent = Intent(activity, InputCaptchaActivity::class.java)
                        intent.apply {
                            val secuStr = output.getJSONObject("Result").optString("보안문자", "")
                            putExtra("captchaImage", secuStr)
                            putExtra("subjectDn", "")
                            putExtra("expiredTime", "")
                            putExtra("inputPw", "")
//                            putExtra(Constants.KSW_Activity, Constants.REQUEST_SCRAPING_CAPTCHA_INPUT)
                        }

                        Logcat.d("보안문자화면 이동")
                        /**
                         * 보안문자 입력 후 비회원 로그인 요청
                         */
                        val startActivityResult=activity.registerForActivityResult(StartActivityForResult()){ result->
                            //입력된 보안문자을 토대로 금융인증서 로그인 요청
                            if (result.resultCode == Activity.RESULT_OK) {
                                inputData
                                        .getJSONObject("MinWon_1")
                                        .getJSONObject("Input")
                                        .put("보안문자", result.data?.getStringExtra("code")).toString()
                                finSecuNumRes(activity,inputData,sasManager)
                            }else{
                                activity.alertDlg("스크래핑 모듈 오류 발생")
                            }
                        }
                        startActivityResult.launch(intent)
                    }else{
                        activity.alertDlg(errorMessage,activity)
                    }
                }
                /**
                 * 2 . 보안문자 검증 결과
                 */
                1 -> {
                    //보안코드 검증 완료시 금융인증서 로그인 요청 실행
                    if ("00000000".equals(errorCode, true)) {
                        finLoginReq(activity,inputData,sasManager)
                    } else {
                        try {
                            val builder = AlertDialog.Builder(activity)
                            builder.setMessage("보안문자 입력이 잘못되었습니다. 새로운 보안문자를 다시 확인 후 입력해 주세요.")
                            builder.setCancelable(false)
                            builder.setPositiveButton(
                                    "확인"
                            ) { _, _ ->
                                LoadingUtil.hide(activity.customProgressDialog)
                                finSecuNumReq(activity,inputData,sasManager)
                            }
                            builder.show()

                        } catch (e: WindowManager.BadTokenException) {
                            Logcat.e("에러입니다 [${e.message}]")
                        } catch (e: Exception) {
                            Logcat.e("에러입니다 [${e.message}]")
                        }
                    }
                }
                /**
                 * 3 . 금융인증서 로그인 요청   네이티브 -> 웹
                 */
                2 -> {
                    val resultCd = if ("00000000".equals(errorCode, true)) {
                        activity.resources.getString(R.string.resultCd_ok)
                    }
                    else{
                        activity.resources.getString(R.string.resultCd_fail)
                    }
                    val resultParam = JSONObject()
                    resultParam.apply {
                        put("signMsg",output)
                    }
                    val callbackName = sp.getValue(Constants.SUCC_FUNC, "")!!
                    DataSend.getInstance().webViewSend(resultCd, resultParam, callbackName, webView)
                }

                /**
                 * 4 . 금융인증서
                 */
            }
        }
        sasManager.addSASRunStatusChangedListener { _, _ -> }
    }
}