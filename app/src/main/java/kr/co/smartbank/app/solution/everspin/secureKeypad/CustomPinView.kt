package kr.co.smartbank.app.solution.everspin.secureKeypad

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import kr.co.everspin.eversafe.eversafenumericpadexample.keypad.NumericpadHelper
import kr.co.smartbank.app.R
import kr.co.smartbank.app.databinding.ActivityKeypadBinding
import kr.co.smartbank.app.util.Logcat
import kr.co.smartbank.app.util.SharedPreferenceHelper
import kr.co.smartbank.app.view.BaseActivity

class CustomPinView :  BaseActivity()  {
    private lateinit var activity   : Activity
    private lateinit var context    : Context
    private lateinit var sp         : SharedPreferenceHelper

    private val TAG = CustomPinView::class.java.simpleName
    private var numpadIndicators = emptyArray<ImageView>()
    private lateinit var binding: ActivityKeypadBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_keypad)
        activity = this
        context = applicationContext
        sp = SharedPreferenceHelper(activity)
        initView()
    }

    override fun onResume() {
        super.onResume()
    }

    private fun initView(){
        binding = DataBindingUtil.setContentView(this, R.layout.activity_keypad)

        val custom_keypad_title = findViewById<TextView>(R.id.custom_keypad_title)
        val custom_keypad_sub_title = findViewById<TextView>(R.id.custom_keypad_sub_title)
        val custom_keypad_sub_hint = findViewById<TextView>(R.id.custom_keypad_sub_hint)

        val title =intent.getStringExtra("title")!!
        //val sub_title =intent.getStringExtra("sub_title")!!
        //val hint =intent.getStringExtra("hint")!!


        custom_keypad_title.text = title
        custom_keypad_sub_title.text="보안키패드"
        custom_keypad_sub_hint.text="6 자리 입력하세요"


        binding.ivNumpad1.visibility=View.VISIBLE
        binding.ivNumpad2.visibility=View.VISIBLE
        binding.ivNumpad3.visibility=View.VISIBLE
        binding.ivNumpad4.visibility=View.VISIBLE
        binding.ivNumpad5.visibility=View.VISIBLE
        binding.ivNumpad6.visibility=View.VISIBLE
        numpadIndicators = arrayOf(
                binding.ivNumpad1,
                binding.ivNumpad2,
                binding.ivNumpad3,
                binding.ivNumpad4,
                binding.ivNumpad5,
                binding.ivNumpad6
        )

        val eversafeKeypadHelper = NumericpadHelper(
                eset = binding.esetNumpad,
                doneCallback = object: NumericpadHelper.NumericpadCallback {
                    override fun onChanged(count: Int) {
                        Log.d(TAG, "input length: $count")
                        setIndicator(count)
                    }
                    override fun onDone(data: String,count: Int) {
                        Log.d(TAG, "final data: $data")

                        if(count==6){
                            finishKeypad(data)
                        }else{
                            Logcat.d("6자리 요망")
                            BaseActivity().alertDlg("6자리 요망",activity)
                        }
                    }
                },
                maxNum = 6,
                key = "",
                type = 1
        )
        eversafeKeypadHelper.buildKeypad(this.resources)
        eversafeKeypadHelper.startKeypad()

        binding.ivNumpadCancel.setOnClickListener{
            eversafeKeypadHelper.closeKeypad()
            cancelKeypad()
        }
    }

    private fun setIndicator(cnt: Int){
        clearIndicator()
        for(i in 0 until cnt){
            numpadIndicators[i].setImageResource(R.drawable.ic_baseline_brightness_1_36_fill)
        }
    }

    private fun clearIndicator(){
        for(i in 0 until 6){
            numpadIndicators[i].setImageResource(R.drawable.ic_baseline_brightness_1_36_empty)
        }
    }

    private fun finishKeypad(data: String?){
        binding.clNumpadIndicator.visibility = View.GONE
        val intentReturn = Intent()
        //intentReturn.putExtra(resources.getString(R.string.FIN_FIRST), CryptoUtil.getInstace().encrypt(data))
        intentReturn.putExtra(resources.getString(R.string.FIN_FIRST), data)
        setResult(Activity.RESULT_OK, intentReturn)
        finish()
        //activity.//overridePendingTransition(R.anim.slide_up, R.anim.slide_bottom)
    }
    private fun cancelKeypad(){
        binding.clNumpadIndicator.visibility = View.GONE
        val intentReturn = Intent()
        setResult(Activity.RESULT_CANCELED, intentReturn)
        finish()
        //activity.//overridePendingTransition(R.anim.slide_up, R.anim.slide_bottom)
    }
}