package kr.co.smartbank.app.view

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import com.lumensoft.ks.KSCertificateManager
import com.lumensoft.ks.KSException
import kr.co.smartbank.app.R
import kr.co.smartbank.app.util.Logcat
import kr.co.smartbank.app.util.SharedPreferenceHelper
import java.util.*


class PermissionActivity : BaseActivity() {
    private lateinit var activity   : Activity
    private lateinit var context    : Context
    private lateinit var sp         : SharedPreferenceHelper
    private lateinit var next       : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_permission)
        activity    =   this
        context     =   applicationContext
        sp          =   SharedPreferenceHelper(activity)
        // PerssionActivity로 이동했기 때문에 다음 실행부터는 최초 실행인 케이스가 아니다.

        next=findViewById(R.id.permission_activity_next)
        next.setOnClickListener{
            if (checkPermission()) {
                //sp.put(SharedPreferenceHelper.IS_FIRST_INSTALL_PERMISSION, false)
                // 토큰 존재 유무 및 custNo 존재 유무를 판단해서 바로 메인화면으로 넘겨줄 것인지 사용자 인증 화면으로 넘겨줄 것인지 정해야한다.
                // custNo 저장 시점은?
                // 사용자 인증 화면은 매번 띄워줘야 하는 건인가?
                goMain()
            }
        }

    }

    private fun goMain() {
        val intent = Intent(activity, SplashActivity::class.java)
        startActivity(intent)
        //overridePendingTransition(R.anim.slide_up, R.anim.slide_bottom)
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
                if (ActivityCompat.checkSelfPermission(activity, perm ) != PackageManager.PERMISSION_GRANTED) {
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
            goMain()
        }
    }
}
