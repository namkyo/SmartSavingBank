package kr.co.smartbank.app.solution.eightbyte.util

import android.app.Activity
import android.content.Intent
import androidx.fragment.app.FragmentManager
import eightbyte.safetoken.SafetokenException
import eightbyte.safetoken.SafetokenFsbOtpClient
import eightbyte.safetoken.SafetokenSimpleClient
import kr.co.smartbank.app.config.Constants
import kr.co.smartbank.app.process.CommonDTO
import kr.co.smartbank.app.solution.everspin.secureKeypad.CustomPinView
import kr.co.smartbank.app.solution.everspin.secureKeypad.SecureKeyPadManager
import kr.co.smartbank.app.util.DataSend
import kr.co.smartbank.app.util.Logcat
import kr.co.smartbank.app.view.BaseActivity
import org.json.JSONObject

class MotpManger {

    //등록용 첫번째 핀번호
    public fun doRegisterMotp1(commonDTO: CommonDTO) {
        // mOTP 토큰 클라이언트 생성
        val oc = SafetokenFsbOtpClient.getInstance(commonDTO.activity)
        // 토큰 체크 로직 추가
        // 토큰이 존재하는 경우는 재설정인 경우이다.
        // 원래 존재하던 토큰을 삭제하고 저장을 진행한다.

        var motpTokenList = oc.tokenList
        if (motpTokenList.size > 0) {    // mOTP 토큰이 존재하는 경우
            Logcat.d("motpTokenList $motpTokenList")

            //기 존재
            val resultCd = "9997"
            var resultParam = JSONObject()
            val callbackName = commonDTO.sp.getValue(Constants.SUCC_FUNC, "")
            //DataSend.getInstance().webViewSend(resultCd, resultParam, callbackName!!, commonDTO.webView)
            //return
        }

        //SecureKeyPadManager().keyPadActivityOn(commonDTO.activity, Constants.KEYPAD_TYPE_NUMBERIC, "mOTP", "6자리 입력", "mOTP 번호 입력해주세요.", 6, Constants.REQUEST_MOTP_INPUT,"",6)
        //커스텀 보안키패드
        val intentKeypad = Intent(commonDTO.activity, CustomPinView::class.java).apply {
            putExtra("maxNumChar", 6)
            putExtra("title", "mOTP")
            putExtra("sub_title", "등록할 mOTP 번호 입력해주세요")
            putExtra("hint", "6자리 입력")
        }
        commonDTO.activity.startActivityForResult(intentKeypad, Constants.REQUEST_MOTP_INPUT)

    }

    //확인용 두번째 핀번호
    public fun doRegisterMotp2(commonDTO: CommonDTO) {
        //SecureKeyPadManager().keyPadActivityOn(commonDTO.activity, Constants.KEYPAD_TYPE_NUMBERIC, "mOTP", "6자리 입력", "mOTP 번호 확인해주세요.", 6, Constants.REQUEST_MOTP_CONFIRM,"",6)
        val intentKeypad = Intent(commonDTO.activity, CustomPinView::class.java).apply {
            putExtra("maxNumChar", 6)
            putExtra("title", "mOTP")
            putExtra("sub_title", "등록할 mOTP 번호 다시 입력해주세요")
            putExtra("hint", "6자리 입력")
        }
        commonDTO.activity.startActivityForResult(intentKeypad, Constants.REQUEST_MOTP_CONFIRM)
    }

    public fun doDeleteMotp(commonDTO: CommonDTO) {
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
        val oc = SafetokenFsbOtpClient.getInstance(commonDTO.activity)
        // 토큰 체크 로직 추가
        // 토큰이 존재하는 경우는 재설정인 경우이다.
        // 원래 존재하던 토큰을 삭제하고 저장을 진행한다.
        var motpTokenList = oc.tokenList
        if (motpTokenList.size > 0) {    // mOTP 토큰이 존재하는 경우
            Logcat.d("Exist mOTP token")
            for (i in 0 until motpTokenList.size) {
                oc.removeToken(motpTokenList[i].uid(), motpTokenList[i].organization())
            }
        }

        val resultCd = "0000"
        var resultParam = JSONObject()
        val callbackName = commonDTO.sp.getValue(Constants.SUCC_FUNC, "")
        DataSend.getInstance().webViewSend(resultCd, resultParam, callbackName!!, commonDTO.webView)
    }

    public fun doStoreTokenMotp(authToken: String,pin:String, commonDTO: CommonDTO) {
        // mOTP 토큰 클라이언트 생성
        val oc = SafetokenFsbOtpClient.getInstance(commonDTO.activity)
        // 토큰 체크 로직 추가
        // 토큰이 존재하는 경우는 재설정인 경우이다.
        // 원래 존재하던 토큰을 삭제하고 저장을 진행한다.
        var motpTokenList = oc.tokenList
        if (motpTokenList.size > 0) {    // mOTP 토큰이 존재하는 경우
            Logcat.d("Exist mOTP token")
            for (i in 0 until motpTokenList.size) {
                oc.removeToken(motpTokenList[i].uid(), motpTokenList[i].organization())
            }
        }
        // 토큰 저장
        try {
            oc.storeToken(authToken, pin)
        } catch (e: SafetokenException) {
            Logcat.e("에러입니다 [${e.message}]")
        } catch (e: RuntimeException) {
            Logcat.e("에러입니다 [${e.message}]")
        } catch (e: ArrayIndexOutOfBoundsException) {
            Logcat.e("에러입니다 [${e.message}]")
        }
    }

    public fun doAuthMotp(commonDTO: CommonDTO) {
        // 토큰 클라이언트 생성
        val oc = SafetokenFsbOtpClient.getInstance(commonDTO.activity)
        // 토큰 체크
         var motpTokenList = oc.tokenList
        if (motpTokenList.size<1) {
            //BaseActivity().alertDlg("MOTP가 등록되어 있지 않습니다. MOTP 등록 후 이용해 주세요.",commonDTO.activity)
            //return
            val resultCd = "9990"
            var resultParam = JSONObject()
            val callbackName = commonDTO.sp.getValue(Constants.SUCC_FUNC, "")
            DataSend.getInstance().webViewSend(resultCd, resultParam, callbackName!!, commonDTO.webView)
            return
        }
        //SecureKeyPadManager().keyPadActivityOn(commonDTO.activity, Constants.KEYPAD_TYPE_NUMBERIC, "인증", "mOTP 입력", "현재 설정된 mOTP 6자리 입력", 6, Constants.REQUEST_MOTP_AUTH,"",6)
        val intentKeypad = Intent(commonDTO.activity, CustomPinView::class.java).apply {
            putExtra("maxNumChar", 6)
            putExtra("title", "mOTP 인증")
            putExtra("sub_title", "현재 설정된 mOTP 6자리 입력")
            putExtra("hint", "6자리 입력")
        }
        commonDTO.activity.startActivityForResult(intentKeypad, Constants.REQUEST_MOTP_AUTH)
    }

}