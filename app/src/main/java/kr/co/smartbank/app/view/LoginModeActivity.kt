package kr.co.smartbank.app.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import eightbyte.safetoken.SafetokenSimpleClient
import kr.co.smartbank.app.R
import kr.co.smartbank.app.process.ActivityResult
import kr.co.smartbank.app.process.CommonDTO
import kr.co.smartbank.app.solution.eightbyte.util.BioManger
import kr.co.smartbank.app.solution.eightbyte.util.PatternManger
import kr.co.smartbank.app.solution.eightbyte.util.PinManger
import kr.co.smartbank.app.util.SharedPreferenceHelper

/**
 * 자동로그인,로그인방법선택 화면
 * @author 김남교
 */
class LoginModeActivity: BaseActivity() {
    //공통
    private lateinit var activity: Activity
    private lateinit var sp: SharedPreferenceHelper
    private lateinit var commonDTO: CommonDTO

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_mode)

        activity = this
        sp = SharedPreferenceHelper(activity)

        commonDTO = CommonDTO(activity, supportFragmentManager, sp)

        val tc = SafetokenSimpleClient.getInstance(activity)

        val randomKey   =   intent.extras?.getString("randomKey","")!!
        val authMesg    =   intent.extras?.getString("authMesg","")!!


        when(intent.getStringExtra("mode")){
            "1"->{
                if (tc.isExistToken){
                    PinManger().doAuthPin(commonDTO, randomKey, authMesg)
                }else{
                    alertDlg("사설인증이 등록되지 않았습니다",activity)
                }
            }
            "2"->{
                if (tc.token.bindBiometric()){
                    BioManger.getInstance().doAuthBio(commonDTO, randomKey, authMesg)
                }else{
                    alertDlg("생체인증이 등록되지 않았습니다",activity)
                }
            }
            "3"->{
                if (tc.token.bindPattern()){
                    PatternManger().doAuthPattern(commonDTO, randomKey, authMesg)
                }else{
                    alertDlg("패턴이 등록되지 않았습니다",activity)
                }
            }
        }


        //클릭이벤트
        val closeBtn : View = findViewById(R.id.login_mode_at_btn_close)
        closeBtn.setOnClickListener{
            val intent = Intent()
            activity.setResult(0, intent)
            //activity.//overridePendingTransition(R.anim.slide_up, R.anim.slide_bottom)
            activity.finish()
        }

        val btnLogin01 : View = findViewById(R.id.login_info_imgs_01)
        btnLogin01.setOnClickListener{
            //finish()
            if (tc.token.bindBiometric()){
                BioManger.getInstance().doAuthBio(commonDTO, randomKey, authMesg)
            }else{
                alertDlg("생체인증이 등록되지 않았습니다",activity)
            }

        }
        val btnLogin02 : View = findViewById(R.id.login_info_imgs_02)
        btnLogin02.setOnClickListener{
            //finish()
            if (tc.isExistToken){
                PinManger().doAuthPin(commonDTO, randomKey, authMesg)
            }else{
                alertDlg("사설인증이 등록되지 않았습니다",activity)
            }
        }
        val btnLogin03 : View = findViewById(R.id.login_info_imgs_03)
        btnLogin03.setOnClickListener{
            //finish()
            if (tc.token.bindPattern()){
                PatternManger().doAuthPattern(commonDTO, randomKey, authMesg)
            }else{
                alertDlg("패턴이 등록되지 않았습니다",activity)
            }
        }

        //생성안한 정보 흐리게 하기
        if (!tc.token.bindPattern()){
            btnLogin03.alpha= 0.5f
        }
        if (!tc.token.bindBiometric()){
            btnLogin01.alpha= 0.5f
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        commonDTO.customProgressDialog=customProgressDialog
        ActivityResult().onActivityResult(requestCode, resultCode, data, commonDTO)
    }
}