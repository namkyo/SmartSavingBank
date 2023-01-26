package kr.co.smartbank.app.util

import android.webkit.WebView
import org.json.JSONObject

class DataSend {
    companion object{
        private var instance : DataSend? = null
        fun getInstance(): DataSend {
            return instance ?: synchronized(this){
                instance ?: DataSend().also {
                    instance = it
                }
            }
        }
    }

    fun webViewSend(resultCd:String,param:JSONObject,callbackName:String,webView: WebView){
        val resultData=JSONObject()
        resultData.put("resultCd",resultCd)
        resultData.put("params",param)
        val strJavaScript = "$callbackName($resultData)"
        Logcat.d("susscessSend : $strJavaScript")
        //System.out.println("susscessSend : "+strJavaScript)
        webView.loadUrl("javascript:$strJavaScript")
    }
    fun webViewSend2(resultCd:String,param:JSONObject,callbackName:String,webView: WebView){
        val resultData=JSONObject()
        resultData.put("resultCd",resultCd)
        resultData.put("params","$param")
        val strJavaScript = "$callbackName($resultData)"
        Logcat.d("susscessSend : $strJavaScript")
        //System.out.println("susscessSend : "+strJavaScript)
        webView.loadUrl("javascript:$strJavaScript")
    }

    fun webViewSend(resultCd:String,callbackName:String,webView: WebView){
        val resultData=JSONObject()
        resultData.put("resultCd",resultCd)
        val strJavaScript = "$callbackName($resultData)"
        Logcat.d("susscessSend : $strJavaScript")
        webView.loadUrl("javascript:$strJavaScript")
    }
    fun webViewSend(resultCd:String,param:JSONObject,callbackName:String){
        val resultData=JSONObject()
        resultData.put("resultCd",resultCd)
        resultData.put("params",param)
        val strJavaScript = "$callbackName($resultData)"
        Logcat.d("susscessSend : $strJavaScript")
        //MainActivity().getWebView().loadUrl("javascript:$strJavaScript")
    }
    fun webViewSend(resultCd:String,callbackName:String){
        val resultData=JSONObject()
        resultData.put("resultCd",resultCd)
        val strJavaScript = "$callbackName($resultData)"
        Logcat.d("susscessSend : $strJavaScript")
        //MainActivity().getWebView().loadUrl("javascript:$strJavaScript")
    }
}