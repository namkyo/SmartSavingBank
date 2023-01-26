package kr.co.smartbank.app.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import kr.co.smartbank.app.config.Constants
import kr.co.smartbank.app.util.Logcat
import kr.co.smartbank.app.util.SharedPreferenceHelper

class PushActivity : BaseActivity() {
    private lateinit var activity: Activity
    private lateinit var sp: SharedPreferenceHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activity = this
        sp = SharedPreferenceHelper(activity!!)

        val intent = Intent(activity, SplashActivity::class.java)
        intent.putExtra(Constants.PURPOSE_OF_INTENT, Constants.PURPOSE_OF_INTENT_PUSH)

        val pushLnkUrl = getIntent().getStringExtra("pushLnkUrl")
        Logcat.d("pushLnkUrl: $pushLnkUrl")
        intent.putExtra("pushLnkUrl", pushLnkUrl)

        startActivity(intent)
        finishAffinity()
    }
}
