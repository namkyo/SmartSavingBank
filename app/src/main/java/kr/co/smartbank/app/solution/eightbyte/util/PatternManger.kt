package kr.co.smartbank.app.solution.eightbyte.util

import android.app.Activity
import android.content.Intent
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import eightbyte.safetoken.SafetokenProof
import eightbyte.safetoken.SafetokenSimpleClient
import eightbyte.safetoken.biometric.SafetokenBiometricAuth
import eightbyte.safetoken.biometric.SafetokenBiometricAuthCallback
import eightbyte.safetoken.biometric.SafetokenBiometricAuthError
import kr.co.smartbank.app.R
import kr.co.smartbank.app.config.Constants
import kr.co.smartbank.app.process.CommonDTO
import kr.co.smartbank.app.solution.eightbyte.view.PatternPrompt
import kr.co.smartbank.app.solution.eightbyte.view.PatternPromptCallback
import kr.co.smartbank.app.util.DataSend
import kr.co.smartbank.app.util.Logcat
import kr.co.smartbank.app.view.BaseActivity
import org.json.JSONObject

class PatternManger: DialogFragment() {

    public fun doRegisterPattern(commonDTO: CommonDTO) {
        // 패턴 인증 정보를 저장할 토큰 획득
        val tc = SafetokenSimpleClient.getInstance(commonDTO.activity)
        val tokenRef = tc.token
        // 토큰 저장 여부 확인
        if (tc.isExistToken()) {
            // 저장된 토큰이 있음.
            Logcat.d("저장된 토큰이 있음.")
        } else {
            // 저장된 토큰이 없음.
            Logcat.d("저장된 토큰이 없음.")
        }
        val firstPin = commonDTO.sp.getValue(Constants.FIN_NUM, "")
        Logcat.d("패턴 등록 firstPin"+firstPin)
        val patternPrompt = PatternPrompt.createStoreCredentialDialog("패턴 입력", "취소").setSafetokenClient(tc).setSafetokenRef(tokenRef)
            .setCredentialForStore(firstPin)
        patternPrompt.setPatternPromptCallback(object : PatternPromptCallback() {
            override fun onStoreCredential() {
                patternPrompt.dismiss()
                Toast.makeText(commonDTO.activity, "패턴이 등록되었습니다.", Toast.LENGTH_SHORT).show()
                val resultCd="0000"
                val callbackName=commonDTO.sp.getValue(Constants.SUCC_FUNC,"")
                DataSend.getInstance().webViewSend(resultCd,callbackName!!,commonDTO.webView)
            }
            override fun onError(code: Int, count: Int, max: Int) {
                patternPrompt.dismiss()
                SafetokenBiometricAuthError.ERROR_AUTHENTICATION
                //val errMsg="\ncode : "+code+"\ncount : "+count+"\nmax : "+max
                var errMsg="기타 에러 ($code)"
                if(code == SafetokenBiometricAuthError.ERROR_STORE_CREDENTIAL){
                    errMsg="패턴인증정보 저장 실패"
                }else if (code == SafetokenBiometricAuthError.ERROR_BIOMETRIC_LOCKOUT) {
                    errMsg="생체인증 실패가 많이 일시적으로 사용이 중지되었습니다. 잠시 후 다시 시도하세요."
                } else if (code == SafetokenBiometricAuthError.ERROR_BIOMETRIC_LOCKOUT_PERMANENT) {
                    errMsg="생체인증 실패가 많이 생체인증을 사용할 수 없습니다. 잠금화면 해제 후 사용 가능합니다."
                } else if (code == SafetokenBiometricAuthError.ERROR_BIOMETRIC_NO_BIOMETRICS) {
                    errMsg="생체정보가 등록되어 있지 않습니다. 등록 후 이용하세요."
                } else if (code == SafetokenBiometricAuthError.ERROR_BIOMETRIC_HW_UNAVAILABLE) {
                    errMsg="생체인증 장치를 사용할 수 없습니다. 잠시 후 다시 시도하세요."
                } else if (code == SafetokenBiometricAuthError.ERROR_BIOMETRIC_HW_NOT_PRESENT) {
                    errMsg="생체인증을 지원하지 않는 기기입니다."
                }
                //Toast.makeText(commonDTO.activity, errMsg, Toast.LENGTH_SHORT).show()
                val resultCd="9999"
                var resultParam = JSONObject()
                resultParam.put("msg",errMsg)
                val callbackName=commonDTO.sp.getValue(Constants.SUCC_FUNC,"")
                DataSend.getInstance().webViewSend(resultCd,resultParam,callbackName!!,commonDTO.webView)
            }
            override fun onCancel() {
                patternPrompt.dismiss()
                Toast.makeText(commonDTO.activity, "패턴 등록을 취소하였습니다.", Toast.LENGTH_SHORT).show()
                val resultCd="0000"
                var resultParam = JSONObject()
                val callbackName=commonDTO.sp.getValue(Constants.SUCC_FUNC,"")
                DataSend.getInstance().webViewSend(resultCd,resultParam,callbackName!!,commonDTO.webView)
            }
        })
        if (!patternPrompt.isVisible) {
           patternPrompt.show(commonDTO.supportFragmentManager, PatternPrompt::class.java!!.name)
        }
    }

