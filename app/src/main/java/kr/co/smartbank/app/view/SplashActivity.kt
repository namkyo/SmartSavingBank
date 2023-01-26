package kr.co.smartbank.app.view

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import com.lumensoft.ks.KSCertificateManager
import com.lumensoft.ks.KSException
import com.nprotect.IxSecureManager
import kr.co.smartbank.app.R
import kr.co.smartbank.app.config.Constants
import kr.co.smartbank.app.solution.everspin.everSafe.EverSafeManager
import kr.co.smartbank.app.util.*
import java.util.*

class SplashActivity : BaseActivity() {
    private lateinit var activity   : Activity
    private lateinit var context    : Context
    private lateinit var sp         : SharedPreferenceHelper

    private var dialog : AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        activity    =   this
        context     =   applicationContext
        sp          =   SharedPreferenceHelper(activity)

        //솔루션 체크
        //솔루션 검증오류시 앱종료
        if (!setting()) {
            Toast.makeText(activity, "솔루션에러", Toast.LENGTH_SHORT).show()
            Handler(Looper.getMainLooper()).postDelayed({
                activity.finishAffinity()
                //  딜레이를 준 후 시작
            },1000)
        }else{
            Handler(Looper.getMainLooper()).postDelayed({
                if (permissionCheck()) {
                        goMain()
                } else {
                    /*
                     1. 최초 설치이므로 권한허용안내 화면으로 이동한다.
                     2. 퍼미션 권한 부족일때 이동한다.
                     */
                    val intent = Intent(activity, PermissionActivity::class.java)
                    startActivity(intent)
                    //overridePendingTransition(R.anim.slide_up, R.anim.slide_bottom)
                }
            },1000)
        }
    }

    /**
     * 솔루션 모듈 초기화
     */
    private fun  setting(): Boolean {
        var settingYn = true

        /*
            1 . 공인인증솔루션 초기화
         */
        kswInit()
        Logcat.d("코스콤초기화")


        /*
           2 . FDS 라이센스 설정
        */
        IxSecureManager.initLicense(activity, Constants.FDS_LICENCE, Constants.FDS_CUSTOM_ID)
        Logcat.d("FDS초기화")

        /*
            3 . 네트워크 상태 체크
         */
        val networkStatus = NetworkStatus.getConnectivityStatus(context)
        if(NETWORK_STATUS.TYPE_NOT_CONNECTED == networkStatus){
            settingYn=false
            val builder = AlertDialog.Builder(activity)
            builder.setMessage("네트워크 연결을 확인해주세요")
            builder.setCancelable(false)
            builder.setPositiveButton(
                    "확인"
            ) { _, _ ->
                finishAffinity()
            }
            dialog = builder.create()
            dialog.let {
                it?.show()
            }
        }
        Logcat.d("네트워크 상태 : " + NetworkStatus.getConnectivityStatus(context))

        return settingYn
    }


    /**
     * 검증후 메인액티비티로 이동
     */
    private fun goMain(){
        var url = ""
        when (Constants.MODE){
            Constants.MODE_H -> {
                url = Constants.WEB_MAIN_H + Constants.WEB_MAIN_VIEW
            }
            Constants.MODE_D -> {
                url = Constants.WEB_MAIN_D + Constants.WEB_MAIN_VIEW
            }
            Constants.MODE_R -> {
                url = Constants.WEB_MAIN_R + Constants.WEB_MAIN_VIEW
            }
            else ->{
                alertDlgAfFinish("비정상접근", activity)
            }
        }

        Logcat.d("loadURl : $url")
        val intent = Intent(activity, MainActivity::class.java).apply {
            putExtra("loadURl", url)
        }
        startActivity(intent)
        //overridePendingTransition(R.anim.slide_up, R.anim.slide_bottom)
        finish()
    }

    /**
        권한체크
     */
    private fun permissionCheck():Boolean {
        // 필요한 권한이 허용되어 있는지 확인만 한다.
        // 권한이 허용되어 있다면 이미 권한에 허용한 사람이기 때문에 앱을 한번이라도 실행해본 경우이고
        // 권한이 허용되어 있지 않다면 앱을 설치하고 최초 실행한 사람으로 생각한다.
        if (checkPermission()) {
            // 필요 권한이 허용되어있으면 최초 설치인지 확인한다.
            return if (sp.getValue(SharedPreferenceHelper.IS_FIRST_INSTALL_PERMISSION, true)) {
                Logcat.d("최초접속")
                true
            }else{
                Logcat.d("정상")
                true
            }
        } else {
            Logcat.d("퍼미션 권한부족")
        }
        return false
    }

    /**
        필요 권한이 허용되어있는지만 확인한다. 권한 요청은 없음
     */
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
                if (ActivityCompat.checkSelfPermission(applicationContext, perm) != PackageManager.PERMISSION_GRANTED) {
                    deniedPermissions.add(perm)
                }
            }
            if (deniedPermissions.size > 0) {
                return false
            }
        }
        return true
    }


    /**
     * 공동인증모듈 초기화
     */
    private fun kswInit(){
        // so파일 로딩부
        if (KSCertificateManager.libInitialize(context) == KSException.APP_NATIVE_INIT_SUCC) {
            Logcat.d("App 설치경로 네이티브 경로지정 성공")
            //Toast.makeText(applicationContext, "App 설치경로 네이티브 경로지정 성공",Toast.LENGTH_LONG).show()
        } else if (KSCertificateManager.libInitialize() == KSException.SYSTEM_NATIVE_INIT_SUCC) {
           // Toast.makeText(applicationContext, "System 네이티브 경로지정 성공",Toast.LENGTH_LONG).show()
            Logcat.d("System 네이티브 경로지정 성공")
        } else {
            //Toast.makeText(applicationContext, "네이티브 경로지정 실패", Toast.LENGTH_LONG).show()
            Logcat.d("네이티브 경로지정 실패")
            finishAffinity()
        }
    }

    override fun onStop() {
        super.onStop()
        dialog?.dismiss()
    }
}