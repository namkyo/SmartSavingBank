package kr.co.smartbank.app.process

import android.widget.Toast
import com.lumensoft.ks.KSCertificateLoader
import eightbyte.safetoken.SafetokenFsbAuthClient
import eightbyte.safetoken.SafetokenFsbOtpClient
import eightbyte.safetoken.SafetokenSimpleClient
import kr.co.smartbank.app.util.Logcat
import org.json.JSONObject

class AppData {
    fun insertAppdata(jsonReqData: JSONObject, commonDTO: CommonDTO){
        val i = jsonReqData.keys() // 키 추출
        val dataKeyList = ArrayList<String>()
        while (i.hasNext()) {
            val key = i.next().toString()
            dataKeyList.add(key)    // 키 저장
        }
        for (key in dataKeyList) {
            commonDTO.sp.put(key, jsonReqData.optString(key, ""))
        }
    }
    fun selectAppdatas(keys: List<String>, commonDTO: CommonDTO): String {
        val jsonObject = JSONObject()
        for (key in keys) {
            when (key) {
                "certificateList" -> {
                    try {
                        val userCerts = KSCertificateLoader.getUserCertificateListWithGpki(commonDTO.activity)
                        jsonObject.put(key, userCerts.size.toString())
                    } catch (e: Exception) {
                        Logcat.e("인증서 리스트 불러오기 중 에러가 발생했습니다. $e")
                        Toast.makeText(commonDTO.activity,"인증서 리스트 불러오기 중 에러가 발생했습니다.",Toast.LENGTH_SHORT).show()
                    }
                }
                "phoneNo" -> {
                    //jsonObject.put(key, BaseActivity().getPhoneNumber(commonDTO.activity))
                } // 전화번호 조회
                "appVersion" -> {
                    val packageInfo = commonDTO.activity.baseContext.packageManager.getPackageInfo(commonDTO.activity.baseContext.packageName, 0)
                    val appVersion = packageInfo.versionName
                    jsonObject.put(key, appVersion)
                }
                "mOTP" -> {
                    val oc = SafetokenFsbOtpClient.getInstance(commonDTO.activity)
                    // mOTP 토큰 체크
                    val motpTokenList = oc.tokenList
                    if (motpTokenList.size > 0) {    // mOTP 토큰이 없는경우
                        jsonObject.put(key, "Y")
                    } else {
                        jsonObject.put(key, "N")
                    }
                }
                "smartAuth" -> {
                    val oc = SafetokenFsbAuthClient.getInstance(commonDTO.activity)
                    // mOTP 토큰 체크
                    val motpTokenList = oc.tokenList
                    if (motpTokenList.size > 0) {    // mOTP 토큰이 없는경우
                        jsonObject.put(key, "Y")
                    } else {
                        jsonObject.put(key, "N")
                    }
                }
                "tokenId" -> {
                    // 토큰 클라이언트 생성
                    val tc = SafetokenSimpleClient.getInstance(commonDTO.activity)
                    // 단말에 간편인증 토큰이 존재한다.
                    if (tc.isExistToken) {
                        // 토큰 객체
                        val tokenRef = tc.token
                        Logcat.d("tokenRef.uid(): " + tokenRef.uid())
                        tokenRef.uid().let {
                            jsonObject.put(key, it)
                        }
                    }
                }
                else ->{
                    jsonObject.put(key, commonDTO.sp.getValue(key, ""))
                }

            }
        }
        return jsonObject.toString()
    }
    fun  selectAppdata(reqData: String, commonDTO: CommonDTO): String {
        val resultData = StringBuilder()
        when (reqData) {
            "certificateList" -> {
                try {
                    val userCerts = KSCertificateLoader.getUserCertificateListWithGpki(commonDTO.activity)
                    resultData.append(userCerts.size.toString())
                } catch (e: Exception) {
                    Logcat.e("인증서 리스트 불러오기 중 에러가 발생했습니다. $e")
                    Toast.makeText(commonDTO.activity,"인증서 리스트 불러오기 중 에러가 발생했습니다.",Toast.LENGTH_SHORT).show()
                }
            }
            "phoneNo" -> {
                //jsonObject.put(key, BaseActivity().getPhoneNumber(commonDTO.activity))
            } // 전화번호 조회
            "appVersion" -> {
                val packageInfo = commonDTO.activity.baseContext.packageManager.getPackageInfo(commonDTO.activity.baseContext.packageName, 0)
                val appVersion = packageInfo.versionName
                resultData.append(appVersion)
            }
            "mOTP" -> {
                val oc = SafetokenFsbOtpClient.getInstance(commonDTO.activity)
                // mOTP 토큰 체크
                val motpTokenList = oc.tokenList
                if (motpTokenList.size > 0) {    // mOTP 토큰이 없는경우
                    resultData.append("Y")
                } else {
                    resultData.append("N")
                }
            }
            "smartAuth" -> {
                val oc = SafetokenFsbAuthClient.getInstance(commonDTO.activity)
                // mOTP 토큰 체크
                val motpTokenList = oc.tokenList
                if (motpTokenList.size > 0) {    // mOTP 토큰이 없는경우
                    resultData.append("Y")
                } else {
                    resultData.append("N")
                }
            }
            "tokenId" -> {
                // 토큰 클라이언트 생성
                val tc = SafetokenSimpleClient.getInstance(commonDTO.activity)
                // 단말에 간편인증 토큰이 존재한다.
                if (tc.isExistToken) {
                    // 토큰 객체
                    val tokenRef = tc.token
                    Logcat.d("tokenRef.uid(): " + tokenRef.uid())
                    tokenRef.uid().let {
                        resultData.append(it)
                    }
                }
            }
            else ->{
                resultData.append(commonDTO.sp.getValue(reqData, "").toString())
            }
        }
        Logcat.d("selectAppdata resultData : $resultData")
        return resultData.toString()
    }
}