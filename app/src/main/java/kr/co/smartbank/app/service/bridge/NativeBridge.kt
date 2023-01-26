package kr.co.smartbank.app.service

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Handler
import android.webkit.JavascriptInterface
import kr.co.smartbank.app.process.Authorization
import kr.co.smartbank.app.process.CommonDTO
import kr.co.smartbank.app.util.Logcat
import kr.co.smartbank.app.view.MainActivity
import org.json.JSONException
import org.json.JSONObject
import java.util.logging.Logger


class NativeBridge(private val activity: Activity) {
    private val mHandler = Handler()

    private var bCmdProcess = false

    private var count = 0;
    @JavascriptInterface
    fun excute(inputData: String,succFunc: String, failFunc: String) {
        try {
            Logcat.d("excute start")
            if (bCmdProcess) return
            bCmdProcess = true

            mHandler.post {
                Logcat.d("===============================NativeBridge "+(++count)+"===============================")
                Logcat.d("inputData: $inputData")
                Logcat.d("succFunc : "+succFunc)
                Logcat.d("failFunc : "+failFunc)
                Logcat.d("============================================================================")
                val jsonObject = JSONObject(inputData)
                val serviceCd = jsonObject.optString("serviceCd", "")
                val params = jsonObject.optString("params", "")

                when (serviceCd) {
                    "LOADING" -> {  //
                        (activity as MainActivity).loading(params)
                    }
                    "CALENDAR" -> { // 네이티브 달력 호출
                        (activity as MainActivity).calendar(params, succFunc, failFunc)
                    }
                    "WEB_LINK" -> { // 웹 링크
                        try {
                            val jsonObject = JSONObject(params)
                            val url = jsonObject.optString("url", "")
                            Logcat.d("url: $url")
                            try {
                                val intent = Intent(
                                    Intent.ACTION_VIEW,
                                    Uri.parse(url)
                                )
                                activity.startActivity(intent)
                            } catch (e: ActivityNotFoundException) {
                                Logcat.e("URI파싱에러")
                            }
                        } catch (e: JSONException) {
                            Logcat.e("웹링크에러")
                        }
                    }
                    "WEB_SUBMIT" -> { // 웹사이트 서브밋
                        try {
                            val jsonObject = JSONObject(params)
                            val url = jsonObject.optString("url", "")
                            val reqData = jsonObject.optString("reqData", "")
                            val finalUrl = "javascript:"  +
                                    "var to = '$url';" +
                                    "var p = $reqData;" +
                                    "var myForm = document.createElement('form');" +
                                    "myForm.method='post' ;" +
                                    "myForm.action = to;" +
                                    "for (var k in p) {" +
                                    "var myInput = document.createElement('input') ;" +
                                    "myInput.setAttribute('type', 'hidden');" +
                                    "myInput.setAttribute('name', k) ;" +
                                    "myInput.setAttribute('value', p[k]);" +
                                    "myForm.appendChild(myInput) ;" + "}" +
                                    "document.body.appendChild(myForm) ;" +
                                    "myForm.submit() ;" +
                                    "document.body.removeChild(myForm) ;"
                            try {
                                val intent = Intent(
                                    Intent.ACTION_VIEW,
                                    Uri.parse(finalUrl)
                                )
                                activity.startActivity(intent)
                            } catch (e: ActivityNotFoundException) {
                                Logcat.e("URI파싱에러")
                            }

                        } catch (e: JSONException) {
                            Logcat.e("앱링크에러")
                        }
                    }
                    "SCRAPING" -> { // 스크래핑
                        (activity as MainActivity).scraping(params, succFunc, failFunc)
                    }
                    "TRANSKEY" -> { // 보안키패드
                       (activity as MainActivity).secureKeyPad(params, succFunc, failFunc)
                    }
                    "SIGN_CERT" -> {  // 공동인증 서명
                        (activity as MainActivity).signCert(params, succFunc, failFunc)
                    }
                    "SIGN_CERT_MANAGE" -> { // 공동인증관리
                        (activity as MainActivity).certMng(params, succFunc, failFunc)
                    }
                    "SIGN_CERT_REG" -> { // 공동인증서 가져오기
                        (activity as MainActivity).certImport(params, succFunc, failFunc)
                    }
                    "OCR" -> { // OCR
                        (activity as MainActivity).ocr(params, succFunc, failFunc)
                    }

                    "CAMERA" -> { // OCR
                        (activity as MainActivity).camera(params, succFunc, failFunc)
                    }

                    "APP_DATA" -> { // 앱데이터공유
                        (activity as MainActivity).appData(params, succFunc, failFunc)
                    }
                    "AUTHORIZATION" -> { // 인증
                        (activity as MainActivity).authorization(params, succFunc, failFunc)
                    }
                    "APP_LINK" -> { // 외부 앱 호출
                        (activity as MainActivity).appLink(params)
                    }
                    "APP_CLOSE" -> { //앱닫기
                        activity.finishAffinity()
                    }
                    "GET_ADID" -> { //ADID
                        (activity as MainActivity).getAdid(params, succFunc, failFunc)
                    }
                    "FDS" -> {  //FDS
                        (activity as MainActivity).fds(params, succFunc, failFunc)
                    }
                    "DOWN" -> {
                        (activity as MainActivity).down(params, succFunc, failFunc)
                    }
                    "PUSH_HIST" -> {
                        (activity as MainActivity).pushHist()
                    }
                    "PRI_SCRAPING" -> {
                        (activity as MainActivity).priScraping(params, succFunc, failFunc)
                    }
                    "FIN_SCRAPING" -> {
                        (activity as MainActivity).finScraping(params, succFunc, failFunc)
                    }
                }

            }
        } catch (e: Exception) {
            Logcat.e("브릿지통신에러")
        } finally {
            bCmdProcess = false
        }
    }
}