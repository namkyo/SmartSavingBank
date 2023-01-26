package kr.co.smartbank.app.solution.face.view

import android.app.Activity
import android.content.Intent
import android.graphics.Point
import android.graphics.Rect
import android.os.*
import android.view.View
import android.widget.*
import com.inzisoft.mobile.data.MIDReaderProfile
import com.inzisoft.mobile.data.RecognizeResult
import com.inzisoft.mobile.recogdemolib.CameraPreviewInterface
import com.inzisoft.mobile.recogdemolib.LibConstants
import com.inzisoft.mobile.util.BeepSoundPool
import kr.co.smartbank.app.R
import kr.co.smartbank.app.config.Constants
import kr.co.smartbank.app.solution.face.util.AppConstants
import kr.co.smartbank.app.util.CryptoUtil
import kr.co.smartbank.app.util.LoadingUtil
import kr.co.smartbank.app.util.Logcat
import kr.co.smartbank.app.util.SharedPreferenceHelper
import kr.co.smartbank.app.view.BaseActivity

class OcrPreviewActivity : BaseActivity() {

    //OCR 카메라 오버레이 디자인
    private lateinit var mOverlayView: IDCardTypeOverlayView

    //OCR 인지소프트 모듈
    private lateinit var mCameraPreviewInterface: CameraPreviewInterface

    //공통
    private lateinit var activity   : Activity
    private lateinit var sp         : SharedPreferenceHelper

    //자동촬영여부
    private var mIsAutoCaptureEnabled = false
    //프리뷰 해상도
    private var mPreviewResolution : Point? = null

    private lateinit var btFindEdge : ImageButton
    private lateinit var btTakePicture : Button
    private lateinit var tvFindEdge : TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //가로세로 모드 0: 가로 , 1:세로
        val orientation = intent.getIntExtra("orientation", 1)
        Logcat.d("orientation : $orientation")

        if (orientation == AppConstants.ORIENTATION_LANDSCAPE) {
            setContentView(R.layout.activity_camera_preview)
        } else {
            setContentView(R.layout.activity_camera_preview_port)
        }


        activity = this
        sp = SharedPreferenceHelper(activity)

        //자동촬영모드
        val isTakeMode = intent.getBooleanExtra(resources.getString(R.string.ocr_take_mode), false)
        Logcat.d("isTakeMode : $isTakeMode")
        mIsAutoCaptureEnabled = isTakeMode


        if (mPreviewResolution == null) {
            mPreviewResolution = Point()
        }

        requestedOrientation = orientation


