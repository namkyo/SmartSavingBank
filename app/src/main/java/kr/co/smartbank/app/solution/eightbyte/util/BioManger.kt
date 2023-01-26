package kr.co.smartbank.app.solution.eightbyte.util

import android.content.Intent
import android.os.Build
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import eightbyte.safetoken.SafetokenProof
import eightbyte.safetoken.SafetokenSimpleClient
import eightbyte.safetoken.biometric.SafetokenBiometricAuth
import eightbyte.safetoken.biometric.SafetokenBiometricAuthCallback
import eightbyte.safetoken.biometric.SafetokenBiometricAuthError
import kr.co.smartbank.app.R
import kr.co.smartbank.app.config.Constants
import kr.co.smartbank.app.process.CommonDTO
import kr.co.smartbank.app.util.DataSend
import kr.co.smartbank.app.util.Logcat
import org.json.JSONObject

class BioManger {
    companion object{
        private var instance : BioManger? = null
        fun getInstance(): BioManger {
            return instance ?: synchronized(this){
                instance ?: BioManger().also {
                    instance = it
                }
            }
        }
    }
    fun doRegisterBio(commonDTO: CommonDTO) {
        val tokenClient = SafetokenSimpleClient.getInstance(commonDTO.activity)

        // 토큰 저장 여부 확인
        if (!tokenClient.isExistToken) {
            Toast.makeText(commonDTO.activity, "저장된 토큰이 없습니다.", Toast.LENGTH_SHORT).show()
            return
        }

        // 저장한 토큰 객체 조회
        val tokenRef = tokenClient.token

        // 생체인증을 이용하여 토큰 저장
        val tokenBioAuth = SafetokenBiometricAuth(commonDTO.activity).setTitle("지문 등록").setNegativeButton("취소")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            tokenBioAuth.setForceFingerprint(false)
        }else{
            tokenBioAuth.setForceFingerprint(true)
        }
        //        val plainDataByte = NFilterUtils.getInstance().nSaferDecryptWithBase64(plainData)
        //        val encryptedData = EncryptHelper.getSHA256(String(plainDataByte))
        //        // 복호화 후 메모리 삭제
        //        for (i in plainDataByte.indices) {
        //            plainDataByte[i] = 0x01
        //        }

