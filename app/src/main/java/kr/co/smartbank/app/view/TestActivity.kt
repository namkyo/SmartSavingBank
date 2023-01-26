package kr.co.smartbank.app.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import com.nprotect.seculog.IxLogManager
import com.nprotect.seculog.IxLogMessage
import kr.co.coocon.sasapi.SASManager
import kr.co.coocon.sasapi.SASRunCompletedListener
import kr.co.coocon.sasapi.SASRunStatusChangedListener
import kr.co.smartbank.app.R
import kr.co.smartbank.app.config.Constants
import kr.co.smartbank.app.process.ActivityResult
import kr.co.smartbank.app.process.CommonDTO
import kr.co.smartbank.app.solution.eightbyte.util.BioManger
import kr.co.smartbank.app.solution.eightbyte.util.MotpManger
import kr.co.smartbank.app.solution.eightbyte.util.PatternManger
import kr.co.smartbank.app.solution.eightbyte.util.PinManger
import kr.co.smartbank.app.solution.everspin.secureKeypad.PinView
import kr.co.smartbank.app.solution.ksw.view.KSW_Activity_CertList
import kr.co.smartbank.app.solution.ksw.view.KSW_Activity_ICRSImportCert
import kr.co.smartbank.app.util.Logcat
import kr.co.smartbank.app.util.SharedPreferenceHelper

