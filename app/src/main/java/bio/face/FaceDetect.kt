package bio.face

import android.content.Context
import android.util.Log
import kr.co.smartbank.app.util.Logcat
import java.io.IOException
import java.io.InputStream

class FaceDetect {
    var fp = Faceprint()
    @Throws(IOException::class)
    fun CreateMemBufFromFile(path: String?, Buf: ByteArray?, context: Context): Int {
        var myInput: InputStream? = null
        myInput = context.assets.open(path!!)
        val nFileSize = myInput.read(Buf)
        myInput.close()
        return nFileSize
    }

    fun FA_Start(path: String?, context: Context): Int {
        var result = 0
        val FAP_Data1 = ByteArray(60 * 1024) //Classifier
        val FAP_Data2 = ByteArray(15 * 1024) //MT
        val FAP_Data3 = ByteArray(620 * 1024) //TFT
        val FAP_Data4 = ByteArray(6 * 1024) //TFT_SPADE
        val FAP_Data5 = ByteArray(11 * 1024) //Mask.raw
        val FAP_Data6 = ByteArray(9150489) //kii_5_landmarks.dat
        //byte FAP_Data7[] = new byte[ 1070684 ]; //hologram_detector.svm
        val FAP_Data7 = ByteArray(2 * 1024 * 1024) //hologram_detector.svm , max 2M Byte
        var nSize1 = 0
        var nSize2 = 0
        var nSize3 = 0
        var nSize4 = 0
        var nSize5 = 0
        var nSize6 = 0
        var nSize7 = 0
        try {
            nSize1 = CreateMemBufFromFile("Data/Classifier", FAP_Data1, context)
            nSize2 = CreateMemBufFromFile("Data/MT", FAP_Data2, context)
            nSize3 = CreateMemBufFromFile("Data/TFT", FAP_Data3, context)
            nSize4 = CreateMemBufFromFile("Data/TFT_SPADE", FAP_Data4, context)
            nSize5 = CreateMemBufFromFile("Data/Mask.raw", FAP_Data5, context)
            nSize6 = CreateMemBufFromFile("Data/kii_5_landmarks.dat", FAP_Data6, context)
            nSize7 = CreateMemBufFromFile("Data/hologram_detector.svm", FAP_Data7, context)
            Logcat.d("nSize1=$nSize1")
            Logcat.d("nSize2=$nSize2")
            Logcat.d("nSize3=$nSize3")
            Logcat.d("nSize4=$nSize4")
            Logcat.d("nSize5=$nSize5")
            Logcat.d("nSize6=$nSize6")
            Logcat.d("nSize7=$nSize7")
            result = if (nSize1 < 1 || nSize2 < 1 || nSize3 < 1 || nSize4 < 1 || nSize5 < 1 || nSize6 < 1 || nSize7 < 1) {
                -3
            } else {
                val nResult = fp.jniFALibInit(path,
                        nSize1, FAP_Data1,
                        nSize2, FAP_Data2,
                        nSize3, FAP_Data3,
                        nSize4, FAP_Data4,
                        nSize5, FAP_Data5,
                        nSize6, FAP_Data6,
                        nSize7, FAP_Data7
                )
                nResult
            }
        } catch (e: IOException) {
            Logcat.e("에러입니다 [${e.message}]")
            result = -3
        }
        return result
    }

    fun FA_End(): Int {
        return fp.jniFALibUnInit()
    }

    fun FA_Detect(Image: ByteArray?, nWidth: Int,
                  nHeight: Int,
                  nFormat: Int,
                  DBFeature: ByteArray? //
    ): Int {
        return fp.jniFAExtractDb(Image, nWidth, nHeight, nFormat, DBFeature)
    }

    fun FA_Detect_base64(Image: ByteArray?, nWidth: Int,
                         nHeight: Int,
                         nFormat: Int,
                         DBFeature: ByteArray? //
    ): Int {
        return fp.jniFAExtractDbBase64(Image, nWidth, nHeight, nFormat, DBFeature)
    }

    fun FA_Detect_bmp(Image: ByteArray?, nSize: Int, DBFeature: ByteArray? // 13,000 byte bmp
    ): Int {
        return fp.jniFAExtractDbBmp(Image, nSize, DBFeature)
    }

    fun FA_Detect_bmp_base64(Image: ByteArray?, nSize: Int, DBFeature: ByteArray? // 13,000 byte bmp
    ): Int {
        return fp.jniFAExtractDbBmpBase64(Image, nSize, DBFeature)
    }

    fun FA_Detect_jpg(Image: ByteArray?, nSize: Int, DBFeature: ByteArray? // 13,000 byte bmp
    ): Int {
        return fp.jniFAExtractDbJpg(Image, nSize, DBFeature)
    }

    fun FA_Detect_jpg_base64(Image: ByteArray?, nSize: Int, DBFeature: ByteArray? // 13,000 byte bmp
    ): Int {
        return fp.jniFAExtractDbJpgBase64(Image, nSize, DBFeature)
    }

    fun GetVersio(): String? {
        return fp.jniGetVersion()
    }

    fun FA_QA_JPG(buf: ByteArray?, nSize: Int, score_flag: IntArray?): Int {
        return fp.QualityCheckJPG(buf, nSize, score_flag)
    }

    fun FA_QA_BMP(buf: ByteArray?, nSize: Int, score_flag: IntArray?): Int {
        return fp.QualityCheckBMP(buf, nSize, score_flag)
    }

    fun FA_getDeFeatureBase64(Feature: ByteArray?, arr: IntArray?): ByteArray? {
        return fp.getDeFeatureBase64(Feature, arr)
    }
}