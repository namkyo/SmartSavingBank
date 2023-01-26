package kr.co.smartbank.app.solution.ksw.view

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import kr.co.everspin.eversafe.keypad.params.ESEditTextParams
import kr.co.everspin.eversafe.keypad.params.ESKeypadParams
import kr.co.everspin.eversafe.keypad.widget.ESEditText
import kr.co.smartbank.app.R
import kr.co.smartbank.app.config.Constants
import kr.co.smartbank.app.process.ActivityResult
import kr.co.smartbank.app.process.CommonDTO
import kr.co.smartbank.app.solution.everspin.secureKeypad.util.CustomQwertyAppearance
import kr.co.smartbank.app.solution.everspin.secureKeypad.util.CustomQwertyLayout
import kr.co.smartbank.app.util.Base64Util
import kr.co.smartbank.app.util.Logcat
import kr.co.smartbank.app.util.SharedPreferenceHelper
import kr.co.smartbank.app.view.BaseActivity

class KSW_Activity_Cspwd_Mtk : BaseActivity() {
    private lateinit var activity   : Activity
    private lateinit var context    : Context
    private lateinit var sp         : SharedPreferenceHelper

    private lateinit var cspwd_pwd :Button

    private lateinit var ksw_cspwd_close :ImageView

    private lateinit var edt_KeyPad: ESEditText

    private lateinit var ksw_keypad: ViewGroup

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.ksw_activity_cspwd_mtk)
        activity = this
        context = applicationContext
        sp = SharedPreferenceHelper(activity)

        ksw_cspwd_close=findViewById(R.id.ksw_cspwd_close) as ImageView
        ksw_cspwd_close.setOnClickListener{
            finish()
        }

        //보안키패드영역
        ksw_keypad=findViewById(R.id.ksw_keypad) as ViewGroup

        //패스워드 입력창
        cspwd_pwd=findViewById(R.id.cspwd_pwd)
        cspwd_pwd.setOnClickListener {
            edt_KeyPad.clear()
            showKeyPad(ksw_keypad,edt_KeyPad,context)
        }
    }

    override fun onResume() {
        super.onResume()
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        var commonDto = CommonDTO(activity, supportFragmentManager, sp)
        ActivityResult().onActivityResult(requestCode, resultCode, data, commonDto)
    }


    //커스텀 보안 키패드
    fun showKeyPad(viewGroup: ViewGroup, esEditText: ESEditText, context: Context) {
        Logcat.dd("보안키패드 열기 ====")

        val builder = ESEditTextParams.Builder()
        builder.setTargetLayout(viewGroup)
        builder.setMaxInputLength(30)
        builder.setKeypadLayoutManager(CustomQwertyLayout())
        builder.setKeypadAppearanceManager(CustomQwertyAppearance(context.resources))
        builder.setEncryptMethodWithKey(ESKeypadParams.EncryptMethod.e_seed, Base64Util.getBase64encode(Constants.keyPadPrivateKey))
        builder.setKeypadType(ESKeypadParams.KeypadType.e_qwerty)
        builder.setOnKeypadListener(object : ESEditText.OnKeypadListener {
            override fun secureKeypadDone(esEditText: ESEditText) {
                val result = esEditText.secureKeypadResult
                val encDate = result.encryptedString
                Logcat.d("esEditText Done : " + result.plainText)
                Logcat.d("encDate : " + encDate)
                Logcat.d("base64 : "+ Base64Util.getBase64encode(Constants.keyPadPrivateKey))
            }
            override fun secureKeypadCancel() {
                Logcat.d("esEditText Cancel ")
            }
            override fun secureKeypadTextLengthChanged(esEditText: ESEditText) {
                val result = esEditText.secureKeypadResult
                Logcat.d("esEditText Done : " + result.plainText)
                //showPwTransPad(esEditText.secureKeypadResult.plainText.length)
            }
        })
        esEditText.init(builder.build())
        esEditText.showSecureKeypad()
    }
}