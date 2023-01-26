package kr.co.smartbank.app.solution.everspin.secureKeypad

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import android.widget.ImageView
import kr.co.everspin.eversafe.keypad.params.ESEditTextParams
import kr.co.everspin.eversafe.keypad.params.ESKeypadParams
import kr.co.everspin.eversafe.keypad.widget.ESEditText
import kr.co.everspin.eversafe.keypad.widget.ESEditText.OnKeypadListener
import kr.co.smartbank.app.R
import kr.co.smartbank.app.config.Constants
import kr.co.smartbank.app.util.Base64Util
import kr.co.smartbank.app.util.CryptoUtil
import kr.co.smartbank.app.util.Logcat
import kr.co.smartbank.app.util.SharedPreferenceHelper
import kr.co.smartbank.app.view.BaseActivity

class PinView : BaseActivity()  {
    private lateinit var activity   : Activity
    private lateinit var context    : Context
    private lateinit var sp         : SharedPreferenceHelper


    private lateinit var targetLayout1: ViewGroup
    private lateinit var targetLayout2: ViewGroup
    private lateinit var esEdit: ESEditText


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.keypad_activity_pin)
        activity = this
        context = applicationContext
        sp = SharedPreferenceHelper(activity)

        targetLayout1= findViewById(R.id.llInputLayout)
        targetLayout2= findViewById(R.id.keypad_q)
        esEdit= findViewById(R.id.edt_q)

        targetLayout1.setOnClickListener{
            esEdit.clear()
            //키패드열기
            showKeyPad(targetLayout2, esEdit)
        }
        //키패드열기
        showKeyPad(targetLayout2, esEdit)

        val btn_close = findViewById<ImageView>(R.id.btn_close)
        btn_close.setOnClickListener{
            activity.finish()
            //activity.//overridePendingTransition(R.anim.slide_up, R.anim.slide_bottom)
        }
    }
    //커스텀 보안 키패드
    private fun showKeyPad(viewGroup: ViewGroup, esEditText: ESEditText) {
        Logcat.d("보안키패드 열기 ====")
        val builder = ESEditTextParams.Builder()
        builder.setTargetLayout(viewGroup)
        builder.setMaxInputLength(6)
        /*
        builder.setKeypadAppearanceManager(object :ESKeypadAppearanceManageable{
            var drawable : Drawable
            drawable=activity.resources.getDrawable(R.drawable.all_box_gray_bg)
            override fun getKeypadBackground(): Drawable {
                return drawable
            }
            override fun getCustomImageForNullKeys(): Drawable {
                return drawable
            }
            override fun getCustomForSpecialKey(p0: ESSpecialKeyTypes?): ESKeyViewInfo {
                var esKeyViewInfo:ESKeyViewInfo
                return esKeyViewInfo
            }
            override fun getCustomForCharacter(p0: Char): ESKeyViewInfo {
                var esKeyViewInfo:ESKeyViewInfo
                return esKeyViewInfo
            }
        })*/
        //builder.setKeypadLayoutManager(CustomNumberLayout())
        //builder.setKeypadAppearanceManager(CustomNumberAppearance(context))
        //builder.setEncryptMethodWithKey(ESKeypadParams.EncryptMethod.e_seed, Base64Util.getBase64encode(Constants.keyPadPrivateKey))
        builder.setKeypadType(ESKeypadParams.KeypadType.e_numeric)
        builder.setOnKeypadListener(object : OnKeypadListener {
            override fun secureKeypadDone(esEditText: ESEditText) {
                val result = esEditText.secureKeypadResult
                val encDate = result.encryptedString
                Logcat.d("esEditText Done : " + result.plainText)
                Logcat.d("encDate : $encDate")
                Logcat.d("base64 : "+Base64Util.getBase64encode(Constants.keyPadPrivateKey))
                if(result.plainText.length==6){
                    val intent = Intent()
                    intent.putExtra(resources.getString(R.string.FIN_FIRST),CryptoUtil.getInstace().encrypt(result.plainText))
                    activity.setResult(-1,intent)
                    activity.finish()
                    //activity.//overridePendingTransition(R.anim.slide_up, R.anim.slide_bottom)
                }else{
                    Logcat.d("6자리 요망")
                    BaseActivity().alertDlg("6자리 요망",activity)
                }
            }
            override fun secureKeypadCancel() {
                Logcat.d("esEditText Cancel ")
            }
            override fun secureKeypadTextLengthChanged(esEditText: ESEditText) {
                val result = esEditText.secureKeypadResult.plainText.length
                Logcat.d("esEditText size : $result")
                showPwTransPad(result)
                if(result==6){
                    intent.putExtra(resources.getString(R.string.FIN_FIRST),CryptoUtil.getInstace().encrypt(esEditText.secureKeypadResult.plainText))
                    activity.setResult(-1,intent)
                    activity.finish()
                    //activity.//overridePendingTransition(R.anim.slide_up, R.anim.slide_bottom)
                }
            }
        })
        esEditText.init(builder.build())
        esEditText.showSecureKeypad()
    }

    //비밀번호 입력 확인 이미지 새로고침
    fun showPwTransPad(numSize: Int) {

        val lo01: ImageView =activity.findViewById(R.id.imvInputNum1)
        val lo02: ImageView =activity.findViewById(R.id.imvInputNum2)
        val lo03: ImageView =activity.findViewById(R.id.imvInputNum3)
        val lo04: ImageView =activity.findViewById(R.id.imvInputNum4)
        val lo05: ImageView =activity.findViewById(R.id.imvInputNum5)
        val lo06: ImageView =activity.findViewById(R.id.imvInputNum6)

        // 입력된 UI 초기화 (showKeyPad 호출되면 입력값이 초기화되므로 view를 동기화 해준다.)
        when(numSize){
            1 -> {
                lo01.setImageResource(R.drawable.input_pin_num_on)
                lo02.setImageResource(R.drawable.input_pin_num_off)
                lo03.setImageResource(R.drawable.input_pin_num_off)
                lo04.setImageResource(R.drawable.input_pin_num_off)
                lo05.setImageResource(R.drawable.input_pin_num_off)
                lo06.setImageResource(R.drawable.input_pin_num_off)
            }
            2 -> {
                lo01.setImageResource(R.drawable.input_pin_num_on)
                lo02.setImageResource(R.drawable.input_pin_num_on)
                lo03.setImageResource(R.drawable.input_pin_num_off)
                lo04.setImageResource(R.drawable.input_pin_num_off)
                lo05.setImageResource(R.drawable.input_pin_num_off)
                lo06.setImageResource(R.drawable.input_pin_num_off)
            }
            3 -> {
                lo01.setImageResource(R.drawable.input_pin_num_on)
                lo02.setImageResource(R.drawable.input_pin_num_on)
                lo03.setImageResource(R.drawable.input_pin_num_on)
                lo04.setImageResource(R.drawable.input_pin_num_off)
                lo05.setImageResource(R.drawable.input_pin_num_off)
                lo06.setImageResource(R.drawable.input_pin_num_off)
            }
            4 -> {
                lo01.setImageResource(R.drawable.input_pin_num_on)
                lo02.setImageResource(R.drawable.input_pin_num_on)
                lo03.setImageResource(R.drawable.input_pin_num_on)
                lo04.setImageResource(R.drawable.input_pin_num_on)
                lo05.setImageResource(R.drawable.input_pin_num_off)
                lo06.setImageResource(R.drawable.input_pin_num_off)
            }
            5 -> {
                lo01.setImageResource(R.drawable.input_pin_num_on)
                lo02.setImageResource(R.drawable.input_pin_num_on)
                lo03.setImageResource(R.drawable.input_pin_num_on)
                lo04.setImageResource(R.drawable.input_pin_num_on)
                lo05.setImageResource(R.drawable.input_pin_num_on)
                lo06.setImageResource(R.drawable.input_pin_num_off)
            }
            6 -> {
                lo01.setImageResource(R.drawable.input_pin_num_on)
                lo02.setImageResource(R.drawable.input_pin_num_on)
                lo03.setImageResource(R.drawable.input_pin_num_on)
                lo04.setImageResource(R.drawable.input_pin_num_on)
                lo05.setImageResource(R.drawable.input_pin_num_on)
                lo06.setImageResource(R.drawable.input_pin_num_on)
            }
            else -> {
                lo01.setImageResource(R.drawable.input_pin_num_off)
                lo02.setImageResource(R.drawable.input_pin_num_off)
                lo03.setImageResource(R.drawable.input_pin_num_off)
                lo04.setImageResource(R.drawable.input_pin_num_off)
                lo05.setImageResource(R.drawable.input_pin_num_off)
                lo06.setImageResource(R.drawable.input_pin_num_off)
                // showKeyPad 호출되면 입력값이 초기화되므로 저장된 값도 초기화 해준다.
                esEdit.clear()
            }
        }
    }
}