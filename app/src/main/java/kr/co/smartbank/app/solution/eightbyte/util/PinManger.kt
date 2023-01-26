package kr.co.smartbank.app.solution.eightbyte.util

import android.app.Activity
import android.content.Intent
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import eightbyte.safetoken.SafetokenException
import eightbyte.safetoken.SafetokenSimpleClient
import eightbyte.safetoken.SafetokenVersion
import kr.co.smartbank.app.R
import kr.co.smartbank.app.config.Constants
import kr.co.smartbank.app.process.CommonDTO
import kr.co.smartbank.app.solution.everspin.secureKeypad.CustomPinView
import kr.co.smartbank.app.solution.everspin.secureKeypad.PinView
import kr.co.smartbank.app.solution.everspin.secureKeypad.QwertyView
import kr.co.smartbank.app.solution.everspin.secureKeypad.SecureKeyPadManager
import kr.co.smartbank.app.util.Logcat
import kr.co.smartbank.app.view.BaseActivity

class PinManger {

    //등록용 첫번째 핀번호
    public fun doRegisterPin1(commonDTO: CommonDTO) {
        //SecureKeyPadManager().keyPadActivityOn(commonDTO.activity, Constants.KEYPAD_TYPE_NUMBERIC, "간편비밀번호", "6자리 입력", "간편비밀번호을 입력해주세요.", 6,Constants.REQUEST_PIN_INPUT)
        //val intent = Intent(commonDTO.activity, PinView::class.java)
        //commonDTO.activity.startActivityForResult(intent,Constants.REQUEST_PIN_INPUT)
        //커스텀 보안키패드
        val intentKeypad = Intent(commonDTO.activity, CustomPinView::class.java).apply {
            putExtra("maxNumChar", 6)
            putExtra("title", "간편비밀번호")
            putExtra("sub_title", "등록할 간편비밀번호을 입력해주세요.")
            putExtra("hint", "6자리 입력")
        }
        commonDTO.activity.startActivityForResult(intentKeypad, Constants.REQUEST_PIN_INPUT)
        //commonDTO.//activity.//overridePendingTransition(R.anim.slide_up, R.anim.slide_bottom)

    }

    //확인용 두번째 핀번호
    public fun doRegisterPin2(commonDTO: CommonDTO) {
        //SecureKeyPadManager().keyPadActivityOn(commonDTO.activity, Constants.KEYPAD_TYPE_NUMBERIC, "간편비밀번호", "6자리 입력", "간편비밀번호을 확인해주세요.", 6,Constants.REQUEST_PIN_CONFIRM,"")
       // val intent = Intent(commonDTO.activity, PinView::class.java)
        //commonDTO.activity.startActivityForResult(intent,Constants.REQUEST_PIN_CONFIRM)
        //커스텀 보안키패드
        val intentKeypad = Intent(commonDTO.activity, CustomPinView::class.java).apply {
            putExtra("maxNumChar", 6)
            putExtra("title", "간편비밀번호")
            putExtra("sub_title", "등록할 간편비밀번호을 다시 입력해주세요.")
            putExtra("hint", "6자리 입력")
        }
        commonDTO.activity.startActivityForResult(intentKeypad, Constants.REQUEST_PIN_CONFIRM)
        //commonDTO.//activity.//overridePendingTransition(R.anim.slide_up, R.anim.slide_bottom)
    }

    public fun doStoreTokenPin(authToken: String,pin:String, commonDTO: CommonDTO) {
        // 토큰 클라이언트 생성
        val tc = SafetokenSimpleClient.getInstance(commonDTO.activity)
        // 토큰 체크 로직 추가
        // 토큰이 존재하는 경우는 재설정인 경우이다.
        // 원래 존재하던 토큰을 삭제하고 저장을 진행한다.
        if (tc.isExistToken) {
            tc.removeToken()
        }
        // 토큰 저장
        try {
            tc.storeToken(authToken, pin)
        } catch (e: SafetokenException) {
            Logcat.e("에러입니다 [${e.message}]")
        } catch (e: RuntimeException) {
            Logcat.e("에러입니다 [${e.message}]")
        } catch (e: ArrayIndexOutOfBoundsException) {
            Logcat.e("에러입니다 [${e.message}]")
        }
    }

    fun doAuthPin(commonDTO: CommonDTO,randomKey:String,authMesg:String) {
        // 토큰 클라이언트 생성
        val tc = SafetokenSimpleClient.getInstance(commonDTO.activity)

        // 토큰 체크
        val isExist = tc.isExistToken

        if (!isExist) {
            BaseActivity().alertDlg("PIN번호 입력 인증수단이 등록되어 있지 않습니다. 인증수단 등록 후 이용해 주세요.",commonDTO.activity)
            return
        }
        // 토큰 객체
        val tokenRef = tc.token

        // 토큰 Identifier
        val identifier = tokenRef.identifier()
        Logcat.d("identifier() : "+tokenRef.identifier()) // 토큰ID
        Logcat.d("uid() : "+tokenRef.uid())  // 사용자ID
        Logcat.d("organization(() : "+tokenRef.organization()) // 조직ID
        Logcat.d("data() : "+tokenRef.data()) // 부가 정보
        Logcat.d("bindBiometric() : "+tokenRef.bindBiometric()) // 토큰ID


        // 데이터 체크
        if (!tokenRef.veryfyData()) {
            BaseActivity().alertDlg("토큰 유호성 검사에 실패하였습니다.",commonDTO.activity)
            //Toast.makeText(commonDTO.activity, "토큰 유호성 검사에 실패하였습니다.", Toast.LENGTH_SHORT).show()
            return
        }

        //SecureKeyPadManager().keyPadActivityOn(commonDTO.activity, Constants.KEYPAD_TYPE_NUMBERIC, "인증", "간편비밀번호 입력", "현재 설정된 간편비밀번호 6자리 입력", 6,Constants.REQUEST_PIN_AUTH,"")
        //val intent = Intent(commonDTO.activity, PinView::class.java)
        //commonDTO.activity.startActivityForResult(intent,Constants.REQUEST_PIN_AUTH)

        val intentKeypad = Intent(commonDTO.activity, CustomPinView::class.java).apply {
            putExtra("maxNumChar", 6)
            putExtra("title", "간편비밀번호")
            putExtra("sub_title", "현재 설정된 간편비밀번호을 입력해주세요.")
            putExtra("hint", "6자리 입력")
        }
        commonDTO.activity.startActivityForResult(intentKeypad, Constants.REQUEST_PIN_AUTH)
        //commonDTO.//activity.//overridePendingTransition(R.anim.slide_up, R.anim.slide_bottom)

    }


}