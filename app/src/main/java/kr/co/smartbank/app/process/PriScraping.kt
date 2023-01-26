package kr.co.smartbank.app.process

import android.content.Intent
import android.webkit.WebView
import androidx.activity.result.ActivityResultLauncher
import kr.co.coocon.sasapi.SASManager
import kr.co.smartbank.app.solution.coocon.view.InputCaptchaActivity
import kr.co.smartbank.app.util.CryptoUtil
import kr.co.smartbank.app.util.DataSend
import kr.co.smartbank.app.util.Logcat
import kr.co.smartbank.app.view.MainActivity
import org.json.JSONObject

class PriScraping private constructor(){
    companion object{
        private var instance : PriScraping? = null
        fun getInstance():PriScraping{
            return instance ?: synchronized(this){
                instance ?: PriScraping().also {
                    instance = it
                }
            }
        }
    }

//    private var startActivityResult: ActivityResultLauncher<Intent>? = null

    /**
     *
     */
    fun priSecuNum(inputData: JSONObject, sasManager: SASManager){
        val scrapData = JSONObject()
        scrapData.apply {
            put("Module", "MinWon")
            put("Class", "민원신청조회")
            put("Job", "보안문자")
            put("Input", JSONObject())
        }
        sasManager.run(0, scrapData.toString())
    }

    fun priReq(inputData: JSONObject, sasManager: SASManager){
        val min01 = inputData.getJSONObject("MinWon_1")
        min01.getJSONObject("Input").put("주민등록번호"
                ,CryptoUtil.getInstace().decrypt(min01.getJSONObject("Input").getString("주민등록번호")))
        val min03 = inputData.getJSONObject("MinWon_3")
        sasManager.run(1, min01.toString())
        sasManager.run(2, min03.toString())
        if(!inputData.isNull("MinWon_0")){
            val min00 = inputData.getJSONObject("MinWon_0")
            sasManager.run(3, min00.toString())
            finPriYn=false
        }else{
            finPriYn=true
        }
    }
    fun priReq2(inputData: JSONObject, sasManager: SASManager){
        val nhis00 = inputData.getJSONObject("NHIS_0")
        sasManager.run(6, nhis00.toString())
    }
    fun priReq3(inputData: JSONObject, sasManager: SASManager){
        val home01 = inputData.getJSONObject("HOME_1")
        home01.getJSONObject("Input").put("주민등록번호"
                ,CryptoUtil.getInstace().decrypt(home01.getJSONObject("Input").getString("주민등록번호")))
        sasManager.run(10, home01.toString())
    }

    fun priRes(inputData: JSONObject, sasManager: SASManager){
        val min02 = inputData.getJSONObject("MinWon_2")
        sasManager.run(4, min02.toString())
    }
    fun priRes2(inputData: JSONObject, sasManager: SASManager){
        val nhis1 = inputData.getJSONObject("NHIS_1")
        if(finPriYn){
            nhis1.getJSONObject("Input").put("주민사업자번호"
                    ,CryptoUtil.getInstace().decrypt(nhis1.getJSONObject("Input").getString("주민사업자번호")))
        }
        sasManager.run(7, nhis1.toString())
    }

    fun priRes3(inputData: JSONObject, sasManager: SASManager){
        val home1 = inputData.getJSONObject("HOME_2")
        sasManager.run(11, home1.toString())
    }

    fun priMinWonLogout(inputData: JSONObject, sasManager: SASManager){
        val min04 = inputData.getJSONObject("MinWon_4")
        sasManager.run(5, min04.toString())
    }
    fun priMinWonRun2(inputData: JSONObject, sasManager: SASManager){
        val nhis2 = inputData.getJSONObject("NHIS_2")
        val nhis3 = inputData.getJSONObject("NHIS_3")
        sasManager.run(8, nhis2.toString())
        sasManager.run(9, nhis3.toString())
    }
    fun priMinWonRun3(inputData: JSONObject, sasManager: SASManager){
        val home3 = inputData.getJSONObject("HOME_3")
        val home4 = inputData.getJSONObject("HOME_4")
        sasManager.run(12, home3.toString())
        sasManager.run(13, home4.toString())
    }