        initLayout()
    }

    private fun makePreView(auto:Boolean){

        //촬영 모드(가로,세로), picture해상도 설정
        MIDReaderProfile.getInstance().SET_USER_SCREEN_PORTRAIT = true //세로
        MIDReaderProfile.getInstance().ENABLE_TOAST         = true  //내부모듈 토스트사용여무
        MIDReaderProfile.getInstance().DEBUGABLE            = false //디버깅로그사용여부
        MIDReaderProfile.getInstance().SET_USE_BEEP_SOUND   = true  //자동촬영시 비프음 사용여부
        MIDReaderProfile.getInstance().FIND_EDGE_ON_PREVIEW = true  //신분증 인식라인

        //신분증 라인
        mOverlayView = IDCardTypeOverlayView(activity, auto)
        mCameraPreviewInterface = CameraPreviewInterface(activity,
                object : CameraPreviewInterface.MoveToRecognizeActivityListener {
                    override fun callback(pictureROI: Rect?, resultCode: Int) {
                        Logcat.d("OCR 결과 MoveToRecognizeActivityListener $pictureROI")
                        Logcat.d("resultCode :$resultCode")
                        if (resultCode == LibConstants.LICENSE_RECOGNITION_TYPE_FAIL) {
                            Toast.makeText(activity, R.string.str_not_support_function, Toast.LENGTH_LONG).show()
                            return
                        } else if (resultCode == LibConstants.ERR_CODE_TAKE_PICTURE_FAILED) {
                            setResult(LibConstants.ERR_CODE_TAKE_PICTURE_FAILED)
                            finish()
                        }
                        val intent = Intent(activity, OcrRecognizeActivity::class.java)
                        intent.putExtra(AppConstants.INTENT_RECT_PICTURE, pictureROI)
                        intent.putExtra(AppConstants.INTENT_RECOG_TYPE, LibConstants.TYPE_IDCARD)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                        startActivityForResult(intent, Constants.OCR_Activity_05)
                    }
                    override fun onCameraStarted() {
                        if (mPreviewResolution == null) {
                            mPreviewResolution = mCameraPreviewInterface.previewResolution
                        }
                    }
                })
        /**
         * 실패콜백
         */
        mCameraPreviewInterface.setStartCameraFailedListener {
            Logcat.e("OCR 에러")
            finish()
        }
        /**
         * 자동촬영 인식 완료후 다음단계로 진행하기 위한 리스너
         */
        mCameraPreviewInterface.setPreviewRecognizeListener(object :
                CameraPreviewInterface.PreviewRecognizeListener {
            override fun onRecognitionStarted() {
                Logcat.d("onRecognitionStarted()")
            }

            override fun onRecognitionEnded() {
                Logcat.d("onRecognitionEnded()")
                pick()
            }
        })

        //카메라 Preview Layout 초기화

        if (auto) {
            mIsAutoCaptureEnabled=true
            btFindEdge.alpha = 1.0f
            tvFindEdge.text="자동촬영"
            btTakePicture.visibility = View.GONE
        } else {
            mIsAutoCaptureEnabled=false
            btFindEdge.alpha = 0.1f
            tvFindEdge.text="수동찰영"
            btTakePicture.visibility = View.VISIBLE

        }

        mCameraPreviewInterface.initLayout(btTakePicture, mOverlayView, R.id.camera_preview)
        mCameraPreviewInterface.setPictureDesireResolution(3000000) //입력된 해상도와 가장 근접한 해상도을 구한다
        mCameraPreviewInterface.setRecogType(LibConstants.TYPE_IDCARD) //TYPE_IDCARD 주민등록증 및 신분증
        mCameraPreviewInterface.setAutoCaptureEnable(false) //자동촬영여부
        mCameraPreviewInterface.setTakePictureDelayTime(1000)
        mCameraPreviewInterface.setAutoCaptureThreshold(0.1f)
        mCameraPreviewInterface.setPreviewPictureRecogEnable(auto) //프리뷰 인식
        mCameraPreviewInterface.onCreate()
    }

    private fun pick(){
        val idResult = RecognizeResult.getInstance().idCardRecognizeResult

        if(idResult!=null){
            val nameLength: Int = idResult.getNameLength(activity)
            val rrnLength = idResult.getRrnLength(activity)
            val licenseLength: Int = idResult.getLicenseNumberLength(activity)
            val dateLength: Int = idResult.getDateLength(activity)

            //주민등록증
            if (idResult.name.length > 1 && rrnLength == 13 && dateLength == 8 && licenseLength==0) {
                Logcat.d("name : ${idResult.name}")
                Logcat.d("nameLength : $nameLength")
                Logcat.d("rrnLength : $rrnLength")
                Logcat.d("licenseLength : $licenseLength")
                Logcat.d("dateLength : $dateLength")
                ocrDataSend("1")
            }else if(idResult.name.length > 1 && rrnLength == 13 && dateLength == 8 && licenseLength>0){
                ocrDataSend("2")
            }
            /**
             * 제데로 인식이 안됬을경우 재실행
             */
            else {
                mCameraPreviewInterface.setContinuePreviewRecognition()
            }
        }else{
            mCameraPreviewInterface.setContinuePreviewRecognition()
        }
    }

    private fun ocrDataSend(recogType: String) {
        //OCR결과
        val idResult = RecognizeResult.getInstance().idCardRecognizeResult
        LoadingUtil.show(customProgressDialog)
        // 인식 완료 시 진동 발생
        // 1. Vibrator 객체를 얻어온 다음
        val vibrator = getSystemService(VIBRATOR_SERVICE) as Vibrator

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
            // 2. 진동 구현: 1000ms 동안 100의 강도로 울린다.
            vibrator.vibrate(VibrationEffect.createOneShot(200, 100))
        }