class TestActivity : BaseActivity(), SASRunCompletedListener, SASRunStatusChangedListener {
    private lateinit var activity: Activity
    private lateinit var sp: SharedPreferenceHelper
    private lateinit var sasManager: SASManager
    private val ex_authToken = "2d757365723a39646133353938313738356333333661663264633038383431633938356130353362633233386337c0817a6c5a63486f3d6af08e0b68723a4e3fb888cb67846561ddbdf13c64ee81c6812211a67c6c3abc05a5db10080b8c5f1897310e2ac82c8d43fdf1dfbf4a4ca1a5d2e433ac61ba9b70736d6cfe576c85f8be62e067aa9de8714f960beb8fb81e7cfdcf7c29a5f9fa26181b3d5d464d5490370bdac27d8fb6cc0b5b6cee6fd45112560ce5936372bb49db5df2a6566628164e5613c93e4cc13498bd5aec1f96114b832e455a0cbac678c8143af1fc8be2e6e36af8a6a1edd4289c47b483dd4919e410299e1d279d9589e8b7a2ea6db0d4e65596054957c529e21dc7ec53b740ad8640446941ff37c6d4d10bd5ad09eddb79df9fae4e4d8c8df992621204d9b779210f9ed758c01ca5b6003a6dc20e1a90a983cf6cd2939e33f3610b2d3358d7a6ff9b994a2da0802f426e21ab68a3398acd9ad42caf31e1997720ac04f30c3fafb092e9f288ba318126ab91f4ff2f296570443e93ea37816d8243df02d30c98aab9a86003840645a4e1b7023e719d94469c1200c844dc628b2bf7280e22f06bae408996a3dee0a96e3182bafb160b0d170f1d0c258c8512fff08929f82953333272fff01641e5154fd9b3e33a138593636e74080b8ee18cc17935ad3cdcdb806b08f9e7cebdf8c65e2c59cc760feb11641bb96ea704a1f8fb6980d28bd06866dea0edda761b5eb3802b49a90ebc42c6b8f3c1f9d1c8b7af54036372414588c50bf747310002?1572242020989=6578706972653d3230323030313031"
    private val ex_randomKey = "56b66e234617d1481d3537a1810ef006845055dd"




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.test_activity)
        activity    = this
        sp          = SharedPreferenceHelper(activity)

        //메인화면
        var testBtn01 = findViewById(R.id.test_btn1) as Button
        testBtn01.setText("메인액티비티")
        testBtn01.setOnClickListener {
            val intent = Intent(activity, MainActivity::class.java)
            startActivity(intent)
        }
        //공동인증 관리테스트
        var testBtn02 = findViewById(R.id.test_btn2) as Button
        testBtn02.setText("공동인증_관리테스트")
        testBtn02.setOnClickListener {
            val intent = Intent(activity, KSW_Activity_CertList::class.java)
            intent.putExtra(Constants.KSW_Activity, Constants.KSW_Activity_CertList)
            startActivity(intent)
        }
        //공동인증 수행테스트
        var testBtn03 = findViewById(R.id.test_btn3) as Button
        testBtn03.setText("공동인증_수행테스트")
        testBtn03.setOnClickListener {
            val intent = Intent(activity, KSW_Activity_CertList::class.java)
            intent.putExtra(Constants.KSW_Activity, Constants.KSW_Activity_CertSign)
            startActivity(intent)
        }
        //사설인증테스트_핀번호등록
        var testBtn04 = findViewById(R.id.test_btn4) as Button
        testBtn04.setText("사설인증테스트_핀번호등록")
        testBtn04.setOnClickListener {
            val common = CommonDTO(activity, supportFragmentManager, sp)
            Logcat.d("doRegisterPin1")
            sp.put("IS_AUTH_TOKEN", ex_authToken)
            PinManger().doRegisterPin1(common)

        }
        //사설인증테스트_핀번호인증
        var testBtn05 = findViewById(R.id.test_btn5) as Button
        testBtn05.setText("사설인증테스트_핀번호인증")
        testBtn05.setOnClickListener {
            sp.put("IS_AUTH_MESG", ex_randomKey)
            sp.put("IS_RANDOM_KEY", ex_randomKey)
            val common = CommonDTO(activity, supportFragmentManager, sp)
            PinManger().doAuthPin(common,"","")
        }
        //사설인증테스트_지문등록
        var testBtn06 = findViewById(R.id.test_btn6) as Button
        testBtn06.setText("사설인증테스트_지문등록")
        testBtn06.setOnClickListener {
            val common = CommonDTO(activity, supportFragmentManager, sp)
            BioManger.getInstance().doRegisterBio(common)
        }
        //사설인증테스트_지문인증
        var testBtn07 = findViewById(R.id.test_btn7) as Button
        testBtn07.setText("사설인증테스트_지문인증")
        testBtn07.setOnClickListener {
            sp.put("IS_AUTH_MESG", ex_randomKey)
            sp.put("IS_RANDOM_KEY", ex_randomKey)
            val common = CommonDTO(activity, supportFragmentManager, sp)
            //BioManger().doAuthBio(common)
        }
        //사설인증테스트_패턴등록
        var testBtn08 = findViewById(R.id.test_btn8) as Button
        testBtn08.setText("사설인증테스트_패턴등록")
        testBtn08.setOnClickListener {
            val common = CommonDTO(activity, supportFragmentManager, sp)
            PatternManger().doRegisterPattern(common)
        }

        //사설인증테스트_패턴인증
        var testBtn09 = findViewById(R.id.test_btn9) as Button
        testBtn09.setText("사설인증테스트_패턴인증")
        testBtn09.setOnClickListener {
            sp.put("IS_AUTH_MESG", ex_randomKey)
            sp.put("IS_RANDOM_KEY", ex_randomKey)
            val common = CommonDTO(activity, supportFragmentManager, sp)
            //PatternManger().doAuthPattern(common)
        }
        //사설인증테스트_MOTP등록
        var testBtn10 = findViewById(R.id.test_btn10) as Button
        testBtn10.setText("사설인증테스트_MOTP등록")
        testBtn10.setOnClickListener {
            sp.put("IS_AUTH_MESG", ex_randomKey)
            sp.put("IS_RANDOM_KEY", ex_randomKey)
            val common = CommonDTO(activity, supportFragmentManager, sp)
            MotpManger().doRegisterMotp1(common)
        }

        //사설인증테스트_MOTP인증
        var testBtn11 = findViewById(R.id.test_btn11) as Button
        testBtn11.setText("사설인증테스트_MOTP인증")
        testBtn11.setOnClickListener {
            val common = CommonDTO(activity, supportFragmentManager, sp)
           MotpManger().doAuthMotp(common)
        }

        //OCR테스트
