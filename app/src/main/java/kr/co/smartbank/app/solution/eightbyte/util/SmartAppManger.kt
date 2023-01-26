package kr.co.smartbank.app.solution.eightbyte.util

import android.content.Intent
import eightbyte.safetoken.SafetokenException
import eightbyte.safetoken.SafetokenFsbAuthClient
import eightbyte.safetoken.SafetokenSimpleClient
import kr.co.smartbank.app.config.Constants
import kr.co.smartbank.app.process.CommonDTO
import kr.co.smartbank.app.solution.everspin.secureKeypad.CustomPinView
import kr.co.smartbank.app.solution.everspin.secureKeypad.SecureKeyPadManager
import kr.co.smartbank.app.util.DataSend
import kr.co.smartbank.app.util.Logcat
import kr.co.smartbank.app.view.BaseActivity
import org.json.JSONObject

class SmartAppManger {

    //등록용 첫번째 핀번호
    public fun doRegisterSmartApp1(commonDTO: CommonDTO) {
        val ac = SafetokenFsbAuthClient.getInstance(commonDTO.activity)
        // 토큰 체크 로직 추가
        // 토큰이 존재하는 경우는 재설정인 경우이다.
        // 원래 존재하던 토큰을 삭제하고 저장을 진행한다.
        var smartAppTokenList = ac.tokenList
        if (smartAppTokenList.size > 0) {    // mOTP 토큰이 존재하는 경우
            Logcat.d("smartAppTokenList : $smartAppTokenList")
            //기 존재
            val resultCd = "9997"
            var resultParam = JSONObject()
            val callbackName = commonDTO.sp.getValue(Constants.SUCC_FUNC, "")
            //DataSend.getInstance().webViewSend(resultCd, resultParam, callbackName!!, commonDTO.webView)
            //return
        }

        //SecureKeyPadManager().keyPadActivityOn(commonDTO.activity, Constants.KEYPAD_TYPE_NUMBERIC, "스마트앱", "6자리 입력", "스마트앱 번호 입력해주세요.", 6, Constants.REQUEST_SMARTAPP_INPUT,"",6)
        val intentKeypad = Intent(commonDTO.activity, CustomPinView::class.java).apply {
            putExtra("maxNumChar", 6)
            putExtra("title", "스마트앱 등록")
            putExtra("sub_title", "설정할 스마트앱 번호 입력해주세요.")
            putExtra("hint", "6자리 입력")
        }
        commonDTO.activity.startActivityForResult(intentKeypad, Constants.REQUEST_SMARTAPP_INPUT)
    }

    //확인용 두번째 핀번호
    public fun doRegisterSmartApp2(commonDTO: CommonDTO) {
        //SecureKeyPadManager().keyPadActivityOn(commonDTO.activity, Constants.KEYPAD_TYPE_NUMBERIC, "스마트앱", "6자리 입력", "스마트앱 번호 확인해주세요.", 6, Constants.REQUEST_SMARTAPP_CONFIRM,"",6)
        val intentKeypad = Intent(commonDTO.activity, CustomPinView::class.java).apply {
            putExtra("maxNumChar", 6)
            putExtra("title", "스마트앱 등록")
            putExtra("sub_title", "설정할 스마트앱 번호 다시 입력해주세요.")
            putExtra("hint", "6자리 입력")
        }
        commonDTO.activity.startActivityForResult(intentKeypad, Constants.REQUEST_SMARTAPP_CONFIRM)
    }
    public fun doDeleteSmartApp(commonDTO: CommonDTO) {

        //토큰여부 검색
        if (!SafetokenSimpleClient.getInstance(commonDTO.activity).isExistToken) {   // 단말에 간편인증 토큰이 존재한다.
            //토큰 미존
            val resultCd = "9999"
            var resultParam = JSONObject()
            val callbackName = commonDTO.sp.getValue(Constants.SUCC_FUNC, "")
            DataSend.getInstance().webViewSend(resultCd, resultParam, callbackName!!, commonDTO.webView)
            return
        }

        // mOTP 토큰 클라이언트 생성
        val ac = SafetokenFsbAuthClient.getInstance(commonDTO.activity)
        // 토큰 체크 로직 추가
        // 토큰이 존재하는 경우는 재설정인 경우이다.
        // 원래 존재하던 토큰을 삭제하고 저장을 진행한다.
        var acTokenList = ac.tokenList
        if (acTokenList.size > 0) {    // mOTP 토큰이 존재하는 경우
            Logcat.d("Exist mOTP token")
            for (i in 0 until acTokenList.size) {
                ac.removeToken(acTokenList[i].uid(), acTokenList[i].organization())
            }
        }
        val resultCd = "0000"
        var resultParam = JSONObject()
        val callbackName = commonDTO.sp.getValue(Constants.SUCC_FUNC, "")
        DataSend.getInstance().webViewSend(resultCd, resultParam, callbackName!!, commonDTO.webView)
    }
    public fun doStoreTokenSmartApp(authToken: String,pin:String, commonDTO: CommonDTO) {
        // mOTP 토큰 클라이언트 생성
        val ac = SafetokenFsbAuthClient.getInstance(commonDTO.activity)
        // 토큰 체크 로직 추가
        // 토큰이 존재하는 경우는 재설정인 경우이다.
        // 원래 존재하던 토큰을 삭제하고 저장을 진행한다.
        var smartAppTokenList = ac.tokenList
        if (smartAppTokenList.size > 0) {    // mOTP 토큰이 존재하는 경우
            Logcat.d("Exist mOTP token")
            for (i in 0 until smartAppTokenList.size) {20
                ac.removeToken(smartAppTokenList[i].uid(), smartAppTokenList[i].organization())
            }
        }
        // 토큰 저장
        try {
            ac.storeToken(authToken, pin)
        } catch (e: SafetokenException) {
            Logcat.e("에러입니다 [${e.message}]")
        } catch (e: RuntimeException) {
            Logcat.e("에러입니다 [${e.message}]")
        } catch (e: ArrayIndexOutOfBoundsException) {
            Logcat.e("에러입니다 [${e.message}]")
        }
    }

    public fun doAuthSmartApp(commonDTO: CommonDTO) {
        // 토큰 클라이언트 생성
        val ac = SafetokenFsbAuthClient.getInstance(commonDTO.activity)
        // 토큰 체크
         var smartTokenList = ac.tokenList

        //토큰없을때
        if (smartTokenList.size<1) {
            //BaseActivity().alertDlg("스마트앱이 등록되어 있지 않습니다. 스마트앱 등록 후 이용해 주세요.",commonDTO.activity)
            //return
            val resultCd = "9990"
            var resultParam = JSONObject()
            val callbackName = commonDTO.sp.getValue(Constants.SUCC_FUNC, "")
            DataSend.getInstance().webViewSend(resultCd, resultParam, callbackName!!, commonDTO.webView)
            return
        }
        //SecureKeyPadManager().keyPadActivityOn(commonDTO.activity, Constants.KEYPAD_TYPE_NUMBERIC, "인증", "스마트앱 번호 입력", "현재 설정된 스마트앱 번호 6자리 입력", 6, Constants.REQUEST_SMARTAPP_AUTH,"",6)
        val intentKeypad = Intent(commonDTO.activity, CustomPinView::class.java).apply {
            putExtra("maxNumChar", 6)
            putExtra("title", "스마트앱 등록")
            putExtra("sub_title", "현재 설정된 스마트앱 번호을 입력해주세요.")
            putExtra("hint", "6자리 입력")
        }
        commonDTO.activity.startActivityForResult(intentKeypad, Constants.REQUEST_SMARTAPP_AUTH)
    }

}