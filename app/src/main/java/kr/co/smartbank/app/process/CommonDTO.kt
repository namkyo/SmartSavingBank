package kr.co.smartbank.app.process

import android.app.Activity
import android.webkit.WebView
import androidx.fragment.app.FragmentManager
import com.lumensoft.ks.KSCertificate
import kr.co.coocon.sasapi.SASManager
import kr.co.everspin.eversafe.EncryptionContext
import kr.co.smartbank.app.custom.CustomProgressDialog
import kr.co.smartbank.app.util.SharedPreferenceHelper
import java.io.File


data class CommonDTO(
        val activity: Activity, val supportFragmentManager: FragmentManager, var sp: SharedPreferenceHelper
){
    lateinit var userCert : KSCertificate
    lateinit var webView: WebView
    lateinit var customProgressDialog: CustomProgressDialog
    lateinit var parmas : String
    lateinit var succFunc : String
    lateinit var failFunc : String
//    lateinit var authMesg :String
//    lateinit var randomKey :String
    lateinit var singData :String
    var sasManager: SASManager? = null
    lateinit var cameraFile :File
//    lateinit var encryptionContext: EncryptionContext
    lateinit var rbrno :String

}