//        var testBtn12 = findViewById(R.id.test_btn12) as Button
//        testBtn12.setText("OCR테스트")
//        testBtn12.setOnClickListener {
//            //val intent = Intent(activity, OCR_Main_Activity::class.java)
//            //startActivity(intent)
//
//            val intent = Intent(activity, OCR_Preview_Activity::class.java)
//            intent.putExtra("take_mode", 2)
//            startActivityForResult(intent,Constants.OCR_Activity_01)
//        }

        //공동인증서 가져오기 테스트
        var testBtn13 = findViewById(R.id.test_btn13) as Button
        testBtn13.setText("공동인증_가져오테스트")
        testBtn13.setOnClickListener {
            val intent = Intent(activity, KSW_Activity_ICRSImportCert::class.java)
            startActivity(intent)
        }

        //스크랩핑 테스
        var testBtn14 = findViewById(R.id.test_btn14) as Button
        testBtn14.setText("쿠콘_스크랩핑")
        testBtn14.setOnClickListener {
            val intent = Intent(activity, KSW_Activity_CertList::class.java)
            intent.putExtra(Constants.KSW_Activity, Constants.REQUEST_SCRAPING_SIGN_CERT)
            startActivity(intent)
        }

        //보안키패드 커스텀
        var testBtn15 = findViewById(R.id.test_btn15) as Button
        testBtn15.setText("커스텀 ")
        testBtn15.setOnClickListener {
            val intent = Intent(activity, PinView::class.java)
            //val intent = Intent(activity, QwertyView::class.java)
            startActivity(intent)
        }
        //FDS
        var testBtn16 = findViewById(R.id.test_btn16) as Button
        testBtn16.setText("FDS ")
        testBtn16.setOnClickListener {
            val ixLogMessage = IxLogMessage(activity) // 1.기기정보 수집 클래스
            ixLogMessage.setServerKey("dsdfdsfe3dwdewffdsfsdfsdfew")//서버에서가져온 서버키
            ixLogMessage.setCheckApp("kr.co.smartbank.app")//패키지명설정 생략가능
            val encryptedLogData = ixLogMessage.getEveryLog() //암호화된수집정보
            val encryptedKey = ixLogMessage.builtKey //암호화된 클라이언트 키
            val ixLogManager = IxLogManager(activity)
            Logcat.d("ixLogManager : "+ixLogManager.everyLogByString)
            Logcat.d("encryptedLogData : "+encryptedLogData)
            Logcat.d("doRegisterPin1")
        }



        //스크랩핑테스트
        var testBtn17 = findViewById(R.id.test_btn17) as Button
        testBtn17.setText("스크랩핑테스트")
        testBtn17.setOnClickListener {
            val intent = Intent(activity, KSW_Activity_CertList::class.java)
            intent.putExtra(Constants.KSW_Activity, Constants.KSW_Activity_SCRAPING)
            startActivity(intent)
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val common = CommonDTO(activity, supportFragmentManager, sp)
        common.customProgressDialog=customProgressDialog
       // common.sasManager=sasManager
        ActivityResult().onActivityResult(requestCode, resultCode, data, common)
    }


    override fun onSASRunCompleted(index: Int, outString: String?) {
        Logcat.d("[scraping result] index : " + index + " , outString : " + outString)
    }

    // SASRunStatusChangedListener
    // SASManager.run이 구동되는 동안,
    // status를 전달받을 콜백
    override fun onSASRunStatusChanged(action: Int, percent: Int) {
        Logcat.d("onSASRunStatusChanged : " + action + " , " + percent);
    }
}