//        else{
//            vibrator.vibrate(200)
//        }


        //mOverlayView.setRecogSuccess(true)
        //camera shutter sound
        BeepSoundPool.getInstance().playBeepSound()
        try {
            mCameraPreviewInterface.onPause(true)
            Thread.sleep(300)
        } catch (e: InterruptedException) {
            Logcat.e("에러 : ${e.message}")
        }

        val isMasking = intent.getBooleanExtra(resources.getString(R.string.isMasking), false)
        val isDetail = intent.getBooleanExtra(resources.getString(R.string.isDetail), false)
        val isSerialNum = intent.getBooleanExtra(resources.getString(R.string.isSerialNum), false)

        val intent = Intent(activity, OcrResultActivity::class.java)
        intent.apply {
            putExtra(resources.getString(R.string.isMasking), isMasking)
            putExtra(resources.getString(R.string.isDetail), isDetail)
            putExtra(resources.getString(R.string.isSerialNum), isSerialNum)

            putExtra(resources.getString(R.string.ocr_type), recogType)
            putExtra(resources.getString(R.string.ocr_name), CryptoUtil.getInstace().encrypt(idResult.name))
            putExtra(resources.getString(R.string.ocr_rbrNo), CryptoUtil.getInstace().encrypt(idResult.rrn))
            //운전면허증일 경우만 암호화
            if(recogType=="2"){
                putExtra(resources.getString(R.string.ocr_driveNo), CryptoUtil.getInstace().encrypt(idResult.licenseNumber))
                putExtra(resources.getString(R.string.ocr_secureNo), CryptoUtil.getInstace().encrypt(idResult.securitySerial))
            }else{
                putExtra(resources.getString(R.string.ocr_driveNo), "")
                putExtra(resources.getString(R.string.ocr_secureNo), "")
            }
            putExtra(resources.getString(R.string.ocr_issueDate), CryptoUtil.getInstace().encrypt(idResult.date))
//            putExtra(resources.getString(R.string.ocr_photoStr),"")
//            putExtra(resources.getString(R.string.ocr_detailPhotoStr),"")
            putExtra(resources.getString(R.string.ocr_image_no_making), "")
            putExtra(resources.getString(R.string.ocr_issueOffice), CryptoUtil.getInstace().encrypt(idResult.issueOffice))
        }
        startActivityForResult(intent, Constants.OCR_Activity_03)
    }

    private fun initLayout(){

        btTakePicture = findViewById<View>(R.id.button_take_camera) as Button
//        btTakePicture.setOnClickListener {
//            Logcat.d("수동촬영 ㄱ")
//            pick()
//        }

        //취소버튼
        val btCancel = findViewById<View>(R.id.button_cancel_camera) as Button
        btCancel.setOnClickListener {
            setResult(RESULT_CANCELED)
            onBackPressed()
        }

        //갤러리버튼 안씀
        val btGallery = findViewById<View>(R.id.button_load_gallery) as Button
        btGallery.visibility = View.GONE

        val llFindedge = findViewById<View>(R.id.ly_findedge) as LinearLayout
        llFindedge.visibility = View.VISIBLE


        //자동촬영글자
        tvFindEdge = findViewById<View>(R.id.tv_findedge) as TextView
        tvFindEdge.visibility = View.VISIBLE




        /**
         * 자동,수동 촬영모드 전환버튼
         */
        btFindEdge = findViewById<View>(R.id.button_findedge) as ImageButton
        btFindEdge.visibility = View.VISIBLE
        btFindEdge.alpha = 1.0f
        btFindEdge.setOnClickListener{
            if (mIsAutoCaptureEnabled) {
                makePreView(false)
            } else {
                makePreView(true)
            }
            mCameraPreviewInterface.setPreviewPictureRecogEnable(mIsAutoCaptureEnabled) //자동촬영여부
            mCameraPreviewInterface.onResume()
        }

        /**
         * 가이드 버튼
         */
        val tgGuide = findViewById<View>(R.id.tb_guide) as ToggleButton
        tgGuide.visibility = View.GONE
        tgGuide.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                mCameraPreviewInterface.setAutoCaptureThreshold(1f)
                mOverlayView.invalidate()
                tvFindEdge.visibility=View.VISIBLE
            } else {
                mOverlayView.invalidate()
                mCameraPreviewInterface.setAutoCaptureThreshold(1f)
                tvFindEdge.visibility=View.GONE
            }
            mCameraPreviewInterface.onResume()
        }


        LoadingUtil.show(customProgressDialog)
        makePreView(true)
        Handler(Looper.myLooper()!!).postDelayed({
            LoadingUtil.hide(customProgressDialog)
        }, 500)
    }

    override fun onResume() {
        super.onResume()
        mCameraPreviewInterface.onResume()
    }

    override fun onPause() {
        super.onPause()
        mCameraPreviewInterface.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mCameraPreviewInterface.release()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Logcat.d("OCR_Activity_03 requestCode : $requestCode , resultCode : $resultCode")
        LoadingUtil.hide(customProgressDialog)
        when(requestCode){
            Constants.OCR_Activity_03 -> {
                //OCR 결과 들고 복귀
                if (resultCode == Activity.RESULT_OK) {
                    setResult(Activity.RESULT_OK, data)
                    finish()
                } else {
                    makePreView(true)
                }
            }

            Constants.OCR_Activity_05 -> {
                //OCR 수동
                if (resultCode == Activity.RESULT_OK) {
                    pick()
                } else {
                    makePreView(true)
                }
            }
        }
    }
}