    private var minwonData : JSONObject = JSONObject()
    private var finPriYn = true

    fun init(activity: MainActivity, priSecuNumResult: ActivityResultLauncher<Intent>, sasManager: SASManager, succFunc: String, webView: WebView){
        sasManager.addSASRunCompletedListener { index, outString ->
            val jsonObject = JSONObject(outString) //결과
            val output = jsonObject.getJSONObject("Output") //결과2
            val errorCode = output.optString("ErrorCode", "")
            val errorMessage = output.optString("ErrorMessage", "")
            Logcat.d("[scraping result] index : $index , errorCode : $errorCode ,errorMessage : $errorMessage  , result : $jsonObject")
            when (index) {
                /**
                 * 보안문자 요청
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
//                        activity.startActivity(intent)
                        /**
                         * 보안문자 입력 후 비회원 로그인 요청
                         */

                        priSecuNumResult.launch(intent)
                    } else {
                        activity.alertDlg(errorMessage, activity)
                    }
                }
                /**
                 * 보안문자 인증
                 */
                1 -> {
                    minwonData.put("MinWon_1",jsonObject.toString())
                }
                /**
                 * 초본
                 */
                2 -> {
                    minwonData.put("MinWon_3",jsonObject.toString())
                    Logcat.d("금융인증서여부 : $finPriYn")
                    if(finPriYn){
                        activity.runOnUiThread {
                            val resultCd = "0000"
                            DataSend.getInstance().webViewSend(resultCd, minwonData, succFunc, webView)
                        }
                    }
                }
                /**
                 * 간편인증 요청
                 */
                3 -> {
                    minwonData.put("MinWon_0",jsonObject.toString())
                        activity.runOnUiThread {
                            val resultCd = "0000"
                            DataSend.getInstance().webViewSend(resultCd, minwonData, succFunc, webView)
                        }
                    }
                4 -> {
                    minwonData.put("MinWon_2",jsonObject.toString())

                    activity.runOnUiThread {
                        val resultCd = "0000"
                        DataSend.getInstance().webViewSend(resultCd, minwonData, succFunc, webView)
                    }
                }
                5 -> {
                    minwonData.put("MinWon_4",jsonObject.toString())
                    activity.runOnUiThread {
                        val resultCd = "0000"
                        DataSend.getInstance().webViewSend(resultCd, minwonData, succFunc, webView)
                    }
                }
                /**
                 * 건강보험공단
                 */
                6 -> {
                    minwonData.put("NHIS_0",jsonObject.toString())
                    activity.runOnUiThread {
                        val resultCd = "0000"
                        DataSend.getInstance().webViewSend(resultCd, minwonData, succFunc, webView)
                    }
                }
                7 -> {
                    minwonData.put("NHIS_1",jsonObject.toString())
                    activity.runOnUiThread {
                        val resultCd = "0000"
                        DataSend.getInstance().webViewSend(resultCd, minwonData, succFunc, webView)
                    }
                }
                8 -> {
                    minwonData.put("NHIS_2",jsonObject.toString())
                }
                9 -> {
                    minwonData.put("NHIS_3",jsonObject.toString())
                    activity.runOnUiThread {
                        val resultCd = "0000"
                        DataSend.getInstance().webViewSend(resultCd, minwonData, succFunc, webView)
                    }
                }
                /**
                 * 국세청
                 */
                10 -> {
                    minwonData.put("HOME_1",jsonObject.toString())
                    activity.runOnUiThread {
                        val resultCd = "0000"
                        DataSend.getInstance().webViewSend(resultCd, minwonData, succFunc, webView)
                    }
                }
                11 -> {
                    minwonData.put("HOME_2",jsonObject.toString())
                    activity.runOnUiThread {
                        val resultCd = "0000"
                        DataSend.getInstance().webViewSend(resultCd, minwonData, succFunc, webView)
                    }
                }
                12 -> {
                    minwonData.put("HOME_3",jsonObject.toString())
                }
                13 -> {
                    minwonData.put("HOME_4",jsonObject.toString())
                    activity.runOnUiThread {
                        val resultCd = "0000"
                        DataSend.getInstance().webViewSend(resultCd, minwonData, succFunc, webView)
                    }
                }
            }
        }
    }
}