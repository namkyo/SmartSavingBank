package kr.co.smartbank.app.custom

import android.content.Context
import android.view.WindowManager
import android.webkit.JsResult
import android.webkit.WebChromeClient
import android.webkit.WebView
import androidx.appcompat.app.AlertDialog
import kr.co.smartbank.app.util.Logcat

class CustomWebChromeClient(var context: Context) : WebChromeClient() {

    override fun onReceivedTitle(view: WebView, sTitle: String) {
        super.onReceivedTitle(view, sTitle)
        //        if (textTitle != null) {
        //            if (sTitle != null && sTitle.length() > 0) {
        //
        //                textTitle.setText(sTitle);
        //            } else {
        //                textTitle.setText("하나저축은행");
        //            }
        //        }

    }

    override fun onJsAlert(view: WebView, url: String, message: String, result: JsResult): Boolean {


        try {
            val builder = AlertDialog.Builder(context)
            //builder.setTitle("AlertDialog 제목");
            builder.setMessage(message)
            builder.setCancelable(false)
            builder.setPositiveButton(
                "확인"
            ) { dialog, which -> result.confirm() }
            builder.show()
        } catch (e: WindowManager.BadTokenException) {
            Logcat.e("에러입니다")
        } catch (e: Exception) {
            Logcat.e("에러입니다")
        }


        return true
    }

    //확인창
    override fun onJsConfirm(view: WebView, url: String, message: String, result: JsResult): Boolean {
        try {
            val builder = AlertDialog.Builder(context)
            //builder.setTitle("AlertDialog 제목");
            builder.setMessage(message)
            builder.setCancelable(false)
            builder.setPositiveButton(
                "확인"
            ) { dialog, which -> result.confirm() }
            builder.setNegativeButton(
                "취소"
            ) { dialog, which -> result.cancel() }
            builder.show()

        } catch (e: WindowManager.BadTokenException) {
            Logcat.e("에러입니다")
        } catch (e: Exception) {
            Logcat.e("에러입니다")
        }



        return true
    }

    override fun onProgressChanged(view: WebView, progress: Int) {
        super.onProgressChanged(view, progress)
    }

}