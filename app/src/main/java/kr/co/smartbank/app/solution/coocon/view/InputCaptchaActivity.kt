package kr.co.smartbank.app.solution.coocon.view

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Base64
import android.widget.*
import kr.co.smartbank.app.R
import kr.co.smartbank.app.process.ActivityResult
import kr.co.smartbank.app.process.CommonDTO
import kr.co.smartbank.app.util.Logcat
import kr.co.smartbank.app.util.SharedPreferenceHelper
import kr.co.smartbank.app.view.BaseActivity

class InputCaptchaActivity : BaseActivity(){
    private lateinit var activity: Activity
    private lateinit var sp: SharedPreferenceHelper
    private lateinit var imvCaptcha: ImageView
    private lateinit var edtInputCode: EditText
    private lateinit var ivBtnRefresh: ImageView


    private lateinit var onClickBtnNo: Button

    private lateinit var onClickBtnOk: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_input_captcha)
        activity    = this
        sp          = SharedPreferenceHelper(activity)

        edtInputCode= findViewById(R.id.edtInputCode)
        val common = CommonDTO(activity, supportFragmentManager, sp)
        common.customProgressDialog=customProgressDialog


        imvCaptcha = findViewById(R.id.imvCaptcha)
        ivBtnRefresh= findViewById(R.id.captcha_btn_refresh)
        //ivBtnRefresh.visibility= View.GONE
        ivBtnRefresh.setOnClickListener{
            setResult(Activity.RESULT_FIRST_USER, intent)
            //activity.//overridePendingTransition(R.anim.slide_up, R.anim.slide_bottom)
            finish()
        }

        customProgressDialog.Hide()



        Logcat.d("[userCert.subjectDn]" + intent.getStringExtra("subjectDn"))
        Logcat.d("[userCert.expiredTime]" + intent.getStringExtra("expiredTime"))
        Logcat.d("[userCert.inputPw]" + intent.getStringExtra("inputPw"))




        val handler:Handler = object : Handler(Looper.getMainLooper()){
            override fun handleMessage(msg: Message) {
                try {
                    customProgressDialog.Show()
                } catch (e: Exception) {
                    Logcat.e("???????????????")
                }


                val captchaImage = intent.getStringExtra("captchaImage")
                if(captchaImage.isNullOrEmpty()) {
                    Toast.makeText(activity, "???????????? ????????? ???????????? ???????????????. ?????? ??????????????????.", Toast.LENGTH_SHORT).show()
                    finish()
                    return
                }
                Logcat.d("captchaImage: $captchaImage")
                val bytes: ByteArray = Base64.decode(captchaImage, Base64.DEFAULT)
                imvCaptcha.setImageBitmap(BitmapFactory.decodeByteArray(bytes, 0, bytes.size))

                try {
                    customProgressDialog.Hide()
                } catch (e: Exception) {
                    Logcat.e("???????????????")
                }
            }
        }
        handler.obtainMessage().sendToTarget()

        //????????? ???????????? ???????????????
        //Scraping().secureString(common,imvCaptcha)
        onClickBtnOk= findViewById(R.id.onClickBtnOk)
        onClickBtnOk.setOnClickListener{
            if(edtInputCode.text.toString().isEmpty()) {
                alertDlg("??????????????? ??????????????????.", activity)
            }else{

                val code = edtInputCode.text.toString()
                Logcat.d("code: $code")
                val intent = Intent()
                intent.putExtra("code", code)
                intent.putExtra("subjectDn", this.intent.getStringExtra("subjectDn"))
                intent.putExtra("expiredTime", this.intent.getStringExtra("expiredTime"))
                intent.putExtra("inputPw", this.intent.getStringExtra("inputPw"))
                setResult(Activity.RESULT_OK, intent)
                //overridePendingTransition(R.anim.slide_up, R.anim.slide_bottom)
                finish()
            }
        }

        onClickBtnNo= findViewById(R.id.onClickBtnNo)
        onClickBtnNo.setOnClickListener{
            finish()
            //overridePendingTransition(R.anim.slide_up, R.anim.slide_bottom)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val commonDto = CommonDTO(activity, supportFragmentManager, sp)
        //commonDto.setUserCert(userCert);
        //commonDto.setUserCert(userCert);
        commonDto.customProgressDialog = customProgressDialog
       // if (runMode == Constants.KSW_Activity_SCRAPING) {
       //     commonDto.parmas = intent.getStringExtra("params")!!
        //}

        ActivityResult().onActivityResult(requestCode, resultCode, data, commonDto)
    }
}