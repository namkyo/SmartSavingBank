package kr.co.smartbank.app.view

import android.Manifest
import android.app.Activity
import android.app.DatePickerDialog
import android.app.DownloadManager
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.Intent.ACTION_PICK
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Bitmap
import android.net.Uri
import android.os.*
import android.provider.MediaStore
import android.view.View
import android.view.WindowManager
import android.webkit.*
import android.webkit.WebView.WebViewTransport
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.google.android.gms.ads.identifier.AdvertisingIdClient
import com.google.android.gms.auth.GooglePlayServicesAvailabilityException
import com.google.android.gms.common.GooglePlayServicesNotAvailableException
import com.google.firebase.iid.FirebaseInstanceId
import eightbyte.safetoken.SafetokenSimpleClient
import kr.co.coocon.sasapi.SASManager
import kr.co.coocon.sasapi.SASRunCompletedListener
import kr.co.coocon.sasapi.SASRunStatusChangedListener
import kr.co.everspin.eversafe.EversafeHelper
import kr.co.smartbank.app.R
import kr.co.smartbank.app.config.Constants
import kr.co.smartbank.app.custom.CustomProgressDialog
import kr.co.smartbank.app.process.*
import kr.co.smartbank.app.service.NativeBridge
import kr.co.smartbank.app.solution.everspin.everSafe.EverSafeManager
import kr.co.smartbank.app.solution.everspin.secureKeypad.CustomView
import kr.co.smartbank.app.solution.everspin.secureKeypad.QwertyView
import kr.co.smartbank.app.solution.face.view.OcrMainActivity
import kr.co.smartbank.app.solution.ksw.view.KSW_Activity_CertList
import kr.co.smartbank.app.solution.ksw.view.KSW_Activity_ICRSImportCert
import kr.co.smartbank.app.util.*
import kr.co.smartbank.app.view.push.PushHistList
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.io.IOException
import java.net.URISyntaxException
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : BaseActivity(), SASRunCompletedListener, SASRunStatusChangedListener {
    private lateinit var activity: Activity
    private lateinit var sp: SharedPreferenceHelper
    private lateinit var webView:WebView
    private var mFilePathCallback: ValueCallback<Array<Uri>>? = null
    private var cameraImageUri: Uri? = null
    private var cameraFile:File? = null

    //???????????? ???????????? ??????
    private var priSasManager: SASManager? = null
    //???????????? ???????????? ??????
    private var finSasManager: SASManager? = null

    private var loadUrl=""

    private var backPressCloseHandler: BackPressCloseHandler? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        webView     = findViewById(R.id.webView)
        activity    = this
        sp          = SharedPreferenceHelper(activity)

        backPressCloseHandler = BackPressCloseHandler(activity, webView)

        FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener(activity as MainActivity) { instanceIdResult ->
            val token = instanceIdResult.token
            sp.put(SharedPreferenceHelper.PUSH_KEY, token)
        }

        setWebView(webView)
        webView.addJavascriptInterface(NativeBridge(activity), "ftBridge")

        intent.getStringExtra("loadURl").let {
            loadUrl=it.toString()
            Logcat.d("url : $loadUrl")
            webView.loadUrl(loadUrl)
        }
//        val intent = Intent(activity, OcrMainActivity::class.java).apply {
//            putExtra(resources.getString(R.string.ocr_take_mode),true)
//            putExtra(resources.getString(R.string.isMasking), true)
//            putExtra(resources.getString(R.string.isDetail), true)
//            putExtra(resources.getString(R.string.isSerialNum), true)
//        }
//        startActivityForResult(intent, Constants.OCR_Activity_01)

        //????????? ?????? ??????
        if(Constants.EVER_SPIN_YN){
            EverSafeManager.run(activity as MainActivity)
        }
        Logcat.e("??????????????? ????????????2 : ${EversafeHelper.getInstance().status}")
    }

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        super.onSaveInstanceState(outState, outPersistentState)
        Logcat.d("????????????")
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        Logcat.d("????????????2")
    }
    override fun onBackPressed() {

        try{
            val now=webView.url
            Logcat.d("??????url = $now")
            //Logcat.d("mainYN = ${now.indexOf("main")}")

            if (now != null) {
                if (now.indexOf("main.view")>0){
                    backPressCloseHandler!!.onBackPressed()
                }else{
                    backPressCloseHandler!!.onBackPressedHome(webView, loadUrl)
        //                if (webView.canGoBack()) run {
        //                    //            webView.goBack()
        //                    runOnUiThread {
        //                        webView.loadUrl("javascript:history.back()")
        //                    }
        //                } else {
        //                    super.onBackPressed()
        //                }
                }
            }
        }catch (e: NullPointerException) {
            Logcat.e("??????????????? [${e.message}]")
            super.onBackPressed()
        }

    }

    private fun setWebView(webView: WebView) {

        val ws = webView.settings
        //????????? ????????? ????????? ?????? ??????????????? ????????????.
        ws.apply {
            javaScriptEnabled = true //????????????(true)
            domStorageEnabled = true //????????????(true)
            javaScriptCanOpenWindowsAutomatically = true //????????????(true)


            cacheMode = WebSettings.LOAD_NO_CACHE
            loadsImagesAutomatically = true
            builtInZoomControls = false
            setSupportZoom(true)
            setSupportMultipleWindows(true)
            loadWithOverviewMode = true
            blockNetworkImage = false
            useWideViewPort = true
            textZoom = 100
            mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        }

        webView.setLayerType(View.LAYER_TYPE_HARDWARE, null)
        webView.isScrollbarFadingEnabled = true
        webView.isVerticalScrollBarEnabled = false
        webView.isHorizontalScrollBarEnabled = false
        webView.setNetworkAvailable(true)
        webView.webChromeClient = object : WebChromeClient() {

            //window.open ???
            override fun onCreateWindow(view: WebView?, isDialog: Boolean, isUserGesture: Boolean, resultMsg: Message?): Boolean {
                val newWebView = WebView(activity)
                val transport = resultMsg!!.obj as WebViewTransport
                transport.webView = newWebView
                resultMsg.sendToTarget()
                newWebView.webViewClient = object : WebViewClient() {
                    override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
                        val browserIntent = Intent(Intent.ACTION_VIEW, request.url)
                        startActivity(browserIntent)
                        return true
                    }

                    override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                        startActivity(browserIntent)
                        return true
                    }
                }
                return true
            }

            override fun onConsoleMessage(consoleMessage: ConsoleMessage?): Boolean {
                if (Constants.WEB_LOG_YN){
                    if (consoleMessage != null) {
                        Logcat.d("===============???????????? start================")
                        Logcat.d(consoleMessage.message() + "\n" + consoleMessage.messageLevel() + "\n" + consoleMessage.sourceId())
                        Logcat.d("${consoleMessage.lineNumber()}")
                        Logcat.d("===============???????????? end================")
                    }
                }
                return true
            }

            override fun onShowFileChooser(
                    webView: WebView?,
                    filePathCallback: ValueCallback<Array<Uri>>?,
                    fileChooserParams: FileChooserParams?
            ): Boolean {
                if(mFilePathCallback!=null){
                   // mFilePathCallback!!.onReceiveValue(null)
                    mFilePathCallback = null
                }

                mFilePathCallback = filePathCallback

                val path=filesDir


                val sdf = SimpleDateFormat("yyyyMMddhhmmss")
                val c1 = Calendar.getInstance()
                val fileName = sdf.format(c1.time)+".jpg"
                val file = File(path, fileName)

                if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.N){
                    val strpa=applicationContext.packageName
                    cameraImageUri = FileProvider.getUriForFile(applicationContext, strpa + ".fileprovider", file)
                }else{
                    cameraImageUri = Uri.fromFile(file);
                }


                val intentCamera = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                val pickIntent = Intent(ACTION_PICK) //?????????
                //pickIntent.setType(MediaStore.Images.Media.CONTENT_TYPE); //??????
                pickIntent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                val pickTitle = "?????? ????????? ????????? ???????????????."
                val chooserIntent = Intent.createChooser(pickIntent, pickTitle)

                // ????????? intent ???????????????..
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf<Parcelable>(intentCamera))
                startActivityForResult(chooserIntent, Constants.REQUEST_FILE_CHOOSE)
                //activity.//overridePendingTransition(R.anim.slide_up, R.anim.slide_bottom)
                return true
            }

            override fun onJsAlert(view: WebView, url: String, message: String, result: JsResult): Boolean {
                try {
                    val builder = AlertDialog.Builder(this@MainActivity)
                    //builder.setTitle("AlertDialog ??????");
                    builder.setMessage(message)
                    builder.setCancelable(false)
                    builder.setPositiveButton("??????") { dialog, which -> result.confirm() }
                    builder.show()
                } catch (e: WindowManager.BadTokenException) {
                    Logcat.e("onJsAlert:Token??????")
                } catch (e: Exception) {
                    Logcat.e("onJsAlert:??????")
                }

                return true
            }

            //?????????
            override fun onJsConfirm(view: WebView, url: String, message: String, result: JsResult): Boolean {
                try {
                    val builder = AlertDialog.Builder(this@MainActivity)
                    //builder.setTitle("AlertDialog ??????");
                    builder.setMessage(message)
                    builder.setCancelable(false)
                    builder.setPositiveButton("??????") { dialog, which -> result.confirm() }
                    builder.setNegativeButton("??????") { dialog, which -> result.cancel() }
                    builder.show()
                } catch (e: WindowManager.BadTokenException) {
                    Logcat.e("onJsConfirm:Token??????")
                } catch (e: Exception) {
                    Logcat.e("onJsConfirm:??????")
                }

                return true
            }
        }
        webView.webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {
                 customProgressDialog.Show()
            }
            override fun onPageFinished(view: WebView, url: String) {
                 customProgressDialog.Hide()
            }

            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                //?????? ??? ??????????????? ?????????(????????? ?????????)??? ??????????????? intent:// URI??? ????????? ??????????????? ?????????.
                //?????? ????????? ?????? ????????????.
                if (url.startsWith("intent://")) {
                    var intent: Intent? = null
                    try {
                        intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME)
                        if (intent != null) {
                            //?????????
                            startActivity(intent)
                        }
                    } catch (e: URISyntaxException) {
                        //URI ?????? ?????? ??? ?????? ??????

                    } catch (e: ActivityNotFoundException) {
                        val packageName = intent!!.getPackage()
                        if (packageName != "") {
                            // ?????? ???????????? ?????? ?????? ?????? ???????????? ??????
                            startActivity(
                                    Intent(
                                            Intent.ACTION_VIEW,
                                            Uri.parse("market://details?id=" + packageName!!)
                                    )

                            )
                        }
                    }

                    //return  ?????? ????????? true??? ?????? ?????????.
                    return true

                } else if (url.startsWith("https://play.google.com/store/apps/details?id=") || url.startsWith(
                                "market://details?id="
                        )) {
                    //????????? ??? ??????????????? ?????? ?????? ??? PlayStore ????????? ???????????? ?????? ??????
                    val uri = Uri.parse(url)
                    val packageName = uri.getQueryParameter("id")
                    if (packageName != null && packageName != "") {
                        // ???????????? ??????
                        startActivity(
                                Intent(
                                        Intent.ACTION_VIEW,
                                        Uri.parse("market://details?id=$packageName")
                                )
                        )
                    }
                    //return  ?????? ????????? true??? ?????? ?????????.
                    return true
                } else if (url.startsWith("tel:")) {
                    val intent = Intent(Intent.ACTION_DIAL, Uri.parse(url))
                    try {
                        startActivity(intent)
                        //overridePendingTransition(R.anim.slide_up, R.anim.slide_bottom)
                        return true
                    } catch (e: SecurityException) {
                        Logcat.e("??????????????? [${e.message}]")
                    }
                }

                //return  ?????? ????????? false??? ?????? ?????????.
                return false
            }

            override fun shouldInterceptRequest(view: WebView?, request: WebResourceRequest?): WebResourceResponse? {
                return super.shouldInterceptRequest(view, request)
            }


            //            override fun onReceivedSslError(
            //                view: WebView,
            //                handler: SslErrorHandler,
            //                error: SslError
            //            ) {
            //                handler.proceed() // SSL ????????? ???????????? ?????? ??????
            //            }
        }

        webView.setDownloadListener { url, userAgent, contentDisposition, mimetype, contentLength ->
            Logcat.d("setDownloadListener url : $url")
            Logcat.d("================setDownloadListener=================")
            Logcat.d("url = $url")
            Logcat.d("userAgent = $userAgent")
            Logcat.d("contentDisposition = $contentDisposition")
            Logcat.d("mimetype = $mimetype")
            Logcat.d("contentLength = $contentLength")
            try {
               // val request = DownloadManager.Request(Uri.parse(url.replace("blob:","")))
                val request = DownloadManager.Request(Uri.parse(url))
                val dm = getSystemService(DOWNLOAD_SERVICE) as DownloadManager

                //contentDisposition = URLDecoder.decode(contentDisposition, "UTF-8") //?????????
                //val FileName = contentDisposition.replace("attachment; filename=", "") //attachment; filename*=UTF-8''?????? ????????????????????? ???????????? ?????????????????? ?????? attachment; filename*=UTF-8''??????
                val FileName = "AgreeListZip.zip"
                val cookies = CookieManager.getInstance().getCookie(url)
                request.addRequestHeader("cookie", cookies)
                //request.setMimeType("application/octet-stream")
                request.setMimeType(mimetype)
                request.addRequestHeader("User-Agent", userAgent)
                request.setDescription("Downloading File")
                request.setAllowedOverMetered(true)
                request.setAllowedOverRoaming(true)
                request.setTitle(FileName)
                //request.setRequiresCharging(false)
                request.allowScanningByMediaScanner()
                request.setAllowedOverMetered(true)
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, FileName)
                dm.enqueue(request)
                Toast.makeText(applicationContext, "????????? ?????????????????????.", Toast.LENGTH_LONG).show()
            } catch (e: java.lang.Exception) {
                if (ContextCompat.checkSelfPermission(this@MainActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this@MainActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        Toast.makeText(baseContext, "??????????????? ??????\n????????? ???????????????.", Toast.LENGTH_LONG).show()
                        ActivityCompat.requestPermissions(this@MainActivity, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 1004)
                    } else {
                        Toast.makeText(baseContext, "??????????????? ??????\n????????? ???????????????.", Toast.LENGTH_LONG).show()
                        ActivityCompat.requestPermissions(this@MainActivity, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 1004)
                    }
                }
            }
        }
    }

    /**
     * ??????
     */
    fun authorization(params: String, succFunc: String, failFunc: String) {
        val common = CommonDTO(activity, supportFragmentManager, sp)
        common.webView=this.webView
        common.succFunc=succFunc
        common.failFunc=failFunc
        sp.put(Constants.SUCC_FUNC, succFunc)
        sp.put(Constants.FAIL_FUNC, failFunc)
        Authorization().authorization(params, common)
    }

    /**
     * ????????????
     */
    fun loading(params: String) {
        try {
            val jsonObject = JSONObject(params)
            val flag = jsonObject.optString("FLAG", "")
            val msg = jsonObject.optString("MSG", "")
            Logcat.d("flag: $flag")
            when (flag) {
                "ON" -> {
                    if (msg != "") {
                        customProgressDialog.Show(msg)
                    }else{
                        customProgressDialog.Show()
                    }
                }
                "OFF" -> {
                    customProgressDialog.Hide()
                }
            }
        } catch (e: JSONException) {
            println("????????? ?????????????????????.")
        }
    }

    /**
     * ??????
     */
    fun calendar(params: String, succFunc: String, failFunc: String) {
        sp.put(Constants.SUCC_FUNC, succFunc)
        sp.put(Constants.FAIL_FUNC, failFunc)
        val cal = Calendar.getInstance()

        // DatePickerDialog
        DatePickerDialog(
                activity, dateSetListener,
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    //?????? ??????
    private val dateSetListener =
            DatePickerDialog.OnDateSetListener { view, year, month, day ->
                val one = 1
                val callbackName=sp.getValue(Constants.SUCC_FUNC, "")
                val resultCd="0000"
                val resultParam= JSONObject()
                resultParam.put(
                        "resData",
                        year.toString() + String.format("%02d", (month + one)) + String.format(
                                "%02d",
                                day
                        )
                )
                DataSend.getInstance().webViewSend(resultCd, resultParam, callbackName!!, webView)
            }


    //?????? ????????? ??????
    fun signCert(params: String, succFunc: String, failFunc: String) {
        sp.put(Constants.SUCC_FUNC, succFunc)
        sp.put(Constants.FAIL_FUNC, failFunc)

        try{
            val intent = Intent(activity, KSW_Activity_CertList::class.java)
            intent.putExtra("signData", JSONObject(params).get("signData").toString())
            intent.putExtra("rbrno", JSONObject(params).get("rbrno").toString())
            intent.putExtra(Constants.KSW_Activity, Constants.KSW_Activity_CertSign)
            startActivityForResult(intent, Constants.KSW_Cert_Activity_01)
            //overridePendingTransition(R.anim.slide_up, R.anim.slide_bottom)
        }catch (e: Exception){
            Toast.makeText(activity, "??????????????????", Toast.LENGTH_SHORT).show()
            val resultCd = "9991"
            val resultParam=JSONObject()
            DataSend.getInstance().webViewSend(resultCd, resultParam, succFunc, webView)
        }
    }
    //?????? ????????? ??????
    fun certMng(params: String, succFunc: String, failFunc: String) {
        //?????? ??????
        if(checkPermissionHard(activity)){
            sp.put(Constants.SUCC_FUNC, succFunc)
            sp.put(Constants.FAIL_FUNC, failFunc)
            val intent = Intent(activity, KSW_Activity_CertList::class.java)
            intent.putExtra(Constants.KSW_Activity, Constants.KSW_Activity_CertList)
            startActivity(intent)
            //overridePendingTransition(R.anim.slide_up, R.anim.slide_bottom)
        }else{
            val resultCd = "9991"
            val resultParam=JSONObject()
            DataSend.getInstance().webViewSend(resultCd, resultParam, succFunc, webView)
        }

    }

    //???????????? ???????????? ?????? ????????? ??????
    fun scraping(params: String, succFunc: String, failFunc: String) {

        if(checkPermissionHard(activity)){
            sp.put(Constants.SUCC_FUNC, succFunc)
            sp.put(Constants.FAIL_FUNC, failFunc)
            val intent = Intent(activity, KSW_Activity_CertList::class.java).apply {
                putExtra("params", params)
                putExtra(Constants.KSW_Activity, Constants.KSW_Activity_SCRAPING)
            }
            startActivityForResult(intent, Constants.KSW_Activity_01)
            //overridePendingTransition(R.anim.slide_up, R.anim.slide_bottom)
        }else{
            val resultCd = "9991"
            val resultParam=JSONObject()
            DataSend.getInstance().webViewSend(resultCd, resultParam, succFunc, webView)
        }
    }

    //?????? ????????? ????????????
    fun certImport(params: String, succFunc: String, failFunc: String) {
        if(checkPermissionHard(activity)){
            sp.put(Constants.SUCC_FUNC, succFunc)
            sp.put(Constants.FAIL_FUNC, failFunc)
            val intent = Intent(activity, KSW_Activity_ICRSImportCert::class.java)
            intent.putExtra(Constants.KSW_Activity, Constants.KSW_Activity_IMPORT)
            startActivity(intent)
            //overridePendingTransition(R.anim.slide_up, R.anim.slide_bottom)
        }else{
            val resultCd = "9991"
            val resultParam=JSONObject()
            DataSend.getInstance().webViewSend(resultCd, resultParam, succFunc, webView)
        }
    }
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
                "JPEG_${timeStamp}_", /* prefix */
                ".jpg", /* suffix */
                storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            //currentPhotoPath = absolutePath
        }
    }
    //??????????????? ??????
    fun camera(params: String, succFunc: String, failFunc: String) {

        sp.put(Constants.SUCC_FUNC, succFunc)
        sp.put(Constants.FAIL_FUNC, failFunc)

        val strpa=applicationContext.packageName
        val sdcard: File = Environment.getExternalStorageDirectory()
        //val uri: Uri = FileProvider.getUriForFile(applicationContext, strpa + ".fileprovider", sdcard)
        cameraFile = File(sdcard, "capture.jpg")

        cameraFile =
                try {
                    createImageFile()
                } catch (ex: IOException) {
                    Logcat.d("???????????? ??????????????? ????????????")
                    null
                }
        cameraFile?.also {
            val photoURI: Uri = FileProvider.getUriForFile(
                    applicationContext, applicationContext.packageName + ".fileprovider", it
            )
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            startActivityForResult(intent, Constants.Native_Camera)
            //overridePendingTransition(R.anim.slide_up, R.anim.slide_bottom)

            //???????????? ?????? ??????????????? ???????????? ??????
            //overridePendingTransition(R.anim.slide_up, R.anim.slide_bottom);
        }

//        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
//            putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(cameraFile))
//            //putExtra(MediaStore.EXTRA_OUTPUT, uri)
//        }
//        startActivityForResult(intent, Constants.Native_Camera)

    }
    //ocr ??????
    fun ocr(params: String, succFunc: String, failFunc: String) {
        if(checkPermissionCamera(activity)){
            sp.put(Constants.SUCC_FUNC, succFunc)
            sp.put(Constants.FAIL_FUNC, failFunc)
            LoadingUtil.show(customProgressDialog)
            val intent = Intent(activity, OcrMainActivity::class.java).apply {
                val jsonObject = JSONObject(params)
                Logcat.d("ocr : input = $jsonObject")

//                putExtra(resources.getString(R.string.ocr_take_mode), jsonObject.optString(resources.getString(R.string.ocr_take_mode), "false").toBoolean())
                putExtra(resources.getString(R.string.ocr_take_mode), true)
                putExtra(resources.getString(R.string.isMasking), jsonObject.optString(resources.getString(R.string.isMasking), "false").toBoolean())
                putExtra(resources.getString(R.string.isDetail), jsonObject.optString(resources.getString(R.string.isDetail), "false").toBoolean())
                putExtra(resources.getString(R.string.isSerialNum), jsonObject.optString(resources.getString(R.string.isSerialNum), "false").toBoolean())
            }
            startActivityForResult(intent, Constants.OCR_Activity_01)
            //overridePendingTransition(R.anim.slide_up, R.anim.slide_bottom)
        }else{
            val resultCd = "9991"
            val resultParam=JSONObject()
            DataSend.getInstance().webViewSend(resultCd, resultParam, succFunc, webView)
        }
    }

    //appData ??????
    fun appData(params: String, succFunc: String, failFunc: String) {
        var common = CommonDTO(activity, supportFragmentManager, sp)
        try {
            val jsonObject = JSONObject(params)
            val gubun = jsonObject.optString("gubun", "")
            val reqData = jsonObject.optString("reqData", "")
            when (gubun) {
                "C" -> { // ??????
                    val jsonReqData = JSONObject(reqData)
                    AppData().insertAppdata(jsonReqData, common)

                    val resultCd = "0000"
                    val callbackName = succFunc
                    DataSend.getInstance().webViewSend(resultCd, callbackName!!, webView)

                }
                "R" -> { // ??????
                    val keys = reqData.replace(" ", "").split(",")
                    Logcat.d("appdata key : " + keys)
                    //???????????????
                    val keysSize = keys.size;
                    Logcat.d("appdata keysSize : " + keysSize)
                    var appData: String
                    var resultData: JSONObject = JSONObject()

                    //??????
                    if (keysSize > 1) {
                        appData = AppData().selectAppdatas(keys, common)
                        resultData = JSONObject(appData)
                    }
                    //??????
                    else {
                        appData = AppData().selectAppdata(reqData, common)
                        resultData.put(reqData, appData)

                    }
                    val callbackName = succFunc
                    val resultCd = "0000"
                    DataSend.getInstance().webViewSend(resultCd, resultData, callbackName!!, webView)
                }
            }
        } catch (e: JSONException) {
            Logcat.d("???????????? ??????")
            val resultCd="9999"
            DataSend.getInstance().webViewSend(resultCd, succFunc, webView)
        }
    }

    //appLink ??????
    fun appLink(params: String) {
        try {
            val jsonObject = JSONObject(params)
            val uri = jsonObject.optString("uri", "")
            val appId = jsonObject.optString("appId", "")
            try {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
                intent.data = Uri.parse(uri)
                startActivity(intent)
                //overridePendingTransition(R.anim.slide_up, R.anim.slide_bottom)
            } catch (e: Exception) {
                try {
                    val builder = AlertDialog.Builder(activity as MainActivity)
                    builder.setMessage("?????? ????????? ?????? ????????????????????? ????????? ???????????????. ??? ????????? ?????????????????? ??????????????? ????????? ??????????????? ??????????????? ??????????????????.")
                    builder.setCancelable(false)
                    builder.setPositiveButton("??????") { dialog, which ->
                        val intent = Intent(Intent.ACTION_VIEW)
                        intent.data = Uri.parse("market://details?id=$appId")
                        startActivity(intent)
                        //overridePendingTransition(R.anim.slide_up, R.anim.slide_bottom)
                    }
                    builder.show()
                } catch (e: WindowManager.BadTokenException) {
                    Logcat.d("appLink ??????")
                } catch (e: Exception) {
                    Logcat.d("appLink ??????")
                }
            }
        } catch (e: JSONException) {
            Logcat.e("??????????????? [${e.message}]")
        }
    }

    fun secureKeyPad(params: String, succFunc: String, failFunc: String){
        sp.put(Constants.SUCC_FUNC, succFunc)
        sp.put(Constants.FAIL_FUNC, failFunc)
        try {
            val jsonObject = JSONObject(params)
            val keyboardType = jsonObject.optInt("KEYBOARD_TYPE", 4)
            val label = jsonObject.optString("LABEL", "")
            val hint = jsonObject.optString("HINT", "")
            val min = jsonObject.optInt("MIN", 1)
            val max = jsonObject.optInt("MAX", 50)
            val key = jsonObject.optString("KEY", "")

            when(keyboardType){
                1 -> {
                    Logcat.d("keyboardType 1")
//                    SecureKeyPadManager().keyPadActivityOn(activity, Constants.KEYPAD_TYPE_NUMBERIC, label, hint, label+"??? ??????????????????.", max, Constants.REQUEST_KEYPAD,key,min)

//                    val intent = Intent(activity, CustomView::class.java)
//                    intent.putExtra("key",key)
//                    intent.putExtra("title",label)
//                    intent.putExtra("sub_title",hint)
//                    intent.putExtra("min",min)
//                    intent.putExtra("max",max)
//                    activity.startActivityForResult(intent,Constants.KeyPad_Custom_Activity)
                    val intentKeypad = Intent(activity, CustomView::class.java).apply {
                        putExtra("maxNumChar", max)
                        putExtra("key", key)
                        putExtra("title", label)
                        putExtra("sub_title", hint)
                        putExtra("min", min)
                        putExtra("max", max)
                    }
                    activity.startActivityForResult(intentKeypad, Constants.REQUEST_KEYPAD)
                    //activity.//overridePendingTransition(R.anim.slide_up, R.anim.slide_bottom)


                }
                2 -> {
                    Logcat.d("keyboardType order")
                    //SecureKeyPadManager().keyPadActivityOn(activity, Constants.KEYPAD_TYPE_QWERTY, label, hint, label+"??? ??????????????????.", max, Constants.REQUEST_KEYPAD_QWETY,key,min)
                    val intentKeypad = Intent(activity, QwertyView::class.java).apply {
                        putExtra("maxNumChar", max)
                        putExtra("key", key)
                        putExtra("title", label)
                        putExtra("sub_title", hint)
                        putExtra("min", min)
                        putExtra("max", max)
                    }
                    activity.startActivityForResult(intentKeypad, Constants.REQUEST_KEYPAD)
                    //activity.//overridePendingTransition(R.anim.slide_up, R.anim.slide_bottom)

                }
                else -> {
                    Logcat.d("keyboardType order")
                }
            }
        } catch (e: JSONException) {
            println("????????? ?????????????????????.")
        }
    }

    //adid ????????? ?????????
    fun getAdid(params: String, succFunc: String, failFunc: String) {
        class AdidTask : AsyncTask<Void, Void, String>() {
            override fun onPreExecute() {
                super.onPreExecute()

            }
            override fun doInBackground(vararg params: Void?): String {
                var adInfo: AdvertisingIdClient.Info? = null
                try {
                    adInfo = AdvertisingIdClient.getAdvertisingIdInfo(applicationContext)
                } catch (e: IOException) {
                    // Unrecoverable error connecting to Google Play services (e.g.,
                    // the old version of the service doesn't support getting AdvertisingId).
                    val resultCd="9999"
                    val callbackName=sp.getValue(Constants.SUCC_FUNC, "")
                    runOnUiThread {
                        DataSend.getInstance().webViewSend(resultCd, callbackName!!, webView)
                    }
                } catch (e: GooglePlayServicesAvailabilityException) {
                    // Encountered a recoverable error connecting to Google Play services.
                    val resultCd="9999"
                    val callbackName=sp.getValue(Constants.SUCC_FUNC, "")
                    runOnUiThread {
                        DataSend.getInstance().webViewSend(resultCd, callbackName!!, webView)
                    }
                } catch (e: GooglePlayServicesNotAvailableException) {
                    // Google Play services is not available entirely.
                    val resultCd="9999"
                    val callbackName=sp.getValue(Constants.SUCC_FUNC, "")
                    runOnUiThread {
                        DataSend.getInstance().webViewSend(resultCd, callbackName!!, webView)
                    }
                }
                val id = adInfo!!.id
                val isLAT = adInfo!!.isLimitAdTrackingEnabled
                return id
            }
            override fun onPostExecute(result: String?) {
                super.onPostExecute(result)
                val resultCd="0000"
                var resultParam= JSONObject()
                resultParam.put("adid", result)
                DataSend.getInstance().webViewSend(resultCd, resultParam, succFunc, webView)
            }
        }
        var adidTask = AdidTask()
        adidTask.execute()
    }

    fun down(params: String, succFunc: String, failFunc: String){
        val jsonObject = JSONObject(params)
        val downUrl = CryptoUtil.getInstace().decrypt(jsonObject.optString("url", ""))
//        val downUrl =  "http://smart-i.smartbank.co.kr/WEB/issDocu/fullpayment-leejongseok.pdf"
//        val downUrl = "https://corona-live.com/thumbnail.png"
        Logcat.d("downUrl : $downUrl")

        downloadImage(downUrl)
    }
    private fun downloadImage(fileURl: String) {
        var mimetype = ""
        var fileName = ""
        try {
            Logcat.d("fileURl : $fileURl")
            val mimetypeSplit = fileURl.split(".")
            mimetype = mimetypeSplit[mimetypeSplit.size - 1].trim()
            Logcat.d("mimetype : $mimetype")
            val fileNameSplit = fileURl.split("/")
            fileName = fileNameSplit[fileNameSplit.size - 1]
            Logcat.d("fileName : " + fileName)
        } catch (e: java.lang.Exception) {
            Toast.makeText(applicationContext, "?????? ??????.", Toast.LENGTH_LONG).show()
            return
        }

        try {
            // val request = DownloadManager.Request(Uri.parse(url.replace("blob:","")))
            val request = DownloadManager.Request(Uri.parse(fileURl))
            val dm = getSystemService(DOWNLOAD_SERVICE) as DownloadManager

            //contentDisposition = URLDecoder.decode(contentDisposition, "UTF-8") //?????????
            //val FileName = contentDisposition.replace("attachment; filename=", "") //attachment; filename*=UTF-8''?????? ????????????????????? ???????????? ?????????????????? ?????? attachment; filename*=UTF-8''??????
            //val FileName = "AgreeListZip.zip"
            val cookies = CookieManager.getInstance().getCookie(fileURl)
            request.addRequestHeader("cookie", cookies)
            //request.setMimeType("application/octet-stream")
            request.setMimeType(mimetype)
            //request.addRequestHeader("User-Agent", userAgent)
            request.setDescription("Downloading File")
            request.setAllowedOverMetered(true)
            request.setAllowedOverRoaming(true)
            request.setTitle(fileName)
            //request.setRequiresCharging(false)
            request.allowScanningByMediaScanner()
            request.setAllowedOverMetered(true)
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)
            dm.enqueue(request)
            Toast.makeText(applicationContext, "????????? ?????????????????????.", Toast.LENGTH_LONG).show()
        } catch (e: java.lang.Exception) {
            if (ContextCompat.checkSelfPermission(this@MainActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    Toast.makeText(baseContext, "??????????????? ??????\n????????? ???????????????.", Toast.LENGTH_LONG).show()
                    ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 1004)
                } else {
                    Toast.makeText(baseContext, "??????????????? ??????\n????????? ???????????????.", Toast.LENGTH_LONG).show()
                    ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 1004)
                }
            }
        }
    }



    fun pushHist(){
        val intent = Intent(activity, PushHistList::class.java)
        startActivity(intent)
        //activity.//overridePendingTransition(R.anim.slide_up, R.anim.slide_bottom)
    }


    /**
     * ???????????? ???????????? ??????24 ???????????? ?????? ?????? ??????
     */
    private val priSecuNumResult = this@MainActivity.registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result->
        //????????? ??????????????? ????????? ??????????????? ????????? ??????
        val params = sp.getValue(Constants.PARAMS, "")!!
        val succFunc = sp.getValue(Constants.SUCC_FUNC, "")!!
        val failFunc = sp.getValue(Constants.FAIL_FUNC, "")!!

        Logcat.d("priSecuNumResult : result.resultCode : ${result.resultCode}")

        var resultCd = "9999"
        val resultParam = JSONObject()
        if (result.resultCode == Activity.RESULT_OK) {
            resultCd = "0000"
            resultParam.put("code", result.data?.getStringExtra("code"))
//            Toast.makeText(activity,"$scrapErrorCheck ???",Toast.LENGTH_SHORT).show()
            DataSend.getInstance().webViewSend(resultCd, resultParam, succFunc, webView)
        }
        else if(result.resultCode == Activity.RESULT_FIRST_USER) {
            resultCd = "9999"
//            Toast.makeText(activity,"$scrapErrorCheck ???",Toast.LENGTH_SHORT).show()
            DataSend.getInstance().webViewSend(resultCd, resultParam, succFunc, webView)
        }else{
            customProgressDialog.Hide()
        }
    }
    private var scrapErrorCheck = false
    fun priScraping(params: String, succFunc: String, failFunc: String){
        val jsonObject = JSONObject(params)
        sp.put(Constants.PARAMS, params)
        sp.put(Constants.SUCC_FUNC, succFunc)
        sp.put(Constants.FAIL_FUNC, failFunc)
        val prcsDvcd = jsonObject.optString(
                "PRCS_DVCD",
                ""
        )    // R: ???????????? , S:??????
        loading("ON")
        when(prcsDvcd){
            "I" -> {
                SASManager.initInstance()
                SASManager.setContext(activity)
                SASManager.setCryptoMode(false)
                SASManager.setDebugMode(false)
                SASManager.setV8Mode(true)
                priSasManager?.cancel()
                priSasManager = SASManager.getInstance()
                PriScraping.getInstance().init(this@MainActivity, priSecuNumResult, priSasManager!!, succFunc, webView)
                activity.runOnUiThread {
                    val resultCd = "0000"
                    val resultData = JSONObject()
                    DataSend.getInstance().webViewSend(resultCd, resultData, succFunc, webView)
                }
            }
            "S" -> {
                scrapErrorCheck = false
                PriScraping.getInstance().priSecuNum(jsonObject, priSasManager!!)
            }
            "MR" -> {
                PriScraping.getInstance().priReq(jsonObject, priSasManager!!)
            }
            "MS" -> {
                PriScraping.getInstance().priRes(jsonObject, priSasManager!!)
            }
            "MP" -> {
                PriScraping.getInstance().priMinWonLogout(jsonObject, priSasManager!!)
            }
            "NR" -> {
                PriScraping.getInstance().priReq2(jsonObject, priSasManager!!)
            }
            "NS" -> {
                PriScraping.getInstance().priRes2(jsonObject, priSasManager!!)
            }
            "NP" -> {
                PriScraping.getInstance().priMinWonRun2(jsonObject, priSasManager!!)
            }
            "HR" -> {
                PriScraping.getInstance().priReq3(jsonObject, priSasManager!!)
            }
            "HS" -> {
                PriScraping.getInstance().priRes3(jsonObject, priSasManager!!)
            }
            "HP" -> {
                PriScraping.getInstance().priMinWonRun3(jsonObject, priSasManager!!)
            }
        }
    }

    private fun sasManagerInit(){
        SASManager.initInstance()
        SASManager.setContext(activity)
        SASManager.setCryptoMode(false)
        SASManager.setDebugMode(true)
        SASManager.setV8Mode(true)
    }
    fun finScraping(params: String, succFunc: String, failFunc: String){
        val inputData = JSONObject(params)
        val prcsDvcd = inputData.optString(
                "PRCS_DVCD",
                ""
        )    // R: ???????????? , S:??????

        when(prcsDvcd){
            "N" -> {
                //?????? ?????????
                sasManagerInit()
                //???????????? ??????
                finSasManager = SASManager.getInstance()
                //?????? ??????
                FinScraping.getInstance().init(this, inputData, finSasManager!!, webView, sp)
                //???????????? ??????
                FinScraping.getInstance().finSecuNumReq(this, inputData, finSasManager!!)

            }
            "R" -> {
                //??????????????? ???????????? ????????? ??????
                FinScraping.getInstance().finLoginRes(this, inputData, finSasManager!!)
            }
            "S" -> {
                //??????????????? ???????????? ????????? ??????
//                FinScraping.getInstance().finRes(params,finSasManager!!)

            }
        }
    }


    //FSB ?????????????????????
    fun fds(params: String, succFunc: String, failFunc: String) {
        Logcat.d("params: $params")
        //bridgeFailFunc = failFunc
        try {
            val jsonObject = JSONObject(params)
            val prcsDvcd = jsonObject.optString(
                    "PRCS_DVCD",
                    ""
            )    // T: ????????????(??????), R: ??????????????????, P: ????????????, M: mOTP?????? MP: ?????? MD : ?????? FD:????????????
            val key = jsonObject.optString("key", "")
            Logcat.d("FDS test")
            Logcat.d("PRCS_DVCD => $prcsDvcd")
            Logcat.d("key => $key")
            when (prcsDvcd) {
                "U" -> {
                    val tc = SafetokenSimpleClient.getInstance(activity)
                    // ?????? ??????
                    val isExist = tc.isExistToken
                    // Logcat.d("Exist token? => $isExist")
                    var tokenId = ""
                    if (isExist) {   // ????????? ???????????? ????????? ????????????.
                        // ?????? ??????
                        val tokenRef = tc.token
                        // Logcat.d("tokenRef.uid(): " + tokenRef.uid())
                        tokenId = tokenRef.uid()
                    }
                    val resultCd = "0000"
                    var resultParam = JSONObject()
                    resultParam.put("uuid", tokenId)
                    resultParam.put("key", "")
                    resultParam.put("data", "")
                    DataSend.getInstance().webViewSend(resultCd, resultParam, succFunc, webView)
                }
                "D" -> {
                    val tc = SafetokenSimpleClient.getInstance(activity)
                    // ?????? ??????
                    val isExist = tc.isExistToken
                    // Logcat.d("Exist token? => $isExist")
                    var tokenId = ""
                    if (isExist) {   // ????????? ???????????? ????????? ????????????.
                        // ?????? ??????
                        val tokenRef = tc.token
                        // Logcat.d("tokenRef.uid(): " + tokenRef.uid())

                        tokenId = tokenRef.uid()
                    }

                    if (key.isNullOrEmpty()) {
                        alertDlg("????????????????????????", activity)
                        val resultCd = "0000"
                        var resultParam = JSONObject()
                        DataSend.getInstance().webViewSend(resultCd, resultParam, succFunc, webView)
                    } else {
                        val ixLogMessage = FdsHelper.getFds(applicationContext, key)
                        val resultCd = "0000"
                        var resultParam = JSONObject()
                        resultParam.put("uuid", tokenId)
                        resultParam.put("key", ixLogMessage.builtKey)//???????????? ??????????????? ???
                        resultParam.put("data", ixLogMessage.getEveryLog()) //????????????????????????
                        DataSend.getInstance().webViewSend(resultCd, resultParam, succFunc, webView)
                    }
                }
            }
        } catch (e: JSONException) {
            val resultCd="9998"
            DataSend.getInstance().webViewSend(resultCd, succFunc, webView)
            Logcat.d("????????? ?????????????????????.")
        }
    }






    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        //null ?????? ?????? ????????? ?????????????????? ??????
        if (requestCode == Constants.REQUEST_FILE_CHOOSE){
            if(resultCode==RESULT_OK){
                Logcat.d("??????????????? ??????")

                if(mFilePathCallback == null){
                    Logcat.d("??????????????? ??????")
                    return
                }

                if (data!!.data == null) {
                    data!!.data = cameraImageUri
                }

                //????????? ??????
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    mFilePathCallback!!.onReceiveValue(WebChromeClient.FileChooserParams.parseResult(resultCode, data));
                    mFilePathCallback = null;
                }else{
                    mFilePathCallback!!.onReceiveValue(arrayOf(Uri.parse(data!!.dataString)));
                }
            }else if (resultCode== RESULT_CANCELED){
                Logcat.d("??????????????? ??????")
                if(mFilePathCallback!=null){
                    mFilePathCallback!!.onReceiveValue(null)
                }
            }else{
                Logcat.d("??????????????? ???????????? ??????")
            }
        }else{
            val commonDto = CommonDTO(activity, supportFragmentManager, sp)
            commonDto.webView=this.webView
            commonDto.customProgressDialog=customProgressDialog
//            commonDto.sasManager = priSasManager!!
            if (this.cameraFile != null){
                commonDto.cameraFile= this.cameraFile!!
            }
            ActivityResult().onActivityResult(requestCode, resultCode, data, commonDto)

        }
    }


    override fun onSASRunCompleted(index: Int, outString: String?) {
        Logcat.d("[scraping result] index : $index , outString : $outString")
    }

    // SASRunStatusChangedListener
    // SASManager.run??? ???????????? ??????,
    // status??? ???????????? ??????
    override fun onSASRunStatusChanged(action: Int, percent: Int) {
        Logcat.d("onSASRunStatusChanged : $action , $percent");
    }


    override fun onDestroy() {
        super.onDestroy()
        //???????????? ??????
        EversafeHelper.getInstance().destroy()
    }
}