package kr.co.smartbank.app.solution.face.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Base64
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.inzisoft.mobile.data.RecognizeResult
import kr.co.smartbank.app.R
import kr.co.smartbank.app.process.FaceManager
import kr.co.smartbank.app.solution.face.util.OCRUtil
import kr.co.smartbank.app.util.CryptoUtil
import kr.co.smartbank.app.util.Logcat
import kr.co.smartbank.app.util.SharedPreferenceHelper
import kr.co.smartbank.app.view.BaseActivity


class OcrResultActivity : BaseActivity()  {
    //공통
    private lateinit var activity   : Activity
    private lateinit var sp         : SharedPreferenceHelper

    //신분증[출력이미지]
    private lateinit var mIvResultView: ImageView
    //신분증[타입]
    private lateinit var mTvIdType: TextView
    //신분증[주민등록번호]
    private lateinit var mEditIdNumber: EditText
    //신분증[이름]
    private lateinit var mEditName: EditText
    //신분증[면허번호]
    private lateinit var mEditLicenseNumber: EditText
    //신분증[발급일자]
    private lateinit var mEditIssueDate: EditText
    //신분증[암호일련번호]
    private lateinit var mEditLicenseCheck: EditText
    //신분증[발급처]
    private lateinit var mEditIssueOffice: EditText

    private lateinit var ocrResult: Intent

    //주민등록증,운전면허증 마다 결과폼 늘리고 줄이기
    private lateinit var mGrpLicenseLicenseNumber: ViewGroup
    private lateinit var mGrpLicenseCheck: ViewGroup
    private lateinit var mGrpIssueOffice: ViewGroup

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity = this
        sp = SharedPreferenceHelper(activity)
        setContentView(R.layout.activity_ocr_result)

        ocrResult = Intent()

        mIvResultView = findViewById(R.id.id_result_recog_imageview_result)
        mTvIdType = findViewById(R.id.id_result_recog_textview_idtype)
        mEditIdNumber = findViewById(R.id.id_result_recog_edit_idnumber)
        mEditName = findViewById(R.id.id_result_recog_edit_name)
        mEditLicenseNumber = findViewById(R.id.id_result_recog_edit_licensenumber)
        mEditIssueDate = findViewById(R.id.id_result_recog_edit_issuedate)
        mEditLicenseCheck = findViewById(R.id.id_result_recog_edit_licensecheck)
        mEditIssueOffice = findViewById(R.id.id_result_recog_edit_office)

        mGrpLicenseLicenseNumber = findViewById(R.id.id_result_recog_layout_licensenumber)
        mGrpLicenseCheck = findViewById(R.id.id_result_recog_layout_licensecheck)
        mGrpIssueOffice  = findViewById(R.id.id_result_recog_layout_office)
        /**
         * OCR 최종전송버튼
         */
        val sendBtn = findViewById<Button>(R.id.face_recog_next)
        sendBtn.setOnClickListener{
            /** 이름 **/
            ocrResult.putExtra(resources.getString(R.string.ocr_name),CryptoUtil.getInstace().encrypt(mEditName.text.toString()))

            /** 발급일자 **/
            ocrResult.putExtra(resources.getString(R.string.ocr_issueDate),CryptoUtil.getInstace().encrypt(mEditIssueDate.text.toString().replace("-","")))

            /** 발급처 **/
            ocrResult.putExtra(resources.getString(R.string.ocr_issueOffice),CryptoUtil.getInstace().encrypt(mEditIssueOffice.text.toString()))

            /** 암호일련번호 **/
            ocrResult.putExtra(resources.getString(R.string.ocr_secureNo),CryptoUtil.getInstace().encrypt(mEditLicenseCheck.text.toString()))

            setResult(Activity.RESULT_OK, ocrResult)
            finish()
        }

        /**
         * OCR 재 촬영 버튼
         */
        val cancelBtn = findViewById<Button>(R.id.face_recog_cancel)
        cancelBtn.setOnClickListener{
            setResult(Activity.RESULT_CANCELED, intent)
            finish()
        }

//        val face_recog_activity_title_sub = findViewById<TextView>(R.id.face_recog_activity_title_sub)


