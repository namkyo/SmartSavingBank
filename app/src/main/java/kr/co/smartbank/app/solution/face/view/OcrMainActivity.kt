package kr.co.smartbank.app.solution.face.view

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.StyleSpan
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.inzisoft.mobile.recogdemolib.LibConstants
import kr.co.smartbank.app.R
import kr.co.smartbank.app.config.Constants
import kr.co.smartbank.app.solution.face.util.AppConstants
import kr.co.smartbank.app.util.Logcat
import kr.co.smartbank.app.util.SharedPreferenceHelper
import kr.co.smartbank.app.view.BaseActivity

class OcrMainActivity : BaseActivity() {
    private lateinit var activity   : Activity
    private lateinit var context    : Context
    private lateinit var sp         : SharedPreferenceHelper


    private val mOrientation = AppConstants.ORIENTATION_PORTRAIT //세로모드 ORIENTATION_LANDSCAPE 가로
    private val mRecogType = LibConstants.TYPE_IDCARD


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ocr_main)

        activity    =   this
        context     =   applicationContext
        sp          =   SharedPreferenceHelper(activity)


        val titleSub = findViewById<TextView>(R.id.face_activity_title_sub)

        //서브타이블 글씨 진하게 하게 하기
        val content = titleSub.text.toString()
        val words = arrayOf("주민등록증", "운전면허증")
        titleSub.text = textFilter(content, words)

        //클릭이벤트
        val closeBtn = findViewById<View>(R.id.face_activity_at_btn_close)
        closeBtn.setOnClickListener {
            val intent = Intent()
            setResult(0, intent)
            finish()
            //overridePendingTransition(R.anim.slide_up, R.anim.slide_bottom)
        }

        val nextBtn = findViewById<Button>(R.id.face_activity_next)
        nextBtn.setOnClickListener {
            val isTakeMode = intent.getBooleanExtra(resources.getString(R.string.ocr_take_mode),false)
            val isMasking = intent.getBooleanExtra(resources.getString(R.string.isMasking), false)
            val isDetail = intent.getBooleanExtra(resources.getString(R.string.isDetail), false)
            val isSerialNum = intent.getBooleanExtra(resources.getString(R.string.isSerialNum), false)


            val intent = Intent(activity, OcrPreviewActivity::class.java)
            intent.apply {
                putExtra(resources.getString(R.string.ocr_take_mode), isTakeMode)
                putExtra(resources.getString(R.string.isMasking), isMasking)
                putExtra(resources.getString(R.string.isDetail), isDetail)
                putExtra(resources.getString(R.string.isSerialNum), isSerialNum)
            }
            startActivityForResult(intent, Constants.OCR_Activity_02)
        }

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode){
            Constants.OCR_Activity_02 ->{
                //OCR 결과 들고 복귀
                if(resultCode == Activity.RESULT_OK){
                    setResult(Activity.RESULT_OK,data)
                    finish()
                    Logcat.d("OCR 성공")
                }else{
                    Logcat.d("OCR 취소")
                }
            }
        }
    }

    private fun textFilter(content: String, words: Array<String>): SpannableString? {
        val spannableString = SpannableString(content)
        for (word in words) {
            val start = content.indexOf(word)
            val end = start + word.length
            spannableString.setSpan(
                StyleSpan(Typeface.BOLD),
                start,
                end,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            ) //글씨 진하게
        }
        return spannableString
    }
}