package kr.co.smartbank.app.util

import android.app.Activity
import kr.co.smartbank.app.custom.CustomProgressDialog

object LoadingUtil {
    fun show(customProgressDialog: CustomProgressDialog){
        try{
            customProgressDialog.Show()
        }catch (e :java.lang.Exception){
            Logcat.d("로딩바 오류")
        }
    }
    fun showAndText(customProgressDialog: CustomProgressDialog, text: String, activity: Activity): CustomProgressDialog {
        try{
            Logcat.d("문자로딩 : $text")
            activity.runOnUiThread{
                customProgressDialog.Hide()
                val customProgressDialog2 = CustomProgressDialog(activity, false,text)
                customProgressDialog2.Show()
            }
        }catch (e :java.lang.Exception){
            Logcat.d("로딩바 오류")
        }
        return customProgressDialog
    }
    fun hide(customProgressDialog: CustomProgressDialog){
        try{
            customProgressDialog.Hide()
        }catch (e :java.lang.Exception){
            Logcat.d("로딩바 오류")
        }
    }
}