package kr.co.smartbank.app.view

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import kr.co.smartbank.app.R
import kr.co.smartbank.app.config.Constants
import kr.co.smartbank.app.util.Logcat
import kr.co.smartbank.app.util.SharedPreferenceHelper
import java.util.ArrayList


class UserAuthActivity : BaseActivity() {
    private lateinit var activity: Activity
    private lateinit var sp: SharedPreferenceHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_user_auth)

        activity = this
        sp = SharedPreferenceHelper(activity)

    }

    fun onClickBtnOk(view: View) {
        if (checkPermission()) {
            signUp()
        }
    }

    private fun signUp() {

        val intent = Intent(activity, MainActivity::class.java)
        intent.putExtra("url", Constants.WEB_SIGN_UP)
        startActivity(intent)
        finish()
    }

    private fun checkPermission(): Boolean {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val permissions = arrayOf(
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.READ_PHONE_NUMBERS,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA
            )

            val deniedPermissions = ArrayList<String>()
            for (perm in permissions) {
                if (ActivityCompat.checkSelfPermission(
                        this,
                        perm
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    deniedPermissions.add(perm)
                }
            }

            if (deniedPermissions.size > 0) {
                val deniedPerms = deniedPermissions.toTypedArray()
                ActivityCompat.requestPermissions(this, deniedPerms, 53874)
                return false
            }

        }
        return true
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        var isGranted = true

        for (i in grantResults.indices) {
            Logcat.d("grantResult[i]: " + grantResults[i])

            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) isGranted = false
        }

        if (!isGranted) {

            try {
                val builder = AlertDialog.Builder(this)
                builder.setMessage("앱을 사용하기 위한 필수 권한이 허용되지 않아 앱을 종료합니다. [설정] > [애플리케이션] 메뉴에서 앱의 필수 권한을 허용하고 다시 시도해주세요.")
                builder.setCancelable(false)
                builder.setPositiveButton(
                    "확인"
                ) { dialog, which ->
                    finishAffinity()
                }
                builder.show()

            } catch (e: WindowManager.BadTokenException) {
                Logcat.e("에러입니다 [${e.message}]")
            } catch (e: Exception) {
                Logcat.e("에러입니다 [${e.message}]")
            }
        } else {    // permission granted
            signUp()
        }
    }
}
