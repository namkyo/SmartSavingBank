package kr.co.smartbank.app.util

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Build
import android.webkit.WebView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.finishAffinity
import kr.co.smartbank.app.config.Constants


class BackPressCloseHandler(private val activity: Activity,private val webView: WebView){
    private var backKeyPressedTime: Long = 0;
    private var toast: Toast?=null
    fun onBackPressed(){
        if (Constants.BACK_BTN_YN){
//            val alert_ex: AlertDialog.Builder = AlertDialog.Builder(activity)
//            alert_ex.setMessage("정말로 종료하시겠습니까?")
//            alert_ex.setPositiveButton("취소", { dialog, which -> })
//            alert_ex.setNegativeButton("종료", { dialog, which -> activity.finishAffinity() })
//            alert_ex.setTitle("스마트저축은행 어플 종료안내!")
//            val alert: AlertDialog = alert_ex.cr
            webView.loadUrl("javascript:logoutExec();")
        }else{
            if(System.currentTimeMillis()>backKeyPressedTime+2000){
                backKeyPressedTime=System.currentTimeMillis()
                showGuide()
                return
            }

            if(System.currentTimeMillis()<=backKeyPressedTime+2000){
                toast!!.cancel()

                if(Build.VERSION.SDK_INT >= 16){
                    // API 16+
                    activity.finishAffinity();
                } else {
                    // API 16 미만
                    ActivityCompat.finishAffinity(activity);
                }
                System.exit(0);
            }
        }
    }
    private fun showGuide(){
        toast=Toast.makeText(activity, "\'뒤로\'버튼을 한번 더 누르시면 종료됩니다", Toast.LENGTH_SHORT)
        toast!!.show()
    }
    fun onBackPressedHome(webView: WebView,iniUrl:String){
        if (Constants.BACK_BTN_YN){
//            val alert_ex: AlertDialog.Builder = AlertDialog.Builder(activity)
//            alert_ex.setMessage("메인으로 이동 하시겠습니까?")
//            alert_ex.setPositiveButton("취소", { dialog, which -> })
//            //alert_ex.setNegativeButton("이동", { dialog, which -> webView.loadUrl(iniUrl) })
//            alert_ex.setNegativeButton("이동", { dialog, which ->  webView.loadUrl("javascript:goMainPage()") })
//            alert_ex.setTitle("스마트저축은행 메인 이동 안내!")
//            val alert: AlertDialog = alert_ex.create()
//            alert.show()
            val mainGoScript =
                    "confirmView(\"메인으로 이동 하시겠습니까?\", " +
                            "         function(){   " +
                            "           goMainPage(); "+
                            "         }, function(){});"
            Logcat.d("mainGoScript : $mainGoScript")
            webView.loadUrl("javascript:"+mainGoScript)
        }else{
            if(System.currentTimeMillis()>backKeyPressedTime+2000){
                backKeyPressedTime=System.currentTimeMillis()
                showGuide()
                return
            }

            if(System.currentTimeMillis()<=backKeyPressedTime+2000){
                toast!!.cancel()

                if(Build.VERSION.SDK_INT >= 16){
                    // API 16+
                    activity.finishAffinity();
                } else {
                    // API 16 미만
                    ActivityCompat.finishAffinity(activity);
                }
                System.exit(0);
            }
        }
    }
}