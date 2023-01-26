package kr.co.smartbank.app.custom

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.View
import android.widget.TextView
import kotlinx.android.synthetic.main.dialog_progress.*
import kr.co.smartbank.app.R
import kr.co.smartbank.app.util.Logcat


/**
 * Created by Administrator on 2016-02-04.
 */
class CustomProgressDialog(private val mCtx: Context, cancelable: Boolean,text:String) : Dialog(mCtx) {
    private val progressDialog: Dialog?
    init {
        progressDialog = Dialog(mCtx, R.style.AppDialog)
        progressDialog.setContentView(R.layout.dialog_progress)
        progressDialog.setCancelable(cancelable)
        progressDialog.loadingTxt.gravity=Gravity.CENTER
        progressDialog.loadingTxt.text=text

        //progressDialog.addContentView()
        // progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        // progressDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);

    }

    //    fun ShowWithMessage(message: String) {
//        (mCtx as Activity).runOnUiThread {
//            if (progressDialog != null) {
//
//                (progressDialog.findViewById<View>(R.id.txvPrgMessage) as TextView).visibility =
//                    View.VISIBLE
//                (progressDialog.findViewById<View>(R.id.txvPrgMessage) as TextView).text = message
//
//                try {
//                    if (!progressDialog.isShowing) {
//                        progressDialog.show()
//                    }
//                } catch (e: RuntimeException) {
//                    Logcat.e("에러입니다")
//                } catch (e: Exception) {
//                    Logcat.e("에러입니다")
//                }
//
//            }
//        }
//    }
    fun Show() {
        (mCtx as Activity).runOnUiThread {
            if (progressDialog != null) {
                (progressDialog.findViewById<View>(R.id.txvPrgMessage) as TextView).visibility =
                        View.GONE
                try {
                    if (!progressDialog.isShowing) {
                        progressDialog.show()
                    }
                } catch (e: RuntimeException) {
                    Logcat.e("에러입니다")
                } catch (e: Exception) {
                    Logcat.e("에러입니다")
                }

            }
        }
    }
    fun Show(msg:String) {
        (mCtx as Activity).runOnUiThread {
            if (progressDialog != null) {
                (progressDialog.findViewById<View>(R.id.txvPrgMessage) as TextView).visibility = View.GONE
                try {
                    if (!progressDialog.isShowing) {
//                        if (msg != "") {
//                            Toast.makeText(mCtx, msg, Toast.LENGTH_SHORT).show()
//                        }
                        progressDialog.findViewById<TextView>(R.id.loadingTxt).text = msg
                        progressDialog.show()
                    }else{
                        progressDialog.hide()
                        progressDialog.findViewById<TextView>(R.id.loadingTxt).text = msg
                        Handler(Looper.myLooper()!!).postDelayed({
                            progressDialog.show()
                        }, 100)
                    }
                } catch (e: RuntimeException) {
                    Logcat.e("에러입니다")
                } catch (e: Exception) {
                    Logcat.e("에러입니다")
                }
            }
        }
    }

    fun Hide() {
        (mCtx as Activity).runOnUiThread {
            if (progressDialog != null) {
                try {
                    if (progressDialog.isShowing) {
                        Handler(Looper.myLooper()!!).postDelayed({
                            progressDialog.loadingTxt.text = ""
                            progressDialog.dismiss()
                        }, 100)
                    }
                } catch (e: IllegalArgumentException) {
                    Logcat.e("에러입니다")
                } catch (e: RuntimeException) {
                    Logcat.e("에러입니다")
                } catch (e: Exception) {
                    Logcat.e("에러입니다")
                }

            }
        }
    }
}