        setOCRdata()
    }

    private fun setOCRdata(){
        val isMasking = intent.getBooleanExtra(resources.getString(R.string.isMasking), false)
        val isDetail = intent.getBooleanExtra(resources.getString(R.string.isDetail), false)
        val isSerialNum = intent.getBooleanExtra(resources.getString(R.string.isSerialNum), false)

        Logcat.d("OCR_ResultRecog_Activity : isMasking = $isMasking")
        Logcat.d("OCR_ResultRecog_Activity : isDetail = $isDetail")
        Logcat.d("OCR_ResultRecog_Activity : isSerialNum = $isSerialNum")


        if(intent.getStringExtra(resources.getString(R.string.ocr_type))=="1"){
            mTvIdType.text="주민등록증"
            mGrpLicenseLicenseNumber.visibility = View.GONE
        }else{
            mTvIdType.text="운전면허증"
            mGrpLicenseLicenseNumber.visibility = View.VISIBLE
        }

        /**
         * 특장점 사용여부
         */
        if(isDetail){
            RecognizeResult.getInstance().photoFaceByte?.let {
                val face = FaceManager.facePrint(it, activity)
                Logcat.d("face : $face")
                if(face.score<60){
                    Toast.makeText(activity, "신분증 얼굴에 대해 품질이 낮습니다 재촬영 하십시오.(${face.score})점)", Toast.LENGTH_SHORT).show()
                    setResult(Activity.RESULT_CANCELED, intent)
                    finish()
                }else{
                    ocrResult.putExtra(resources.getString(R.string.ocr_detailPhotoScore), "${face.score}")
                    ocrResult.putExtra(resources.getString(R.string.ocr_detailPhotoStr), CryptoUtil.getInstace().encrypt(face.resultImage))
                }
            }
        }

        /**
         * 미리보기 이미지 마스킹여부
         */
        RecognizeResult.getInstance().getRecogResultImage(false)?.let {
            if(isMasking){
                RecognizeResult.getInstance().getRecogResultImage(true, true, true)?.let {masking ->
                    mIvResultView.setImageBitmap(masking)
                }
            }else{
                mIvResultView.setImageBitmap(it)
            }
            val cryImg = CryptoUtil.getInstace().encrypt(Base64.encodeToString(OCRUtil.bitmapToByteArray(it),Base64.NO_WRAP))
            ocrResult.putExtra(resources.getString(R.string.ocr_photoStr), cryImg)
        }
//        RecognizeResult.getInstance().cleanOriginImage()
//        RecognizeResult.getInstance().clean()
        RecognizeResult.getInstance().cleanRecogData()

        /**
         * 이름 출력
         */
        intent.getStringExtra(resources.getString(R.string.ocr_name))?.let {
            mEditName.setText(CryptoUtil.getInstace().decrypt(it))
        }
        /**
         * 주민번호 출력
         */
        intent.getStringExtra(resources.getString(R.string.ocr_rbrNo))?.let {
            ocrResult.putExtra(resources.getString(R.string.ocr_rbrNo),it)
            if(CryptoUtil.getInstace().decrypt(it).length==13 && isMasking){
                val str =CryptoUtil.getInstace().decrypt(it).substring(0, 6) +
                        "-" +
                        CryptoUtil.getInstace().decrypt(it).substring(6, 7) +
                        "******"
                mEditIdNumber.setText(str)
            }else{
                mEditIdNumber.setText(CryptoUtil.getInstace().decrypt(it))
            }
        }
        /**
         * 발급일자 출력
         */
        intent.getStringExtra(resources.getString(R.string.ocr_issueDate))?.let {
//            if (CryptoUtil.getInstace().decrypt(it).length == 8) {
//                val str = CryptoUtil.getInstace().decrypt(it).substring(0, 4) +
//                        "-" +
//                        CryptoUtil.getInstace().decrypt(it).substring(4, 6) +
//                        "-" +
//                        CryptoUtil.getInstace().decrypt(it).substring(6, 8)
//                mEditIssueDate.setText(str)
//            } else {
//                mEditIssueDate.setText(CryptoUtil.getInstace().decrypt(it))
//            }
            mEditIssueDate.setText(CryptoUtil.getInstace().decrypt(it))
        }
        /**
         * 면허번호 출력
         */
        intent.getStringExtra(resources.getString(R.string.ocr_driveNo))?.let {
            ocrResult.putExtra(resources.getString(R.string.ocr_driveNo),it)
            if (CryptoUtil.getInstace().decrypt(it).length>8 && isMasking) {
                val str = StringBuilder()
                str.append(CryptoUtil.getInstace().decrypt(it).substring(0, 8))
                for (i in 8..(CryptoUtil.getInstace().decrypt(it).length)) {
                    str.append("*")
                }
                mEditLicenseNumber.setText(str.toString())
            } else {
                mEditLicenseNumber.setText(CryptoUtil.getInstace().decrypt(it))
            }
        }

        /**
         * 발급처
         */
        intent.getStringExtra(resources.getString(R.string.ocr_issueOffice))?.let {
            mEditIssueOffice.setText(CryptoUtil.getInstace().decrypt(it))
        }

        /**
         * 암호일련번호
         * */
        if(intent.getStringExtra(resources.getString(R.string.ocr_type))=="2" && isSerialNum){
            intent.getStringExtra(resources.getString(R.string.ocr_secureNo))?.let {
                mGrpLicenseCheck.visibility=View.VISIBLE
                mEditLicenseCheck.setText(CryptoUtil.getInstace().decrypt(it))
            }
        }else{
            mGrpLicenseCheck.visibility=View.GONE
        }
    }
}