    fun doAuthPattern(commonDTO: CommonDTO,randomKey: String,authMesg: String) {
        // 전자서명에 사용할 토큰 획득
        val tc = SafetokenSimpleClient.getInstance(commonDTO.activity)
        val tokenRef = tc.token
        if (tokenRef == null) {
            Toast.makeText(commonDTO.activity, "저장된 토큰이 없습니다.", Toast.LENGTH_SHORT).show()
            return
        }

        // 패턴 인증정보가 저장되어 있는지 확인
        if (!tokenRef.bindPattern()) {
            //BaseActivity().alertDlg("패턴 입력 인증수단이 등록되어 있지 않습니다. 인증수단 등록 후 이용해 주세요.")
            // Toast.makeText(activity, "패턴 정보가 등록되어 있지 않습니다.", Toast.LENGTH_SHORT).show()
            return
        }

        // 전자서명을 위한 데이터
        //val authMesg =  commonDTO.sp.getValue("IS_AUTH_MESG","")
        //val randomKey = commonDTO.sp.getValue("IS_RANDOM_KEY","")

        val patternPrompt = PatternPrompt.createGenerateSignDialog("패턴 입력", "취소")
                .setSafetokenClient(tc)
                .setSafetokenRef(tokenRef)
                .setRandomForSign(randomKey)
                .setMsgForSign(authMesg)
        patternPrompt.setPatternPromptCallback(object : PatternPromptCallback() {
            override fun onGenerateSign(tnp: SafetokenProof?, msg: String?) {
                // Toast.makeText(activity, tnp.toString(), Toast.LENGTH_SHORT).show()
                // 패턴 다이얼로그 닫기
                patternPrompt.dismiss()
//                val resultCd="0000"
//                var resultParam=JSONObject()
//                resultParam.put(commonDTO.activity.resources.getString(R.string.CUST_NO),commonDTO.sp.getValue("custNo",""))
//                resultParam.put(commonDTO.activity.resources.getString(R.string.RANDOM_KEY),randomKey)
//                resultParam.put(commonDTO.activity.resources.getString(R.string.AUTH_MESG),authMesg)
//                resultParam.put(commonDTO.activity.resources.getString(R.string.SIGN),tnp)
//                val callbackName=commonDTO.sp.getValue(Constants.SUCC_FUNC,"")
//                DataSend.getInstance().webViewSend(resultCd,resultParam,callbackName!!,commonDTO.webView)

                val loginIntent = Intent().apply {
                    putExtra(commonDTO.activity.resources.getString(R.string.CUST_NO), commonDTO.sp.getValue("custNo",""))
                    putExtra(commonDTO.activity.resources.getString(R.string.RANDOM_KEY), randomKey)
                    putExtra(commonDTO.activity.resources.getString(R.string.AUTH_MESG), authMesg)
                    putExtra(commonDTO.activity.resources.getString(R.string.SIGN), tnp.toString())
                }


                commonDTO.sp.put(Constants.LAST_LOGIN_PLAN,"3")
                commonDTO.activity.setResult(AppCompatActivity.RESULT_OK,loginIntent)
                commonDTO.activity.finish()
                //commonDTO.//activity.//overridePendingTransition(R.anim.slide_up, R.anim.slide_bottom)

            }

            override fun onExceedFail(count: Int, max: Int) {
                patternPrompt.dismiss()

//                val resultCd="9998"
//                val callbackName=commonDTO.sp.getValue(Constants.SUCC_FUNC,"")
//                DataSend.getInstance().webViewSend(resultCd,callbackName!!,commonDTO.webView)
//                val loginFailIntent = Intent()
//                commonDTO.activity.setResult(AppCompatActivity.RESULT_CANCELED,loginFailIntent)
//                commonDTO.activity.finish()
            }

            override fun onError(code: Int, count: Int, max: Int) {
                Toast.makeText(commonDTO.activity, "패턴 인증에 실패하였습니다.", Toast.LENGTH_SHORT).show()

//                val resultCd="9998"
//                val callbackName=commonDTO.sp.getValue(Constants.SUCC_FUNC,"")
//                DataSend.getInstance().webViewSend(resultCd,callbackName!!,commonDTO.webView)
//                val loginFailIntent = Intent()
//                commonDTO.activity.setResult(AppCompatActivity.RESULT_CANCELED,loginFailIntent)
//                commonDTO.activity.finish()
            }

            override fun onCancel() {
                patternPrompt.dismiss()
//                val resultCd="9998"
//                val callbackName=commonDTO.sp.getValue(Constants.SUCC_FUNC,"")
//                DataSend.getInstance().webViewSend(resultCd,callbackName!!,commonDTO.webView)
//                val loginFailIntent = Intent()
//                commonDTO.activity.setResult(AppCompatActivity.RESULT_CANCELED,loginFailIntent)
//                commonDTO.activity.finish()
            }
        })
        if (!patternPrompt.isVisible) {
            patternPrompt.show(commonDTO.supportFragmentManager, PatternPrompt::class.java!!.name)
        }
    }



}