        //기존 지문 등록시
        if(tokenRef.bindBiometric()){
            tokenClient.removeBiometricCredential(tokenRef)
        }
        val firstPin = commonDTO.sp.getValue(Constants.FIN_NUM, "")
        Logcat.d("바이오 등록 firstPin"+firstPin)
        tokenBioAuth.storeCredential(tokenClient, tokenRef, firstPin, commonDTO.supportFragmentManager, object : SafetokenBiometricAuthCallback() {
            override fun onStoreCredential() {
                Toast.makeText(commonDTO.activity, "생체정보 저장 성공", Toast.LENGTH_SHORT).show()
                val resultCd="0000"
                val callbackName=commonDTO.sp.getValue(Constants.SUCC_FUNC,"")
                DataSend.getInstance().webViewSend(resultCd,callbackName!!,commonDTO.webView)
            }

            override fun onError(code: Int, errString: String?) {

                val resultParam = JSONObject()
                // 생체인증 정보 저장시 예외 처리
                if (code == SafetokenBiometricAuthError.ERROR_BIOMETRIC_LOCKOUT) {
                    Toast.makeText(commonDTO.activity, "생체인증 실패가 많이 일시적으로 사용이 중지되었습니다. 잠시 후 다시 시도하세요.", Toast.LENGTH_SHORT).show()
                    resultParam.put("msg","생체인증 실패가 많이 일시적으로 사용이 중지되었습니다. 잠시 후 다시 시도하세요.")

                } else if (code == SafetokenBiometricAuthError.ERROR_BIOMETRIC_LOCKOUT_PERMANENT) {
                    Toast.makeText(commonDTO.activity, "생체인증 실패가 많이 생체인증을 사용할 수 없습니다. 잠금화면 해제 후 사용 가능합니다.", Toast.LENGTH_SHORT).show()
                    resultParam.put("msg","생체인증 실패가 많이 생체인증을 사용할 수 없습니다. 잠금화면 해제 후 사용 가능합니다.")
                } else if (code == SafetokenBiometricAuthError.ERROR_BIOMETRIC_NO_BIOMETRICS) {
                    Toast.makeText(commonDTO.activity, "생체정보가 등록되어 있지 않습니다. 등록 후 이용하세요.", Toast.LENGTH_SHORT).show()
                    resultParam.put("msg","생체정보가 등록되어 있지 않습니다. 등록 후 이용하세요.")
                } else if (code == SafetokenBiometricAuthError.ERROR_BIOMETRIC_HW_UNAVAILABLE) {
                    Toast.makeText(commonDTO.activity, "생체인증 장치를 사용할 수 없습니다. 잠시 후 다시 시도하세요.", Toast.LENGTH_SHORT).show()
                    resultParam.put("msg","생체인증 장치를 사용할 수 없습니다. 잠시 후 다시 시도하세요.")
                } else if (code == SafetokenBiometricAuthError.ERROR_BIOMETRIC_HW_NOT_PRESENT) {
                    Toast.makeText(commonDTO.activity, "생체인증을 지원하지 않는 기기입니다.", Toast.LENGTH_SHORT).show()
                    resultParam.put("msg","생체인증을 지원하지 않는 기기입니다.")
                } else {
                    //Toast.makeText(commonDTO.activity, String.format("[%d]%s", code, errString), Toast.LENGTH_SHORT).show()
                    resultParam.put("msg","기타 "+String.format("[%d]%s", code, errString))
                }
                val resultCd="9998"
                val callbackName = commonDTO.sp.getValue(Constants.SUCC_FUNC, "")
                DataSend.getInstance().webViewSend(resultCd,resultParam,callbackName!!,commonDTO.webView)
            }

            override fun onFail(count: Int) {
                // 생체인증 정보 저장시 인증 오류
                Toast.makeText(commonDTO.activity, "생체인증 오류", Toast.LENGTH_SHORT).show()
                val resultCd="9998"
                val callbackName=commonDTO.sp.getValue(Constants.SUCC_FUNC,"")
                DataSend.getInstance().webViewSend(resultCd,callbackName!!,commonDTO.webView)
            }

            override fun invalidateBiometric() {
                // 생체정보가 변경되어 기존 생체인증 정보를 이용할 수 없음.
                Toast.makeText(commonDTO.activity, "기기에 등록된 생체 정보가 변경되어 생체인증 정보를 초기화 하였습니다. 생체인증 정보를 다시 등록해주세요.", Toast.LENGTH_SHORT).show()
                val resultCd="9998"
                val callbackName=commonDTO.sp.getValue(Constants.SUCC_FUNC,"")
                DataSend.getInstance().webViewSend(resultCd,callbackName!!,commonDTO.webView)
//                val loginFailIntent = Intent()
//                commonDTO.activity.setResult(AppCompatActivity.RESULT_CANCELED,loginFailIntent)
//                commonDTO.activity.finish()
//                //commonDTO.//activity.//overridePendingTransition(R.anim.slide_up, R.anim.slide_bottom)
            }

            override fun onCancel() {
                Toast.makeText(commonDTO.activity, "생체인증등록을 취소하였습니다.", Toast.LENGTH_SHORT).show()
                val resultCd="0000"
                val callbackName=commonDTO.sp.getValue(Constants.SUCC_FUNC,"")
                DataSend.getInstance().webViewSend(resultCd,callbackName!!,commonDTO.webView)
//                val loginFailIntent = Intent()
//                commonDTO.activity.setResult(AppCompatActivity.RESULT_CANCELED,loginFailIntent)
//                commonDTO.activity.finish()
//                //commonDTO.//activity.//overridePendingTransition(R.anim.slide_up, R.anim.slide_bottom)
            }
        })

    }
    fun doAuthBio(commonDTO: CommonDTO,randomKey: String,authMesg: String) {
        val tokenClient = SafetokenSimpleClient.getInstance(commonDTO.activity)
        // 토큰 저장 여부 확인
        if (!tokenClient.isExistToken) {
            Toast.makeText(commonDTO.activity, "저장된 토큰이 없습니다.", Toast.LENGTH_SHORT).show()
            val resultCd="9997"
            val callbackName=commonDTO.sp.getValue(Constants.SUCC_FUNC,"")
            DataSend.getInstance().webViewSend(resultCd,callbackName!!,commonDTO.webView)
            return
        }
        // 저장된 토큰 객체
        val tokenRef = tokenClient.token
        // 생체인증 정보를 사용하고 있는지 확인
        if (!tokenRef.bindBiometric()) {
            //BaseActivity().alertDlg("지문인식 인증수단이 등록되어 있지 않습니다. 인증수단 등록 후 이용해 주세요.",)
            // Toast.makeText(activity, "생체인증을 사용하고 있지 않습니다.", Toast.LENGTH_SHORT).show()
            return
        }


        //val authMesg =  commonDTO.sp.getValue(Constants.AUTH_MESG,"")
        //val randomKey = commonDTO.sp.getValue(Constants.RANDOM_KEY,"")

        // 생체인증을 이용하여 서명 저장
        val tokenBioAuth = SafetokenBiometricAuth(commonDTO.activity)
                .setTitle("간편 로그인")    // 전자 서명
                .setSubTitle("지문을 인식하여 로그인을 수행합니다.")  // 전자 서명을 수행합니다.
                .setNegativeButton("취소")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            tokenBioAuth.setForceFingerprint(false)
        }else{
            tokenBioAuth.setForceFingerprint(true)
        }
        tokenBioAuth.generateSign(tokenClient, tokenRef, randomKey, authMesg.toByteArray(),
                commonDTO.supportFragmentManager, object :
                SafetokenBiometricAuthCallback() {
            override fun onGenerateSign(tnp: SafetokenProof?) {
                Logcat.d("Sign with biometric auth : " + tnp!!.toString())
//                val resultCd="0000"
//                var resultParam= JSONObject()
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
                commonDTO.sp.put(Constants.LAST_LOGIN_PLAN,"2")
                commonDTO.activity.setResult(AppCompatActivity.RESULT_OK,loginIntent)
                commonDTO.activity.finish()
                //commonDTO.//activity.//overridePendingTransition(R.anim.slide_up, R.anim.slide_bottom)
            }

            override fun onError(code: Int, errString: String?) {
                Logcat.e("generateSign:onError:code -> $code")
                Logcat.e("generateSign:onError:msg -> " + errString!!)
                // 전자서명시 예외 처리
                if (code == SafetokenBiometricAuthError.ERROR_BIOMETRIC_LOCKOUT) {
                    Toast.makeText(
                            commonDTO.activity,
                            "생체인증 실패가 많이 일시적으로 사용이 중지되었습니다. 잠시 후 다시 시도하세요.",
                            Toast.LENGTH_LONG
                    ).show()
                } else if (code == SafetokenBiometricAuthError.ERROR_BIOMETRIC_LOCKOUT_PERMANENT) {
                    Toast.makeText(
                            commonDTO.activity,
                            "생체인증 실패가 많이 생체인증을 사용할 수 없습니다. 잠금화면 해제 후 사용 가능합니다.",
                            Toast.LENGTH_LONG
                    ).show()
                } else if (code == SafetokenBiometricAuthError.ERROR_BIOMETRIC_NO_BIOMETRICS) {
                    // 생체인증 이용중 이 에러가 발생했다면 기존 생체정보를 모두 삭제한 상태임
                    // 이경우 필요에 따라 아래코드를 이용하여 생체인증정보를 삭제함.
                    //tokenClient.removeBiometricCredential(tokenRef);

                    Toast.makeText(
                            commonDTO.activity,
                            "생체정보가 등록되어 있지 않습니다. 등록 후 이용하세요.",
                            Toast.LENGTH_LONG
                    ).show()
                } else if (code == SafetokenBiometricAuthError.ERROR_BIOMETRIC_HW_UNAVAILABLE) {
                    Toast.makeText(
                            commonDTO.activity,
                            "생체인증 장치를 사용할 수 없습니다. 잠시 후 다시 시도하세요.",
                            Toast.LENGTH_SHORT
                    ).show()
                } else if (code == SafetokenBiometricAuthError.ERROR_BIOMETRIC_HW_NOT_PRESENT) {
                    Toast.makeText(
                            commonDTO.activity,
                            "생체인증을 지원하지 않는 기기입니다.",
                            Toast.LENGTH_LONG
                    ).show()
                } else {
                    Toast.makeText(
                            commonDTO.activity,
                            String.format("[%d]%s", code, errString),
                            Toast.LENGTH_LONG
                    ).show()
                }
//                val resultCd="9998"
//                val callbackName=commonDTO.sp.getValue(Constants.SUCC_FUNC,"")
//                DataSend.getInstance().webViewSend(resultCd,callbackName!!,commonDTO.webView)

//
//                val loginFailIntent = Intent()
//                commonDTO.activity.setResult(AppCompatActivity.RESULT_CANCELED,loginFailIntent)
//                commonDTO.activity.finish()
            }

            override fun onFail(count: Int) {
//                val resultCd="9998"
//                Toast.makeText(commonDTO.activity, "$count 회 실패", Toast.LENGTH_SHORT).show()
//                val callbackName=commonDTO.sp.getValue(Constants.SUCC_FUNC,"")
//                DataSend.getInstance().webViewSend(resultCd,callbackName!!,commonDTO.webView)
            }

            override fun invalidateBiometric() {
                // 생체정보가 변경되어 기존 생체인증 정보를 이용할 수 없음.
                Toast.makeText(
                        commonDTO.activity,
                        "기기에 등록된 생체 정보가 변경되어 생체인증을 더이상 이용할 수 없습니다. 재등록 후 이용하세요",
                        Toast.LENGTH_SHORT
                ).show()
////                val resultCd="9998"
////                val callbackName=commonDTO.sp.getValue(Constants.SUCC_FUNC,"")
////                DataSend.getInstance().webViewSend(resultCd,callbackName!!,commonDTO.webView)
//                val loginFailIntent = Intent()
//                commonDTO.activity.setResult(AppCompatActivity.RESULT_CANCELED,loginFailIntent)
//                commonDTO.activity.finish()
            }

            override fun onCancel() {
                Toast.makeText(commonDTO.activity, "생체인증을 취소하였습니다.", Toast.LENGTH_SHORT).show()
//                val resultCd="9998"
//                val callbackName=commonDTO.sp.getValue(Constants.SUCC_FUNC,"")
//                DataSend.getInstance().webViewSend(resultCd,callbackName!!,commonDTO.webView)
//                val loginFailIntent = Intent()
//                commonDTO.activity.setResult(AppCompatActivity.RESULT_CANCELED,loginFailIntent)
//                commonDTO.activity.finish()
            }
        })
    }
}