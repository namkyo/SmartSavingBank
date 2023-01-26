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
import kr.co.smartbank.app.process.ActivityResult
import kr.co.smartbank.app.process.CommonDTO
import kr.co.smartbank.app.util.Logcat
import kr.co.smartbank.app.util.SharedPreferenceHelper
import kr.co.smartbank.app.view.BaseActivity

class QwertyView: BaseActivity()  {
    private lateinit var activity   : Activity
    private lateinit var context    : Context
    private lateinit var sp         : SharedPreferenceHelper

    private val TAG = QwertyView::class.java.simpleName
    private var numpadIndicators = emptyArray<ImageView>()
    private lateinit var binding: ActivityKeypadBinding

    private lateinit var startBar:View
    private lateinit var startBar2:View
    private lateinit var startBarText:TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_keypad)
        activity = this
        context = applicationContext
        sp = SharedPreferenceHelper(activity)
        initView()
    }
    private fun initView() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_keypad)

        val custom_keypad_title = findViewById<TextView>(R.id.custom_keypad_title)
        val custom_keypad_sub_title = findViewById<TextView>(R.id.custom_keypad_sub_title)
        val custom_keypad_sub_hint = findViewById<TextView>(R.id.custom_keypad_sub_hint)

        val rsakey =intent.getStringExtra("key")!!
        val title =intent.getStringExtra("title")!!
        //var sub_title =intent.getStringExtra("sub_title")!!
        val min =intent.getIntExtra("min",0)
        var max =intent.getIntExtra("max",0)

        //52자리
        if (max==20){
            max=52
        }
        custom_keypad_title.text = "보안키패드"
        custom_keypad_sub_title.text=title
        custom_keypad_sub_hint.text="$min 자리 이상 입력하세요"

        startBar = findViewById<View>(R.id.pin_startBar)
        startBar2 = findViewById<View>(R.id.pin_startBar2)
        startBarText = findViewById(R.id.pwTxfield)

        if(max==52){
            startBar.visibility=View.GONE
            startBar2.visibility=View.VISIBLE
        }


        when(max){
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
            10 -> {
                binding.ivNumpad1.visibility=View.VISIBLE
                binding.ivNumpad2.visibility=View.VISIBLE
                binding.ivNumpad3.visibility=View.VISIBLE
                binding.ivNumpad4.visibility=View.VISIBLE
                binding.ivNumpad5.visibility=View.VISIBLE
                binding.ivNumpad6.visibility=View.VISIBLE
                binding.ivNumpad7.visibility=View.VISIBLE
                binding.ivNumpad8.visibility=View.VISIBLE
                binding.ivNumpad9.visibility=View.VISIBLE
                binding.ivNumpad10.visibility=View.VISIBLE
                numpadIndicators = arrayOf(
                        binding.ivNumpad1,
                        binding.ivNumpad2,
                        binding.ivNumpad3,
                        binding.ivNumpad4,
                        binding.ivNumpad5,
                        binding.ivNumpad6,
                        binding.ivNumpad7,
                        binding.ivNumpad8,
                        binding.ivNumpad9,
                        binding.ivNumpad10
                )
            }
            12 -> {
                binding.ivNumpad1.visibility=View.VISIBLE
                binding.ivNumpad2.visibility=View.VISIBLE
                binding.ivNumpad3.visibility=View.VISIBLE
                binding.ivNumpad4.visibility=View.VISIBLE
                binding.ivNumpad5.visibility=View.VISIBLE
                binding.ivNumpad6.visibility=View.VISIBLE
                binding.ivNumpad7.visibility=View.VISIBLE
                binding.ivNumpad8.visibility=View.VISIBLE
                binding.ivNumpad9.visibility=View.VISIBLE
                binding.ivNumpad10.visibility=View.VISIBLE
                binding.ivNumpad11.visibility=View.VISIBLE
                binding.ivNumpad12.visibility=View.VISIBLE
                numpadIndicators = arrayOf(
                        binding.ivNumpad1,
                        binding.ivNumpad2,
                        binding.ivNumpad3,
                        binding.ivNumpad4,
                        binding.ivNumpad5,
                        binding.ivNumpad6,
                        binding.ivNumpad7,
                        binding.ivNumpad8,
                        binding.ivNumpad9,
                        binding.ivNumpad10,
                        binding.ivNumpad11,
                        binding.ivNumpad12
                )
            }
            14 -> {
                binding.ivNumpad1.visibility=View.VISIBLE
                binding.ivNumpad2.visibility=View.VISIBLE
                binding.ivNumpad3.visibility=View.VISIBLE
                binding.ivNumpad4.visibility=View.VISIBLE
                binding.ivNumpad5.visibility=View.VISIBLE
                binding.ivNumpad6.visibility=View.VISIBLE
                binding.ivNumpad7.visibility=View.VISIBLE
                binding.ivNumpad8.visibility=View.VISIBLE
                binding.ivNumpad9.visibility=View.VISIBLE
                binding.ivNumpad10.visibility=View.VISIBLE
                binding.ivNumpad11.visibility=View.VISIBLE
                binding.ivNumpad12.visibility=View.VISIBLE
                binding.ivNumpad13.visibility=View.VISIBLE
                binding.ivNumpad14.visibility=View.VISIBLE
                numpadIndicators = arrayOf(
                        binding.ivNumpad1,
                        binding.ivNumpad2,
                        binding.ivNumpad3,
                        binding.ivNumpad4,
                        binding.ivNumpad5,
                        binding.ivNumpad6,
                        binding.ivNumpad7,
                        binding.ivNumpad8,
                        binding.ivNumpad9,
                        binding.ivNumpad10,
                        binding.ivNumpad11,
                        binding.ivNumpad12,
                        binding.ivNumpad13,
                        binding.ivNumpad14
                )
            }
            20 -> {
                binding.ivNumpad1.visibility=View.VISIBLE
                binding.ivNumpad2.visibility=View.VISIBLE
                binding.ivNumpad3.visibility=View.VISIBLE
                binding.ivNumpad4.visibility=View.VISIBLE
                binding.ivNumpad5.visibility=View.VISIBLE
                binding.ivNumpad6.visibility=View.VISIBLE
                binding.ivNumpad7.visibility=View.VISIBLE
                binding.ivNumpad8.visibility=View.VISIBLE
                binding.ivNumpad9.visibility=View.VISIBLE
                binding.ivNumpad10.visibility=View.VISIBLE
                binding.ivNumpad11.visibility=View.VISIBLE
                binding.ivNumpad12.visibility=View.VISIBLE
                binding.ivNumpad13.visibility=View.VISIBLE
                binding.ivNumpad14.visibility=View.VISIBLE
                binding.ivNumpad15.visibility=View.VISIBLE
                binding.ivNumpad16.visibility=View.VISIBLE
                binding.ivNumpad17.visibility=View.VISIBLE
                binding.ivNumpad18.visibility=View.VISIBLE
                binding.ivNumpad19.visibility=View.VISIBLE
                binding.ivNumpad20.visibility=View.VISIBLE
                numpadIndicators = arrayOf(
                        binding.ivNumpad1,
                        binding.ivNumpad2,
                        binding.ivNumpad3,
                        binding.ivNumpad4,
                        binding.ivNumpad5,
                        binding.ivNumpad6,
                        binding.ivNumpad7,
                        binding.ivNumpad8,
                        binding.ivNumpad9,
                        binding.ivNumpad10,
                        binding.ivNumpad11,
                        binding.ivNumpad12,
                        binding.ivNumpad13,
                        binding.ivNumpad14,
                        binding.ivNumpad15,
                        binding.ivNumpad16,
                        binding.ivNumpad17,
                        binding.ivNumpad18,
                        binding.ivNumpad19,
                        binding.ivNumpad20
                )
            }

        }


        val eversafeKeypadHelper = NumericpadHelper(
                eset = binding.esetNumpad,
                doneCallback = object: NumericpadHelper.NumericpadCallback {
                    override fun onChanged(count: Int) {
                        Log.d(TAG, "input length: $count")


//                        setIndicator(count,max)
                        if (max==52){
                            var setStarText = ""
                            for (star in 1..count) {
                                setStarText+="*"
                            }
                            Logcat.d("star : $setStarText")
                            startBarText.text=setStarText
                        }else{
                            setIndicator(count,max)
                        }
                    }
                    override fun onDone(data: String,count: Int) {
                        Log.d(TAG, "final count: $count")
                        Log.d(TAG, "final data: $data")

                        if(count>=min){
                            finishKeypad(data)
                        }else if(max==52) {
                            finishKeypad(data)
                        }else{
                                // NumericpadHelper().startKeypad()
                                Logcat.d("최소 $min 자리 요망")
                                alertDlg("최소 $min 자리 요망",activity)
                                return
                            }
                    }
                },
                maxNum = max,
                key = rsakey,
                type = 2
        )
        eversafeKeypadHelper.buildKeypad(this.resources)
        eversafeKeypadHelper.startKeypad()

        binding.ivNumpadCancel.setOnClickListener{
            eversafeKeypadHelper.closeKeypad()
            cancelKeypad()
        }


        if (max!=52){

            //전체가리기
            for(i in 0 until max){
                numpadIndicators[i].setImageResource(R.drawable.ic_baseline_brightness_1_36_fill)
                numpadIndicators[i].visibility=View.GONE
            }
        }

    }

    private fun setIndicator(count: Int,max: Int){

        if (max==52) {
            startBarText.text=""
        }else{
            clearIndicator(max)
            for(i in 0 until count){
                numpadIndicators[i].visibility=View.VISIBLE
            }
        }
    }

    private fun clearIndicator(max: Int){
        if (max==52) {
            startBarText.text=""
        }else{

            for(i in 0 until max){
                numpadIndicators[i].visibility=View.GONE
            }
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
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val commonDto = CommonDTO(activity,supportFragmentManager,sp)
        ActivityResult().onActivityResult(requestCode, resultCode, data,commonDto)
    }
}