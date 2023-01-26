package kr.co.smartbank.app.process

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Base64
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.lumensoft.ks.*
import com.signkorea.securedata.ProtectedData
import com.signkorea.securedata.SecureData
import eightbyte.safetoken.*
import kr.co.smartbank.app.R
import kr.co.smartbank.app.config.Constants
import kr.co.smartbank.app.solution.eightbyte.util.MotpManger
import kr.co.smartbank.app.solution.eightbyte.util.PinManger
import kr.co.smartbank.app.solution.eightbyte.util.SmartAppManger
import kr.co.smartbank.app.solution.everspin.secureKeypad.SecureKeyPadManager
import kr.co.smartbank.app.solution.face.util.OCRUtil
import kr.co.smartbank.app.util.*
import kr.co.smartbank.app.view.BaseActivity
import org.json.JSONObject


class ActivityResult {
    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?, commonDTO: CommonDTO){
        Logcat.d("TestActivity.onActivityResult : 테스트 후처리");
        Logcat.d("requestCode:$requestCode \nresultCode : $resultCode");

        if(AppCompatActivity.RESULT_OK == resultCode){
            when (requestCode){
                //핀번호 입력
                Constants.REQUEST_PIN_INPUT -> {
                    val manaer = SecureKeyPadManager()
                    //val firstPin = manaer.keyPadActivityOff(data)
                    val firstPin = if (data?.getStringExtra(commonDTO.activity.resources.getString(R.string.FIN_FIRST))
                                    .isNullOrEmpty()
                    ) "" else data?.getStringExtra(
                            commonDTO.activity.resources.getString(R.string.FIN_FIRST)
                    )
                    commonDTO.sp.put(Constants.FIN_NUM, firstPin!!)
                    Logcat.d("TestActivity보안키패드 IS_FIRST_PIN : $firstPin");
                    Logcat.d("doRegisterPin2")


                    //핀번호 유효성체크
                    if (Constants.CHECK_VALIDATION) {
                        ValidationUtil().checkPinValidation(commonDTO, firstPin, "PIN")
                    } else {
                        PinManger().doRegisterPin2(commonDTO)
                    }
                }
                //핀번호 확인
                Constants.REQUEST_PIN_CONFIRM -> {
                    //val secondPin = SecureKeyPadManager().keyPadActivityOff(data)
                    val secondPin = if (data?.getStringExtra(commonDTO.activity.resources.getString(R.string.FIN_FIRST))
                                    .isNullOrEmpty()
                    ) "" else data?.getStringExtra(
                            commonDTO.activity.resources.getString(R.string.FIN_FIRST)
                    )
                    val firstPin = commonDTO.sp.getValue(Constants.FIN_NUM, "")
                    Logcat.d("핀번호 firstPin : $firstPin")
                    Logcat.d("핀번호 secondPin : $secondPin")

                    //키패드 입력번호 비교
                    if (secondPin.toString().equals(firstPin)) {
                        Logcat.d("일치")
                        //commonDTO.sp.put("IS_FIRST_PIN", "")
                        val authToken = commonDTO.sp.getValue(Constants.AUTH_TOKEN, "")
                        Logcat.d("사설인증 authToken : $authToken")

                        //서버에서온 토큰값 비교
                        if (authToken != null && !"".equals(secondPin)) {

                            //인증 처리
                            PinManger().doStoreTokenPin(authToken, secondPin.toString(), commonDTO)
                            Logcat.d("핀번호 저장" + secondPin)
                            commonDTO.sp.put(Constants.FIN_NUM, secondPin.toString())

                            val resultCd = commonDTO.activity.resources.getString(R.string.resultCd_ok)
                            var resultParam = JSONObject()
                            val custNo = commonDTO.sp.getValue("custNo", "")
                            resultParam.put(commonDTO.activity.resources.getString(R.string.CUST_NO), custNo)
                            resultParam.put("AUTH_TOKEN", authToken)
                            val callbackName = commonDTO.sp.getValue(Constants.SUCC_FUNC, "")
                            DataSend.getInstance().webViewSend(
                                    resultCd,
                                    resultParam,
                                    callbackName!!,
                                    commonDTO.webView
                            )

                        } else {
                            BaseActivity().alertDlg("서버에서 응답이 없습니다.", commonDTO.activity)
                            Logcat.d("토큰 미존재")
                        }
                    } else {
                        //비밀번호 틀림
                        val resultCd = "9996"
                        val resultParam = JSONObject()
                        val callbackName = commonDTO.sp.getValue(Constants.SUCC_FUNC, "")
                        DataSend.getInstance().webViewSend(
                                resultCd,
                                resultParam,
                                callbackName!!,
                                commonDTO.webView
                        )
                    }
                }
//                Constants.Pin_Key_Activity_01 -> {
//                    val resultCd = commonDTO.activity.resources.getString(R.string.resultCd_ok)
//                    var resultParam = JSONObject()
//                    resultParam.put(commonDTO.activity.resources.getString(R.string.CUST_NO), commonDTO.sp.getValue("custNo", ""))
//                    resultParam.put(commonDTO.activity.resources.getString(R.string.RANDOM_KEY), data?.getStringExtra(commonDTO.activity.resources.getString(R.string.RANDOM_KEY)).toString())
//                    resultParam.put(commonDTO.activity.resources.getString(R.string.AUTH_MESG), data?.getStringExtra(commonDTO.activity.resources.getString(R.string.AUTH_MESG)).toString())
//                    resultParam.put(commonDTO.activity.resources.getString(R.string.SIGN), data?.getStringExtra(commonDTO.activity.resources.getString(R.string.SIGN)).toString())
//                    val callbackName = commonDTO.sp.getValue(Constants.SUCC_FUNC, "")
//                    DataSend.getInstance().webViewSend(resultCd, resultParam, callbackName!!, commonDTO.webView)
//                }
                //핀번호 서명
                Constants.REQUEST_PIN_AUTH -> {
                    if (data == null) return
                    // 라이브러리 버전
                    val safetokenVersion = SafetokenVersion.getVersion()
                    // 토큰 클라이언트 생성
                    val tc = SafetokenSimpleClient
                            .getInstance(commonDTO.activity)
                    // 토큰 체크
                    val isExist = tc.isExistToken

                    val tokenRef = tc.token
                    // 전자서명
                    try {
                        //val inputPin = SecureKeyPadManager().keyPadActivityOff( data)

                        //val inputPin: Long? = data?.getLongExtra(commonDTO.activity.resources.getString(R.string.FIN_FIRST), 0)
                        val inputPin = if (data?.getStringExtra(commonDTO.activity.resources.getString(R.string.FIN_FIRST))
                                        .isNullOrEmpty()
                        ) "" else data?.getStringExtra(
                                commonDTO.activity.resources.getString(R.string.FIN_FIRST)
                        )
                        val authMesg = commonDTO.sp.getValue(Constants.AUTH_MESG, "")
                        val randomKey = commonDTO.sp.getValue(Constants.RANDOM_KEY, "")
                        val custNo = commonDTO.sp.getValue("custNo", "")
                        // val authMesg =  commonDTO.authMesg
                        //val randomKey = commonDTO.randomKey
                        Logcat.d("사설인증 inputPin : $inputPin")
                        Logcat.d("사설인증 authMesg : $authMesg")
                        Logcat.d("사설인증 randomKey : $randomKey")
                        Logcat.d("사설인증 핀번호 로그인")
                        var tnp = tc.sign(randomKey, inputPin.toString(), authMesg!!.toByteArray())
                        //결과 셋팅
//                            val resultCd = commonDTO.activity.resources.getString(R.string.resultCd_ok)
//                            var resultParam = JSONObject()
//                            resultParam.put(commonDTO.activity.resources.getString(R.string.CUST_NO), custNo)
//                            resultParam.put(commonDTO.activity.resources.getString(R.string.RANDOM_KEY), randomKey)
//                            resultParam.put(commonDTO.activity.resources.getString(R.string.AUTH_MESG), authMesg)
//                            resultParam.put(commonDTO.activity.resources.getString(R.string.SIGN), tnp)
//                            val callbackName = commonDTO.sp.getValue(Constants.SUCC_FUNC, "")
//                            DataSend.getInstance().webViewSend(resultCd, resultParam, callbackName!!, commonDTO.webView)
                        val loginIntent = Intent().apply {
                            putExtra(commonDTO.activity.resources.getString(R.string.CUST_NO), custNo)
                            putExtra(commonDTO.activity.resources.getString(R.string.RANDOM_KEY), randomKey)
                            putExtra(commonDTO.activity.resources.getString(R.string.AUTH_MESG), authMesg)
                            putExtra(commonDTO.activity.resources.getString(R.string.SIGN), tnp.toString())
                        }
                        commonDTO.sp.put(Constants.LAST_LOGIN_PLAN, "1")
                        commonDTO.activity.setResult(AppCompatActivity.RESULT_OK, loginIntent)
                        commonDTO.activity.finish()
                        ////commonDTO.//activity.//overridePendingTransition(R.anim.slide_up, R.anim.slide_bottom)
                    } catch (e: SafetokenException) {
                        Logcat.e("에러입니다")
                        val resultCd = "9999"
                        val callbackName = commonDTO.sp.getValue(Constants.SUCC_FUNC, "")
                        DataSend.getInstance().webViewSend(resultCd, callbackName!!)
                        BaseActivity().alertDlg("인증실패", commonDTO.activity)
                    }
                }
                Constants.LOGIN_MODE -> {
                    val resultCd = commonDTO.activity.resources.getString(R.string.resultCd_ok)
                    val resultParam = JSONObject()
                    resultParam.apply {
                        put(commonDTO.activity.resources.getString(R.string.CUST_NO), data?.getStringExtra(commonDTO.activity.resources.getString(R.string.CUST_NO)))
                        put(commonDTO.activity.resources.getString(R.string.RANDOM_KEY), data?.getStringExtra(commonDTO.activity.resources.getString(R.string.RANDOM_KEY)))
                        put(commonDTO.activity.resources.getString(R.string.AUTH_MESG), data?.getStringExtra(commonDTO.activity.resources.getString(R.string.AUTH_MESG)))
                        put(commonDTO.activity.resources.getString(R.string.SIGN), data?.getStringExtra(commonDTO.activity.resources.getString(R.string.SIGN)))
                    }

                    LoadingUtil.show(commonDTO.customProgressDialog)
                    //로그인시 토큰교환
                    ValidationUtil().everSafeTokenCheck(commonDTO) {
                        Logcat.d(it)
                        LoadingUtil.hide(commonDTO.customProgressDialog)
                        val callbackName = commonDTO.sp.getValue(Constants.SUCC_FUNC, "")!!
                        DataSend.getInstance().webViewSend(
                                resultCd,
                                resultParam,
                                callbackName,
                                commonDTO.webView
                        )
                    }

                }
                //MOTP 입력
                Constants.REQUEST_MOTP_INPUT -> {
                    val manaer = SecureKeyPadManager()
                    //val firstPin = manaer.keyPadActivityOff_encryptedString(data)
                    val firstPin = if (data?.getStringExtra(commonDTO.activity.resources.getString(R.string.FIN_FIRST))
                                    .isNullOrEmpty()
                    ) "" else data?.getStringExtra(
                            commonDTO.activity.resources.getString(R.string.FIN_FIRST)
                    )!!
                    commonDTO.sp.put(Constants.MOTP_FIN_NUM, firstPin)
                    Logcat.d("TestActivity보안키패드 IS_FIRST_PIN : $firstPin");
                    Logcat.d("doRegisterPin2")

                    //핀번호 유효성체크
                    if (Constants.CHECK_VALIDATION) {
                        ValidationUtil().checkPinValidation(commonDTO, firstPin, "MOTP")
                    } else {
                        MotpManger().doRegisterMotp2(commonDTO)
                    }
                }
                //MOTP 확인
                Constants.REQUEST_MOTP_CONFIRM -> {
                    //val secondPin = SecureKeyPadManager().keyPadActivityOff_encryptedString(data)
                    val secondPin = if (data?.getStringExtra(commonDTO.activity.resources.getString(R.string.FIN_FIRST))
                                    .isNullOrEmpty()
                    ) "" else data?.getStringExtra(
                            commonDTO.activity.resources.getString(R.string.FIN_FIRST)
                    )!!
                    val firstPin = commonDTO.sp.getValue(Constants.MOTP_FIN_NUM, "")
                    Logcat.d("MOTP등록 firstPin : $firstPin")
                    Logcat.d("MOTP등록 secondPin : $secondPin")

                    //키패드 입력번호 비교
                    if (secondPin.equals(firstPin)) {
                        Logcat.d("일치")
                        //commonDTO.sp.put("IS_FIRST_PIN", "")
                        val authToken = commonDTO.sp.getValue(Constants.M_AUTH_TOKEN, "")
                        Logcat.d("사설인증 authToken : $authToken")

                        //서버에서온 토큰값 비교
                        if (authToken != null && !"".equals(secondPin)) {
                            MotpManger().doStoreTokenMotp(authToken, secondPin, commonDTO)
                            //BaseActivity().alertDlg("MOTP 성공",commonDTO.activity)
                            val resultCd = commonDTO.activity.resources.getString(R.string.resultCd_ok)
                            var resultParam = JSONObject()
                            resultParam.put(commonDTO.activity.resources.getString(R.string.CUST_NO), commonDTO.sp.getValue("custNo", ""))
                            resultParam.put("AUTH_TOKEN", authToken)
                            val callbackName = commonDTO.sp.getValue(Constants.SUCC_FUNC, "")
                            DataSend.getInstance().webViewSend(
                                    resultCd,
                                    resultParam,
                                    callbackName!!,
                                    commonDTO.webView
                            )
                        } else {
                            BaseActivity().alertDlg("서버에서 응답이 없습니다.", commonDTO.activity)
                            Logcat.d("토큰 미존재")
                        }
                    } else {
                        //비밀번호 틀림
                        val resultCd = "9996"
                        var resultParam = JSONObject()
                        val callbackName = commonDTO.sp.getValue(Constants.SUCC_FUNC, "")
                        DataSend.getInstance().webViewSend(
                                resultCd,
                                resultParam,
                                callbackName!!,
                                commonDTO.webView
                        )
                    }
                }
                //MOTP 서명
                Constants.REQUEST_MOTP_AUTH -> {
                    Logcat.d("====Motp 서명====")
                    if (data == null) return

                    // 토큰 클라이언트 생성
                    val oc = SafetokenFsbOtpClient.getInstance(commonDTO.activity)

                    // mOTP 토큰 체크
                    var motpTokenList = oc.tokenList
                    // 전자서명
                    try {
                        //val inputPin = SecureKeyPadManager().keyPadActivityOff_encryptedString(data)
                        val inputPin = if (data?.getStringExtra(commonDTO.activity.resources.getString(R.string.FIN_FIRST))
                                        .isNullOrEmpty()
                        ) "" else data?.getStringExtra(
                                commonDTO.activity.resources.getString(R.string.FIN_FIRST)
                        )!!
                        val authMesg = commonDTO.sp.getValue(Constants.AUTH_MESG, "")
                        val randomKey = commonDTO.sp.getValue(Constants.RANDOM_KEY, "")

                        Logcat.d("사설인증 Motp inputPin : $inputPin")
                        Logcat.d("사설인증 Motp authMesg : $authMesg")
                        Logcat.d("사설인증 Motp randomKey : $randomKey")

                        var tnp = oc.sign(
                                motpTokenList.get(0),
                                randomKey,
                                inputPin,
                                authMesg!!.toByteArray()
                        )
                        //MotpManger().doDeleteMotp(commonDTO)
                        val resultCd = commonDTO.activity.resources.getString(R.string.resultCd_ok)
                        var resultParam = JSONObject()
                        resultParam.put(commonDTO.activity.resources.getString(R.string.CUST_NO), commonDTO.sp.getValue("custNo", ""))
                        resultParam.put(commonDTO.activity.resources.getString(R.string.RANDOM_KEY), randomKey)
                        resultParam.put(commonDTO.activity.resources.getString(R.string.AUTH_MESG), authMesg)
                        resultParam.put(commonDTO.activity.resources.getString(R.string.SIGN), tnp)
                        val callbackName = commonDTO.sp.getValue(Constants.SUCC_FUNC, "")
                        DataSend.getInstance().webViewSend(
                                resultCd,
                                resultParam,
                                callbackName!!,
                                commonDTO.webView
                        )

                    } catch (e: SafetokenException) {
                        Logcat.e("에러입니다")
                        val resultCd = "9999"
                        val callbackName = commonDTO.sp.getValue(Constants.SUCC_FUNC, "")
                        DataSend.getInstance().webViewSend(resultCd, callbackName!!)
                        //BaseActivity().alertDlg("인증실패", commonDTO.activity)
                    }
                }
                //스마트앱 입력
                Constants.REQUEST_SMARTAPP_INPUT -> {
                    //val firstPin = SecureKeyPadManager().keyPadActivityOff_encryptedString(data)
                    val firstPin = if (data?.getStringExtra(commonDTO.activity.resources.getString(R.string.FIN_FIRST))
                                    .isNullOrEmpty()
                    ) "" else data?.getStringExtra(
                            commonDTO.activity.resources.getString(R.string.FIN_FIRST)
                    )!!
                    commonDTO.sp.put(Constants.SMARTAPP_FIN_NUM, firstPin)
                    Logcat.d("스마트앱 IS_FIRST_PIN : $firstPin");
                    Logcat.d("REQUEST_SMARTAPP_INPUT")

                    //핀번호 유효성체크
                    if (Constants.CHECK_VALIDATION) {
                        ValidationUtil().checkPinValidation(commonDTO, firstPin, "SMARTAPP")
                    } else {
                        SmartAppManger().doRegisterSmartApp2(commonDTO)
                    }
                }
                //스마트앱 확인
                Constants.REQUEST_SMARTAPP_CONFIRM -> {
                    //val secondPin = SecureKeyPadManager().keyPadActivityOff_encryptedString(data)
                    val secondPin = if (data?.getStringExtra(commonDTO.activity.resources.getString(R.string.FIN_FIRST))
                                    .isNullOrEmpty()
                    ) "" else data?.getStringExtra(
                            commonDTO.activity.resources.getString(R.string.FIN_FIRST)
                    )!!
                    val firstPin = commonDTO.sp.getValue(Constants.SMARTAPP_FIN_NUM, "")
                    Logcat.d("스마트앱 firstPin : $firstPin")
                    Logcat.d("스마트앱 secondPin : $secondPin")

                    //키패드 입력번호 비교
                    if (secondPin.equals(firstPin)) {
                        Logcat.d("일치")
                        //commonDTO.sp.put("IS_FIRST_PIN", "")
                        val authToken = commonDTO.sp.getValue(Constants.A_AUTH_TOKEN, "")
                        Logcat.d("사설인증 authToken : $authToken")

                        //서버에서온 토큰값 비교
                        if (authToken != null && !"".equals(secondPin)) {
                            SmartAppManger().doStoreTokenSmartApp(authToken, secondPin, commonDTO)
                            //BaseActivity().alertDlg("MOTP 성공",commonDTO.activity)
                            val resultCd = commonDTO.activity.resources.getString(R.string.resultCd_ok)
                            var resultParam = JSONObject()
                            resultParam.put(commonDTO.activity.resources.getString(R.string.CUST_NO), commonDTO.sp.getValue("custNo", ""))
                            resultParam.put("AUTH_TOKEN", authToken)
                            val callbackName = commonDTO.sp.getValue(Constants.SUCC_FUNC, "")
                            DataSend.getInstance().webViewSend(resultCd, resultParam, callbackName!!)
                        } else {
                            BaseActivity().alertDlg("서버에서 응답이 없습니다.", commonDTO.activity)
                            Logcat.d("토큰 미존재")
                        }
                    } else {
                        //비밀번호 틀림
                        val resultCd = "9996"
                        var resultParam = JSONObject()
                        val callbackName = commonDTO.sp.getValue(Constants.SUCC_FUNC, "")
                        DataSend.getInstance().webViewSend(
                                resultCd,
                                resultParam,
                                callbackName!!,
                                commonDTO.webView
                        )
                    }
                }
                //스마트앱 서명
                Constants.REQUEST_SMARTAPP_AUTH -> {
                    if (data == null) return

                    // 토큰 클라이언트 생성
                    val ac = SafetokenFsbAuthClient.getInstance(commonDTO.activity)

                    // mOTP 토큰 체크
                    var motpTokenList = ac.tokenList
                    // 전자서명
                    try {
                        //val inputPin = SecureKeyPadManager().keyPadActivityOff_encryptedString(data)
                        val inputPin = if (data?.getStringExtra(commonDTO.activity.resources.getString(R.string.FIN_FIRST))
                                        .isNullOrEmpty()
                        ) "" else data?.getStringExtra(
                                commonDTO.activity.resources.getString(R.string.FIN_FIRST)
                        )!!
                        val authMesg = commonDTO.sp.getValue(Constants.AUTH_MESG, "")
                        val randomKey = commonDTO.sp.getValue(Constants.RANDOM_KEY, "")

                        Logcat.d("사설인증 스마트앱 inputPin : $inputPin")
                        Logcat.d("사설인증 스마트앱 authMesg : $authMesg")
                        Logcat.d("사설인증 스마트앱 randomKey : $randomKey")

                        //var tnp = tc.sign(tokenRef,randomKey, inputPin, authMesg?.toByteArray())
                        var tnp = ac.sign(
                                motpTokenList.get(0),
                                randomKey,
                                inputPin,
                                authMesg!!.toByteArray()
                        )

                        Logcat.d("사설인증 스마트앱 로그인")

                        val resultCd = commonDTO.activity.resources.getString(R.string.resultCd_ok)
                        var resultParam = JSONObject()
                        resultParam.put(commonDTO.activity.resources.getString(R.string.CUST_NO), commonDTO.sp.getValue("custNo", ""))
                        resultParam.put(commonDTO.activity.resources.getString(R.string.RANDOM_KEY), randomKey)
                        resultParam.put(commonDTO.activity.resources.getString(R.string.AUTH_MESG), authMesg)
                        resultParam.put(commonDTO.activity.resources.getString(R.string.SIGN), tnp)
                        val callbackName = commonDTO.sp.getValue(Constants.SUCC_FUNC, "")
                        DataSend.getInstance().webViewSend(
                                resultCd,
                                resultParam,
                                callbackName!!,
                                commonDTO.webView
                        )

                    } catch (e: SafetokenException) {
                        Logcat.e("에러입니다")
                        val resultCd = "9999"
                        val callbackName = commonDTO.sp.getValue(Constants.SUCC_FUNC, "")
                        DataSend.getInstance().webViewSend(resultCd, callbackName!!)
                        BaseActivity().alertDlg("인증실패", commonDTO.activity)
                    }
                }


                Constants.KSW_Activity_CertSign -> {
                    Logcat.d("공동인증서 보안키패드 후처리 : 정상")
                    //입력된 비밀번호
                    // val inputPw = SecureKeyPadManager().keyPadActivityOff_encval(data)

                    val inputPw = if (data?.getStringExtra(commonDTO.activity.resources.getString(R.string.FIN_FIRST))
                                    .isNullOrEmpty()
                    ) "" else data?.getStringExtra(
                            commonDTO.activity.resources.getString(R.string.FIN_FIRST)
                    )!!


                    var rrnCheck1 = 1
                    var pwdCheck1 = false


                    // 1. 주민번호 검증
                    try {
                        val password: ProtectedData = SecureData(
                                CryptoUtil.getInstace().decrypt(inputPw).toByteArray()
                        )
                        val textrrn = CryptoUtil.getInstace().decrypt(commonDTO.rbrno)
                        Logcat.d("textrrn == $textrrn")
                        rrnCheck1 = KSCertificateManager.userVerify(
                                commonDTO.userCert,
                                textrrn.toByteArray(),
                                password
                        )
                        Logcat.d("인증서 명 검증 == $rrnCheck1")
                        if (rrnCheck1 == 0) {
                            Logcat.d("인증서 주민번호 검증 성공")
                        } else {
                            Logcat.d("인증서 주민번호 검증 실패")
                        }
                    } catch (e: java.lang.Exception) {
                        Logcat.e("에러입니다 [${e.message}]")
                    }
                    try {
                        Logcat.d("비밀번호" + CryptoUtil.getInstace().decrypt(inputPw))
                        val password: ProtectedData = SecureData(
                                CryptoUtil.getInstace().decrypt(inputPw).toByteArray()
                        )
                        pwdCheck1 = KSCertificateManager.checkPwd(commonDTO.userCert, password)
                    } catch (e: java.lang.Exception) {
                        Logcat.d("복호화 오류")
                    }


                    if (pwdCheck1) {
                        // 2 . 비밀번호 검증
                        if (rrnCheck1 == 0) {
                            var esgnMsgOrgnlCtns = ""

                            //서명 원본 메세지
                            val plainText = commonDTO.singData
                            Logcat.d("plainText : $plainText")
                            var signature: ByteArray? = ByteArray(4096)
                            var randomResult = ""

                            //서명
                            try {
                                //plainText="서명"
                                //signature = KSSign.sign(KSSign.KOSCOM, commonDTO.userCert, plainText.toByteArray(), SecureData(CryptoUtil.getInstace().decrypt(inputPw).toByteArray()))
                                signature = KSSign.sign(
                                        KSSign.CMS,
                                        commonDTO.userCert,
                                        plainText.toByteArray(),
                                        SecureData(
                                                CryptoUtil.getInstace().decrypt(
                                                        inputPw
                                                ).toByteArray()
                                        )
                                )

                                //signature=KSSign.cmsSign(commonDTO.userCert,plainText.toByteArray(),SecureData(CryptoUtil.getInstace().decrypt(inputPw).toByteArray()))
                                //KSSign.sin
                                var encodedSignResult: ByteArray
                                encodedSignResult = KSBase64.encode(signature)
                                esgnMsgOrgnlCtns = String(encodedSignResult)
                                Logcat.d("cms")
                                Logcat.d("서명 : $esgnMsgOrgnlCtns")

                                var encodedSignResult2: ByteArray
                                encodedSignResult2 = KSBase64.encode(commonDTO.userCert.vidMsg)
                                randomResult = String(encodedSignResult2)
                                //Logcat.d("cms : $esgnMsgOrgnlCtns")

//                                signature = KSSign.sign(KSSign.KOSCOM, commonDTO.userCert, plainText.toByteArray(), SecureData(CryptoUtil.getInstace().decrypt(inputPw).toByteArray()))
//                                encodedSignResult = KSBase64.encode(signature)
//                                esgnMsgOrgnlCtns = String(encodedSignResult)
//                                Logcat.d("KSSign.KOSCOM : $esgnMsgOrgnlCtns")
//                                signature = KSSign.sign(KSSign.KOSCOM_BRIEF, commonDTO.userCert, plainText.toByteArray(), SecureData(CryptoUtil.getInstace().decrypt(inputPw).toByteArray()))
//                                encodedSignResult = KSBase64.encode(signature)
//                                esgnMsgOrgnlCtns = String(encodedSignResult)
//                                Logcat.d("KSSign.KOSCOM_BRIEF : $esgnMsgOrgnlCtns")
//                                Logcat.d("KSSign.CMS : $esgnMsgOrgnlCtns")
//                                signature = KSSign.sign(KSSign.BRIEF, commonDTO.userCert, plainText.toByteArray(), SecureData(CryptoUtil.getInstace().decrypt(inputPw).toByteArray()))
//                                encodedSignResult = KSBase64.encode(signature)
//                                esgnMsgOrgnlCtns = String(encodedSignResult)
//                                Logcat.d("KSSign.BRIEF : $esgnMsgOrgnlCtns")
//                                signature = KSSign.sign(KSSign.CMS, commonDTO.userCert, plainText.toByteArray(), SecureData(CryptoUtil.getInstace().decrypt(inputPw).toByteArray()))
//                                encodedSignResult = KSBase64.encode(signature)
//                                esgnMsgOrgnlCtns = String(encodedSignResult)

                            } catch (e: KSException) {
                                Logcat.e("에러입니다")
                            }
                            Logcat.d("인증서 비밀번호가 일치합니다.")
                            //결과
                            try {
                                data?.putExtra("crtsNm", commonDTO.userCert.subjectName)
                                data?.putExtra("crtsKeyInf", commonDTO.userCert.publicKey_HexLow)
                                data?.putExtra("crtsDn", commonDTO.userCert.issuerDn)
                                data?.putExtra("userCertDn", commonDTO.userCert.subjectDn)
                                data?.putExtra("esgnCtns", esgnMsgOrgnlCtns)
                                data?.putExtra("esgnMsgOrgnlCtns", plainText)
                                data?.putExtra("vidRandom", randomResult)
                                commonDTO.activity.setResult(-1, data)
                                commonDTO.activity.finish()
                                ////commonDTO.//activity.//overridePendingTransition(R.anim.slide_up, R.anim.slide_bottom)
                            } catch (e: Exception) {
                                Logcat.e("에러입니다")
                            }
                        } else {
                            BaseActivity().alertDlg("인증서 실명번호가 틀렸습니다.", commonDTO.activity)
                            Logcat.d("인증서 실명번호가 틀렸습니다.")
                            //로딩
                            try {
                                if (BaseActivity().customProgressDialog != null) {
                                    BaseActivity().customProgressDialog!!.Hide()
                                }
                            } catch (e: Exception) {
                                Logcat.e("에러입니다")
                            }
                        }
                    } else {
                        //로딩
                        Logcat.d("비밀번호가 틀렸습니다.")
                        try {
                            if (BaseActivity().customProgressDialog != null) {
                                BaseActivity().customProgressDialog!!.Hide()
                            }
                        } catch (e: Exception) {
                            Logcat.e("에러입니다")
                        }
                        BaseActivity().alertDlgFinish("인증서 비밀번호가 틀렸습니다.", commonDTO.activity)

                    }


                }
                Constants.KSW_Cert_Activity_02 -> {
                    Logcat.d("ActivityResult KSW_Cert_Activity_02")
                    commonDTO.activity.setResult(AppCompatActivity.RESULT_OK, data)
                    commonDTO.activity.finish()
                    //commonDTO.//activity.//overridePendingTransition(R.anim.slide_up, R.anim.slide_bottom)
                }

                //공동인증서 서명완료후 웹뷰 투척
                Constants.KSW_Cert_Activity_01 -> {
                    Logcat.d("ActivityResult KSW_Cert_Activity_01")
                    val callbackName = commonDTO.sp.getValue(Constants.SUCC_FUNC, "")
                    val resultCd = commonDTO.activity.resources.getString(R.string.resultCd_ok)
                    var resultParam = JSONObject()
                    resultParam.put(
                            "crtsNm",
                            if (data?.getStringExtra("crtsNm")
                                            .isNullOrEmpty()
                            ) "" else data?.getStringExtra(
                                    "crtsNm"
                            )
                    )
                    resultParam.put(
                            "crtsKeyInf",
                            if (data?.getStringExtra("crtsKeyInf")
                                            .isNullOrEmpty()
                            ) "" else data?.getStringExtra(
                                    "crtsKeyInf"
                            )
                    )
                    resultParam.put(
                            "crtsDn",
                            if (data?.getStringExtra("crtsDn")
                                            .isNullOrEmpty()
                            ) "" else data?.getStringExtra(
                                    "crtsDn"
                            )
                    )
                    resultParam.put(
                            "userCertDn",
                            if (data?.getStringExtra("userCertDn")
                                            .isNullOrEmpty()
                            ) "" else data?.getStringExtra(
                                    "userCertDn"
                            )
                    )
                    resultParam.put(
                            "esgnCtns",
                            if (data?.getStringExtra("esgnCtns")
                                            .isNullOrEmpty()
                            ) "" else data?.getStringExtra(
                                    "esgnCtns"
                            )
                    )
                    resultParam.put(
                            "esgnMsgOrgnlCtns",
                            if (data?.getStringExtra("esgnMsgOrgnlCtns")
                                            .isNullOrEmpty()
                            ) "" else data?.getStringExtra(
                                    "esgnMsgOrgnlCtns"
                            )
                    )
                    resultParam.put(
                            "vidRandom",
                            if (data?.getStringExtra("vidRandom")
                                            .isNullOrEmpty()
                            ) "" else data?.getStringExtra(
                                    "vidRandom"
                            )
                    )

                    DataSend.getInstance().webViewSend(resultCd, resultParam, callbackName!!, commonDTO.webView)
                }
                Constants.REQUEST_SCRAPING_SIGN_CERT -> {
                    if (data == null) return

                    //입력된 비밀번호
                    val inputPw = SecureKeyPadManager().keyPadActivityOff(data)
                    //비밀번호 검증
                    var pwdCheck1 = false
                    try {
                        pwdCheck1 = KSCertificateManager.checkPwd(
                                commonDTO.userCert,
                                SecureData(CryptoUtil.getInstace().decrypt(inputPw).toByteArray())
                        )
                    } catch (e: java.lang.Exception) {
                        Logcat.d("복호화 오류")
                    }
                    if (pwdCheck1) {
                        var userCertDn = commonDTO.userCert.subjectDn              //사용자dn
                        var userCertExpireDate = commonDTO.userCert.expiredTime            //만료일
                        var cipherData = data?.getStringExtra("cipherData")
                        var iRealDataLength = data?.getIntExtra("iRealDataLength", 0)
                        var secureKey = data?.getByteArrayExtra("secureKey")

                    } else {
                        BaseActivity().alertDlg("인증서 비밀번호가 틀렸습니다.", commonDTO.activity)
                        Logcat.d("인증서 비밀번호가 틀렸습니다.")
                        //로딩
                        commonDTO.activity.runOnUiThread {
                            commonDTO.customProgressDialog.hide()
                        }
                    }
                    //로딩
                    commonDTO.activity.runOnUiThread {
                        commonDTO.customProgressDialog.show()
                    }
                }
                Constants.KSW_Activity_CHANGE_PWD_01 -> {
                    val inputPw = SecureKeyPadManager().keyPadActivityOff(data)
                }
                Constants.REQUEST_KEYPAD -> {
                    val inputNum = if (data?.getStringExtra(commonDTO.activity.resources.getString(R.string.FIN_FIRST))
                                    .isNullOrEmpty()
                    ) "" else data?.getStringExtra(
                            commonDTO.activity.resources.getString(R.string.FIN_FIRST)
                    )
                    Logcat.d("inputNum : $inputNum")
                    val resultCd = commonDTO.activity.resources.getString(R.string.resultCd_ok)
                    var resultParam = JSONObject()
                    resultParam.put("encData", inputNum)
                    val callbackName = commonDTO.sp.getValue(Constants.SUCC_FUNC, "")
                    DataSend.getInstance().webViewSend(resultCd, resultParam, callbackName!!, commonDTO.webView)
                }
                Constants.REQUEST_KEYPAD_QWETY -> {
                    val inputNum = SecureKeyPadManager().keyPadActivityOff_encryptedString2(data)
                    Logcat.d("inputNum : $inputNum")
                    val resultCd = commonDTO.activity.resources.getString(R.string.resultCd_ok)
                    var resultParam = JSONObject()
                    resultParam.put("encData", inputNum)
                    val callbackName = commonDTO.sp.getValue(Constants.SUCC_FUNC, "")
                    DataSend.getInstance().webViewSend(resultCd, resultParam, callbackName!!, commonDTO.webView)
                }
                Constants.KeyPad_Custom_Activity -> {
                    val inputNum = SecureKeyPadManager().keyPadActivityOff_encryptedString2(data)
                    Logcat.d("inputNum : $inputNum")
                    val resultCd = commonDTO.activity.resources.getString(R.string.resultCd_ok)
                    var resultParam = JSONObject()
                    resultParam.put("encData", inputNum)
                    val callbackName = commonDTO.sp.getValue(Constants.SUCC_FUNC, "")
                    DataSend.getInstance().webViewSend(resultCd, resultParam, callbackName!!, commonDTO.webView)
                }
                //공동인증서 인증후 스크래핑 처리
                Constants.KSW_Activity_SCRAPING -> {
                    Logcat.d("공동인증서 보안키패드 후처리 : 정상")

                    //입력된 비밀번호
                    val inputPw = if (data?.getStringExtra(commonDTO.activity.resources.getString(R.string.FIN_FIRST))
                                    .isNullOrEmpty()
                    ) "" else data?.getStringExtra(
                            commonDTO.activity.resources.getString(R.string.FIN_FIRST)
                    )!!
                    Logcat.d("스크랩핑pp$inputPw")
                    //비밀번호 검증
                    var pwdCheck1 = false
                    try {
                        pwdCheck1 = KSCertificateManager.checkPwd(
                                commonDTO.userCert,
                                SecureData(CryptoUtil.getInstace().decrypt(inputPw).toByteArray())
                        )
                    } catch (e: java.lang.Exception) {
                        Logcat.d("복호화 오류")
                    }

                    if (pwdCheck1) {
                        Logcat.d("인증서 비밀번호가 일치합니다. 스크랩핑 시작")



                        commonDTO.sp.put(Constants.SCRAPTEMP, inputPw)
                        //스크래핑 처리
                        Scraping().allScrapingInit(commonDTO, inputPw)
                    } else {
                        //로딩
                        try {
                            //if (BaseActivity().customProgressDialog != null) {
                            //    BaseActivity().customProgressDialog!!.Hide()
                            // }
                        } catch (e: Exception) {
                            Logcat.d("로딩바 에러")
                        }
                        Logcat.d("인증서 비밀번호가 틀렸습니다.")
                        //안내
                        try {
                            val builder = AlertDialog.Builder(commonDTO.activity)
                            builder.setMessage("인증서 비밀번호가 틀렸습니다.")
                            builder.setCancelable(false)
                            builder.setPositiveButton(
                                    "확인"
                            ) { _, _ ->
                                commonDTO.activity.setResult(AppCompatActivity.RESULT_CANCELED)
                                commonDTO.activity.finish()
                            }
                            builder.show()

                        } catch (e: WindowManager.BadTokenException) {
                            Logcat.d("알림창생성에러")
                        } catch (e: Exception) {
                            Logcat.d("알림창에러")
                        }

                    }
                }
                //보안이미지 입력후 처리
                Constants.REQUEST_SCRAPING_CAPTCHA_INPUT -> {
                    LoadingUtil.show(commonDTO.customProgressDialog)
                    LoadingUtil.showAndText(
                            commonDTO.customProgressDialog, commonDTO.activity.getString(
                            R.string.sc_txt_02
                    ), commonDTO.activity
                    )

                    //보안문자 입력후 다음 진행
                    Scraping().allScrapingRun(commonDTO, data!!)

                }
                Constants.KSW_Activity_02 -> {
                    Logcat.d("ActivityResult KSW_Activity_02")
                    commonDTO.activity.setResult(AppCompatActivity.RESULT_OK, data)
                    commonDTO.activity.finish()
                    //commonDTO.//activity.//overridePendingTransition(R.anim.slide_up, R.anim.slide_bottom)
                }
                Constants.KSW_Activity_01 -> {
                    Logcat.d("ActivityResult KSW_Activity_01")
                    //결과 셋팅
                    val resultCd = commonDTO.activity.resources.getString(R.string.resultCd_ok)
                    val resultParam = JSONObject(
                            if (data?.getStringExtra("resultJson")
                                            .isNullOrEmpty()
                            ) "" else data?.getStringExtra(
                                    "resultJson"
                            )
                    )
                    val callbackName = commonDTO.sp.getValue(Constants.SUCC_FUNC, "")
                    DataSend.getInstance().webViewSend(resultCd, resultParam, callbackName!!, commonDTO.webView)
                }
                Constants.OCR_Activity_01 -> {
                    //결과 셋팅
                    //intent to JsonObject
                    Logcat.d("ActivityResult OCR_Activity_01")
                    val resultCd = commonDTO.activity.resources.getString(R.string.resultCd_ok)
                    val resultParam = JSONObject()
                    resultParam.apply {
                        data?.extras?.let{
                            for(key in it.keySet()){
                                put(key,it.getString(key,""))
                            }
                        }
                    }
                    Logcat.d("===OCR결과===")
                    val keys = resultParam.keys()
                    for (key in keys) {
                        try {
                            val value = CryptoUtil.getInstace().decrypt(resultParam.getString(key))
                            Logcat.d("OCR결과 [$key]: $value")
                        } catch (e: Exception) {
                            Logcat.d("OCR결과 [$key]: ${resultParam.getString(key)}")
                        }
                    }
                    val callbackName = commonDTO.sp.getValue(Constants.SUCC_FUNC, "")
                    DataSend.getInstance().webViewSend(resultCd, resultParam, callbackName!!, commonDTO.webView)
                    LoadingUtil.hide(commonDTO.customProgressDialog)
                }
                Constants.OCR_Activity_02 -> {
                    Logcat.d("ActivityResult OCR_Activity_02")
                    commonDTO.activity.setResult(AppCompatActivity.RESULT_OK, data)
                    commonDTO.activity.finish()
                    //commonDTO.//activity.//overridePendingTransition(R.anim.slide_up, R.anim.slide_bottom)
                }
                Constants.OCR_Activity_03 -> {
                    Logcat.d("ActivityResult OCR_Activity_03")
                    commonDTO.activity.setResult(AppCompatActivity.RESULT_OK, data)
                    commonDTO.activity.finish()
                    //commonDTO.//activity.//overridePendingTransition(R.anim.slide_up, R.anim.slide_bottom)
                }
                Constants.OCR_Activity_04 -> {
                    Logcat.d("ActivityResult OCR_Activity_04")
                    commonDTO.activity.setResult(AppCompatActivity.RESULT_OK, data)
                    commonDTO.activity.finish()
                    //commonDTO.//activity.//overridePendingTransition(R.anim.slide_up, R.anim.slide_bottom)
                }
                Constants.Native_Camera -> {
                    val options = BitmapFactory.Options()
                    options.inSampleSize = 8

                    //결과 셋팅
                    val resultCd = commonDTO.activity.resources.getString(R.string.resultCd_ok)
                    val resultParam = JSONObject()
//                    resultParam.put(
//                        "photoStr", CryptoUtil.encrypt(
//                            Base64.encodeToString(
//                                OCRUtil().bitmapToByteArray(
//                                    BitmapFactory.decodeFile(
//                                        commonDTO.cameraFile.getAbsolutePath(),
//                                        options
//                                    ), 70
//                                ), Base64.NO_WRAP
//                            )
//                        )
//                    )
                    resultParam.put(
                            "photoStr", CryptoUtil.getInstace().encrypt(
                            Base64.encodeToString(
                                    OCRUtil.bitmapToByteArray(
                                            BitmapFactory.decodeFile(
                                                    commonDTO.cameraFile.absolutePath,
                                                    options
                                            )
                                    ), Base64.NO_WRAP
                            )
                    )
                    )

                    LoadingUtil.show(commonDTO.customProgressDialog)
                    Handler(Looper.myLooper()!!).postDelayed({
                        LoadingUtil.hide(commonDTO.customProgressDialog)
                        val callbackName = commonDTO.sp.getValue(Constants.SUCC_FUNC, "")
                        DataSend.getInstance().webViewSend(resultCd, resultParam, callbackName!!, commonDTO.webView)
                    }, 1500)

                }

                else ->{
                    Logcat.d("TestActivity.onActivityResult : 테스트 후처리");
                }
            }
        }else if(0 == resultCode){
            Logcat.d("화면작업 취소");
            LoadingUtil.hide(commonDTO.customProgressDialog)
            when(requestCode) {
                Constants.OCR_Activity_01 -> {
                    Logcat.d("ActivityResult OCR취소")
                    val callbackName = commonDTO.sp.getValue(Constants.SUCC_FUNC, "")
                    val resultCd = "9998"
                    val resultParam = JSONObject()
                    resultParam.put("msg", "작업을 취소하셨습니다")
                    DataSend.getInstance().webViewSend(resultCd, resultParam, callbackName!!, commonDTO.webView)
                }
                //공동인증서 비밀번호 틀렸을때
                Constants.KSW_Activity_02 -> {
                    Logcat.d("ActivityResult KSW_Activity_02")

                }
                //공동인증 -> 공동인증서 선택후 취호
                Constants.KSW_Cert_Activity_02 -> {
                    Logcat.d("ActivityResult KSW_Cert_Activity_02")
                    //commonDTO.activity.setResult(AppCompatActivity.RESULT_CANCELED, data)
                    //commonDTO.activity.finish()
                }
                Constants.OCR_Activity_02 -> {
                    Logcat.d("ActivityResult OCR_Activity_02")
                    //재촬영 시
                    //commonDTO.activity.setResult(AppCompatActivity.RESULT_CANCELED, data)
                    //commonDTO.activity.finish()
                }
                Constants.OCR_Activity_03 -> {
                    Logcat.d("ActivityResult OCR_Activity_03")
                    //commonDTO.activity.setResult(AppCompatActivity.RESULT_CANCELED, data)
                    //commonDTO.activity.finish()
                }
                Constants.OCR_Activity_04 -> {
                    Logcat.d("ActivityResult OCR_Activity_04")
                    commonDTO.activity.setResult(AppCompatActivity.RESULT_CANCELED, data)
                    commonDTO.activity.finish()
                    //commonDTO.//activity.//overridePendingTransition(R.anim.slide_up, R.anim.slide_bottom)
                }
                Constants.KSW_Activity_SCRAPING -> {
                    Logcat.d("ActivityResult KSW_Activity_SCRAPING")
                    commonDTO.activity.setResult(AppCompatActivity.RESULT_CANCELED, data)
                    commonDTO.activity.finish()
                    //commonDTO.//activity.//overridePendingTransition(R.anim.slide_up, R.anim.slide_bottom)
                }
                Constants.KSW_Activity_CertSign -> {
                    Logcat.d("ActivityResult KSW_Activity_CertSign")
                    commonDTO.activity.setResult(AppCompatActivity.RESULT_CANCELED, data)
                    commonDTO.activity.finish()
                    //commonDTO.//activity.//overridePendingTransition(R.anim.slide_up, R.anim.slide_bottom)
                }
                Constants.REQUEST_SCRAPING_CAPTCHA_INPUT -> {
                    Logcat.d("ActivityResult KSW_Activity_CertSign")
                    commonDTO.activity.setResult(AppCompatActivity.RESULT_CANCELED, data)
                    commonDTO.activity.finish()
                    //commonDTO.//activity.//overridePendingTransition(R.anim.slide_up, R.anim.slide_bottom)
                }
//                Constants.KSW_Cert_Activity_01 -> {
//                    Logcat.d("ActivityResult KSW_Cert_Activity_01")
//                    commonDTO.activity.setResult(AppCompatActivity.RESULT_CANCELED, data)
//                    commonDTO.activity.finish()
//                }

                Constants.REQUEST_PIN_AUTH -> {
                    Logcat.d("ActivityResult REQUEST_PIN_AUTH")
                    //commonDTO.activity.setResult(AppCompatActivity.RESULT_CANCELED, data)
                    //commonDTO.activity.finish()
                }
                else ->{
                    val callbackName=commonDTO.sp.getValue(Constants.SUCC_FUNC, "")
                    val resultCd ="9998"
                    val resultParam= JSONObject()
                    resultParam.put("msg", "작업을 취소하셨습니다")
                    DataSend.getInstance().webViewSend(resultCd, resultParam, callbackName!!, commonDTO.webView)
                }
            }
        }
        //스크랩핑 화면 문구 재실행
        else if(1 == resultCode){
            val spw=if (commonDTO.sp.getValue(Constants.SCRAPTEMP, "").isNullOrEmpty()) "" else commonDTO.sp.getValue(
                    Constants.SCRAPTEMP,
                    ""
            ).toString()
            Logcat.d("spw:$spw")
            Scraping().allScrapingInit(commonDTO, spw)
        }else{
            Logcat.d("화면작업 불량")
        }
    }
}