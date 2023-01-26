package kr.co.smartbank.app.config

/*
 * [Checklist]
 * 1. 운영서버 확인
 * 2. todo: 코드 확인
 * 3. 앱 위변조 서버 주소 확인
 */
object Constants {
    //모드
    const val MODE = "R"  //H : 남교집 , D: 개발  R:운영
    //위변조
    const val EVER_SPIN_YN = true
    //크롬로그여부
    const val WEB_LOG_YN = false

    const val MODE_H ="H"
    const val MODE_D ="D"
    const val MODE_R ="R"
    const val WEB_MAIN_H: String = "http://210.221.92.125:8082/"
    const val WEB_MAIN_D: String = "https://mtest.smartbank.co.kr:8010/"
    const val WEB_MAIN_R: String = "https://smart-i.smartbank.co.kr/"

    const val WEB_MAIN_VIEW: String = "main.view"
    const val WEB_SIGN_UP: String = "COM0006.view"
    const val WEB_ERROR: String = "error.html"
    /*
     *  검증설정 checkPinValidation
     */
    const val CHECK_VALIDATION = true
    
    /*
       뒤로가기 버튼 활성화 여부  true : 다이알로그  false : 두번누르면 앱종료
     */
    const val BACK_BTN_YN = true

    /*
     * 앱위변조 검사
     */
    const val EVERSAFE_SERVER_URL_D: String = "https://mtest.smartbank.co.kr:4443/eversafe"

    const val EVERSAFE_SERVER_URL_R: String = "https://smart-i.smartbank.co.kr:4443/eversafe"

    //정책 등록한 에퍼세이프 앱 ID
    const val EVERSAFE_APP_ID_D: String = "0D112362834CD518"
    const val EVERSAFE_APP_ID_R: String = "8AEB3F9B22A9FF78"

    const val keyPadPrivateKey="smart"

    const val PARAMS="PARAMS"
    const val SUCC_FUNC="SUCC_FUNC"
    const val FAIL_FUNC="FAIL_FUNC"
    const val AUTH_TOKEN="AUTH_TOKEN"
    const val A_AUTH_TOKEN="A_AUTH_TOKEN"
    const val M_AUTH_TOKEN="M_AUTH_TOKEN"
    const val AUTH_MESG="AUTH_MESG"
    const val RANDOM_KEY="RANDOM_KEY"
    const val LAST_LOGIN_PLAN="LAST_LOGIN_PLAN"


    const val FIN_NUM="FIN_NUM"
    const val MOTP_FIN_NUM="MOTP_FIN_NUM"
    const val SMARTAPP_FIN_NUM="SMARTAPP_FIN_NUM"
    const val SCRAPTEMP="SPW"



    const val PURPOSE_OF_INTENT: String = "porpose_of_intent"
    const val ERROR: Int = -1
    const val PURPOSE_OF_INTENT_PUSH: Int = 0
    const val REQUEST_FILE_CHOOSE: Int = 101
    // 전자서명 요청 코드
    const val KSW_Activity: String = "KSW_Activity"
    const val KSW_Activity_CertList: Int = 102
    const val KSW_Activity_CertSign: Int = 103
    const val KSW_Activity_CertPw: Int = 104
    const val KSW_Activity_IMPORT: Int = 105
    const val KSW_Activity_SCRAPING: Int = 106

    // 보안키패드
    const val KEYPAD_Activity: String = "KEYPAD_Activity"
    const val REQUEST_SECURE_KEYPAD: Int = 1
    const val KEYPAD_TYPE_QWERTY: Int = 1
    const val KEYPAD_TYPE_NUMBERIC: Int = 2
    const val KEYPAD_TYPE_NUMBERIC_LINE: Int = 3


    const val TYPE_KOSCOM_SIGN = 0x01
    const val TYPE_KOSCOM_SIGN_BRIEF = 0x02
    const val TYPE_CMS_SIGN = 0x03
    const val TYPE_BRIEF_SIGN = 0x04


    // FDS
    const val FDS_LICENCE="D104810F89B4"
    const val FDS_CUSTOM_ID="SMART_01"


    // OCR
    const val REQUEST_OCR_SIGN: Int = 110
    const val REQ_OCR_ID: Int = 111
    const val REQ_OCR_RESULT: Int = 112

    // 간편인증 PIN번호 입력
    const val REQUEST_PIN_INPUT: Int = 120
    const val REQUEST_PIN_CONFIRM: Int = 121
    const val REQUEST_PIN_AUTH: Int = 122

    //  MOTP 입력
    const val REQUEST_MOTP_INPUT: Int = 123
    const val REQUEST_MOTP_CONFIRM: Int = 124
    const val REQUEST_MOTP_AUTH: Int = 125
    const val REQUEST_MOTP_DELETE: Int = 126

    //  스마트앱 입력
    const val REQUEST_SMARTAPP_INPUT: Int = 127
    const val REQUEST_SMARTAPP_CONFIRM: Int = 128
    const val REQUEST_SMARTAPP_AUTH: Int = 129
    const val REQUEST_SMARTAPP_DELETE: Int = 130

    // 스크랩핑
    const val REQUEST_SCRAPING_SIGN_CERT = 131
    const val REQUEST_SCRAPING_CAPTCHA_INPUT: Int = 132

    //보안키패드
    const val REQUEST_KEYPAD: Int = 133
    const val REQUEST_KEYPAD_QWETY: Int = 134


    const val KSW_Activity_CHANGE_PWD_01: Int = 140
    const val KSW_Activity_CHANGE_PWD_02: Int = 141
    const val KSW_Activity_CHANGE_PWD_03: Int = 142


    //OCR
    const val OCR_Activity_01: Int = 143
    const val OCR_Activity_02: Int = 144
    const val OCR_Activity_03: Int = 145
    const val OCR_Activity_04: Int = 146
    const val OCR_Activity_05: Int = 147


    const val KSW_Activity_01: Int = 148
    const val KSW_Activity_02: Int = 149


    const val KSW_Cert_Activity_01: Int = 150
    const val KSW_Cert_Activity_02: Int = 151

    const val Pin_Key_Activity_01: Int = 152
    const val Motp_Key_Activity_01: Int = 153
    const val Smart_Key_Activity_01: Int = 154
    const val KeyPad_Custom_Activity: Int = 155


    const val Native_Camera: Int = 156
    const val LOGIN_MODE: Int = 157


}