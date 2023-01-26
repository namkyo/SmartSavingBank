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
import kr.co.smartbank.app.util.CryptoUtil
import kr.co.smartbank.app.util.Logcat
import kr.co.smartbank.app.util.SharedPreferenceHelper
import kr.co.smartbank.app.view.BaseActivity


class CustomView : BaseActivity()  {
    private lateinit var activity   : Activity
    private lateinit var context    : Context
    private lateinit var sp         : SharedPreferenceHelper

    private val TAG = CustomView::class.java.simpleName
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

    private fun initView(){
        binding = DataBindingUtil.setContentView(this, R.layout.activity_keypad)

        var custom_keypad_title = findViewById(R.id.custom_keypad_title) as TextView
        var custom_keypad_sub_title = findViewById(R.id.custom_keypad_sub_title) as TextView
        var custom_keypad_sub_hint = findViewById(R.id.custom_keypad_sub_hint) as TextView



        var rsakey =intent.getStringExtra("key")!!
        var title =intent.getStringExtra("title")!!
        var sub_title =intent.getStringExtra("sub_title")!!
        var min =intent.getIntExtra("min",0)!!
        var max =intent.getIntExtra("max",0)!!

        custom_keypad_title.text = title
        custom_keypad_sub_title.text="보안키패드"
        custom_keypad_sub_hint.text="$max 자리 입력하세요"

        when(max){
            4 ->{
                binding.ivNumpad1.visibility=View.VISIBLE
                binding.ivNumpad2.visibility=View.VISIBLE
                binding.ivNumpad3.visibility=View.VISIBLE
                binding.ivNumpad4.visibility=View.VISIBLE
                numpadIndicators = arrayOf(
                    binding.ivNumpad1,
                    binding.ivNumpad2,
                    binding.ivNumpad3,
                    binding.ivNumpad4
                )
            }
            6 -> {
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
            }
            7 -> {
                binding.ivNumpad1.visibility=View.VISIBLE
                binding.ivNumpad2.visibility=View.VISIBLE
                binding.ivNumpad3.visibility=View.VISIBLE
                binding.ivNumpad4.visibility=View.VISIBLE
                binding.ivNumpad5.visibility=View.VISIBLE
                binding.ivNumpad6.visibility=View.VISIBLE
                binding.ivNumpad7.visibility=View.VISIBLE
                numpadIndicators = arrayOf(
                    binding.ivNumpad1,
                    binding.ivNumpad2,
                    binding.ivNumpad3,
                    binding.ivNumpad4,
                    binding.ivNumpad5,
                    binding.ivNumpad6,
                    binding.ivNumpad7
                )
            }
        }

        val eversafeKeypadHelper = NumericpadHelper(
            eset = binding.esetNumpad,
            doneCallback = object: NumericpadHelper.NumericpadCallback {
                override fun onChanged(count: Int) {
                    Log.d(TAG, "input length: $count")
                    setIndicator(count,max)
                }
                override fun onDone(data: String,count: Int) {
                    Log.d(TAG, "final count: $count")
                    Log.d(TAG, "final data: $data")

                    if(data.length>min){
                        finishKeypad(data)
                    }else{
                        Logcat.d("최소 $min 자리 요망")
                        alertDlg("최소 $min 자리 요망",activity)
                    }
                }
            },
            maxNum = max,
            key = rsakey,
                type = 1
        )
        eversafeKeypadHelper.buildKeypad(this.resources)
        eversafeKeypadHelper.startKeypad()

        binding.ivNumpadCancel.setOnClickListener{
            eversafeKeypadHelper.closeKeypad()
            cancelKeypad()
        }
    }

    private fun setIndicator(count: Int,max: Int){
        clearIndicator(max)
        for(i in 0 until count){
            numpadIndicators[i].setImageResource(R.drawable.ic_baseline_brightness_1_36_fill)
        }
    }

    private fun clearIndicator(max: Int){
        for(i in 0 until max){
            numpadIndicators[i].setImageResource(R.drawable.ic_baseline_brightness_1_36_empty)
        }
    }

    private fun finishKeypad(data: String?){
        binding.clNumpadIndicator.visibility = View.GONE
        val intentReturn = Intent()
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