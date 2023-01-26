package kr.co.smartbank.app.view

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import kr.co.smartbank.app.config.Constants
import kr.co.smartbank.app.custom.CustomProgressDialog
import kr.co.smartbank.app.util.Logcat
import java.util.ArrayList


open class BaseActivity : AppCompatActivity() {
    lateinit var customProgressDialog: CustomProgressDialog
//    lateinit var customProgressDialogMsg: CustomProgressDialog

    fun alertDlg(msg: String) {
        try {
            val builder = AlertDialog.Builder(this)
            builder.setMessage(msg)
            builder.setCancelable(false)
            builder.setPositiveButton(
                    "확인"
            ) { _, _ ->

            }
            builder.show()

        } catch (e: WindowManager.BadTokenException) {
            Logcat.e("에러입니다 [${e.message}]")
        } catch (e: Exception) {
            Logcat.e("에러입니다 [${e.message}]")
        }
    }
    fun alertDlg(msg: String,activity: Activity) {
        try {
            val builder = AlertDialog.Builder(activity)
            builder.setMessage(msg)
            builder.setCancelable(false)
            builder.setPositiveButton(
                "확인"
            ) { _, _ ->

            }
            builder.show()

        } catch (e: WindowManager.BadTokenException) {
            Logcat.e("에러입니다 [${e.message}]")
        } catch (e: Exception) {
            Logcat.e("에러입니다 [${e.message}]")
        }
    }

    fun alertDlgFinish(msg: String,activity: Activity) {
        try {
            val builder = AlertDialog.Builder(activity)
            builder.setMessage(msg)
            builder.setCancelable(false)
            builder.setPositiveButton(
                "확인"
            ) { _, _ ->
                activity.finish()
            }
            builder.show()

        } catch (e: WindowManager.BadTokenException) {
            Logcat.e("에러입니다 [${e.message}]")
        } catch (e: Exception) {
            Logcat.e("에러입니다 [${e.message}]")
        }
    }
//    fun alertDlgCancelFinish(msg: String,activity: Activity) {
//        try {
//            val builder = AlertDialog.Builder(activity)
//            builder.setMessage(msg)
//            builder.setCancelable(false)
//            builder.setPositiveButton(
//                    "확인"
//            ) { _, _ ->
//                activity.setResult(RESULT_CANCELED)
//                activity.finish()
//            }
//            builder.show()
//
//        } catch (e: WindowManager.BadTokenException) {
//            Logcat.e("에러입니다 [${e.message}]")
//        } catch (e: Exception) {
//            Logcat.e("에러입니다 [${e.message}]")
//        }
//    }
    fun alertDlgAfFinish(msg: String,activity: Activity) {
        try {
            val builder = AlertDialog.Builder(activity)
            builder.setMessage(msg)
            builder.setCancelable(false)
            builder.setPositiveButton(
                    "확인"
            ) { _, _ ->
                activity.finishAffinity()
            }
            builder.show()

        } catch (e: WindowManager.BadTokenException) {
            Logcat.e("에러입니다 [${e.message}]")
        } catch (e: Exception) {
            Logcat.e("에러입니다 [${e.message}]")
        }
    }



//    fun confirmDlg(msg: String) {
//        try {
//            val builder = AlertDialog.Builder(this)
//            builder.setMessage(msg)
//            builder.setCancelable(false)
//            builder.setPositiveButton(
//                "확인"
//            ) { dialog, which ->
//
//            }
//            builder.setNegativeButton(
//                "취소"
//            ) { dialog, which ->
//
//            }
//            builder.show()
//
//        } catch (e: WindowManager.BadTokenException) {
//            Logcat.e("에러입니다 [${e.message}]")
//        } catch (e: Exception) {
//            Logcat.e("에러입니다 [${e.message}]")
//        }
//    }

//    fun confirmDlgFinish(msg: String) {
//        try {
//            val builder = AlertDialog.Builder(this)
//            builder.setMessage(msg)
//            builder.setCancelable(false)
//            builder.setPositiveButton(
//                "확인"
//            ) { dialog, which ->
//                finish()
//            }
//            builder.setNegativeButton(
//                "취소"
//            ) { dialog, which ->
//                finish()
//            }
//            builder.show()
//
//        } catch (e: WindowManager.BadTokenException) {
//            Logcat.e("에러입니다 [${e.message}]")
//        } catch (e: Exception) {
//            Logcat.e("에러입니다 [${e.message}]")
//        }
//    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //운영일때만 화면 캡쳐 방지
        if(Constants.MODE==Constants.MODE_R){
            window.addFlags(WindowManager.LayoutParams.FLAG_SECURE)
        }

//        val isIsolatedProcess = Process
        customProgressDialog = CustomProgressDialog(this, false,"")
//        customProgressDialogMsg = CustomProgressDialog(this, false,"")

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
//            if (android.os.Process.isIsolated()) {
//
//            } else {
//                TODO("VERSION.SDK_INT < P")
//            }
//        }

    }

    override fun onStop() {
        super.onStop()
        if(customProgressDialog.isShowing) {
            customProgressDialog.Hide()
        }
    }

    fun checkPermissionCamera(activity: Activity): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val permissions = arrayOf(
                    Manifest.permission.CAMERA
            )
            val deniedPermissions = ArrayList<String>()
            for (perm in permissions) {
                /*
                    1. 현재 권한이 있는지 없는지 체크
                 */
                if (ActivityCompat.checkSelfPermission(activity, perm ) != PackageManager.PERMISSION_GRANTED) {
                    deniedPermissions.add(perm)
                }else{
                    return true
                }
            }
            /*
                  2. 권한 추가 여부 판단
             */
            if (deniedPermissions.size > 0) {
                val deniedPerms = deniedPermissions.toTypedArray()
                ActivityCompat.requestPermissions(activity, deniedPerms, 53874)
                Logcat.d("퍼미션 요청 정상")
                return false
            }
        }
        return true
    }
    fun checkPermissionHard(activity: Activity): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val permissions = arrayOf(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
            val deniedPermissions = ArrayList<String>()
            for (perm in permissions) {
                /*
                    1. 현재 권한이 있는지 없는지 체크
                 */
                if (ActivityCompat.checkSelfPermission(activity, perm ) != PackageManager.PERMISSION_GRANTED) {
                    deniedPermissions.add(perm)
                }else{
                    return true
                }
            }

            /*
                2. 권한 추가 여부 판단
           */
            if (deniedPermissions.size > 0) {
                val deniedPerms = deniedPermissions.toTypedArray()
                ActivityCompat.requestPermissions(activity, deniedPerms, 53874)
                Logcat.d("퍼미션 요청 정상")
                return true
            }else{
                Logcat.d("퍼미션 요청 거부")
                return false
            }
        }
        return true
    }

}
