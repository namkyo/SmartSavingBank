package kr.co.smartbank.app.process

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import bio.face.FaceDetect
import kr.co.smartbank.app.util.Logcat

object FaceManager {
    //사진 품질검사 실시후 정상인 사진인 경우 특징점 추출 실시
    fun facePrint(image:ByteArray,context: Context): FaceImageResult {
        var faceImageResult = FaceImageResult(faceImage = image,context = context)

        val fd = FaceDetect()
        //한국인식산업 솔루션 초기화
        Logcat.d("FaceDetectQA start")
        FaceDetectQA_Start(fd, faceImageResult)

        faceImageResult = FaceDetectQA_Process(fd,faceImageResult)

        //한국인식산업 솔루션 종료
        Logcat.d("FaceDetectQA end")
        FaceDetectQA_Stopt(fd)

        return faceImageResult
    }

    private fun FaceDetectQA_Process(fd:FaceDetect,inFix:FaceImageResult):FaceImageResult{
        val FaceDetectBuf = ByteArray(4668) // base 64
        val iType = 1 // 1: bmp, 2: jpg
        inFix.score= FaceDetectQA(fd, inFix.faceImage, inFix.faceImage.size, iType, FaceDetectBuf)
        for(i in 0..4667){
            inFix.resultImage+=FaceDetectBuf[i].toChar().toString()
        }
        return inFix
    }

    private fun FaceDetectQA_Stopt(fd: FaceDetect) {
        // 프로그램 종료시 한번 실행
        fd.FA_End()
    }

    //한국인식산업 모듈 초기화
    private fun FaceDetectQA_Start(fd: FaceDetect, infix: FaceImageResult): Int {
        var isFaceDetectInit = 0
        // 프로그램 시작시 한번 실행
        val rt: Int = fd.FA_Start("", infix.context) // init
        Logcat.d("FA_Start $rt")
        if (rt == -1) {
            Logcat.d("License Time expire ")
        }
        if (rt == -2) {
            Logcat.d("License mac address fail ")
        }
        if (rt == 0) {
            Logcat.d("FA_Start fail ")
        }
        Logcat.d("License OK")
        Logcat.d("FA_Start OK")
        return rt
    }


    private fun FaceDetectQA(fd: FaceDetect, Buf: ByteArray, nFileSize: Int, iType: Int, FaceDetectBuf: ByteArray): Int {
        Logcat.d("FA_QA_BMP start ")
        var result = -1
        result = -20000
        Logcat.d("FA_QA_BMP start 2 ")
        // 품질검사 실시
        var QA_Result = 0
        val arr = IntArray(2)
        QA_Result = if (iType == 1) {
            fd.FA_QA_BMP(Buf, nFileSize, arr)
        } else {
            fd.FA_QA_JPG(Buf, nFileSize, arr)
        }
        Logcat.d("FA_QA_BMP end ")
        result -= QA_Result
        //QA 결과 출력
        if (QA_Result == 0) Logcat.d("인식가능")
        else if (QA_Result == 1) Logcat.d("초점흐림")
        else if (QA_Result == 2) Logcat.d("반 사 광")
        else if (QA_Result == 3) Logcat.d("홀로그램")
        else if (QA_Result == 4) Logcat.d("흑백")
        else  Logcat.d("에러$QA_Result")
        if (QA_Result == 0) { // 품질검사통과하면
            var detect_rt = 0
            detect_rt = if (iType == 1) {
                fd.FA_Detect_bmp_base64(Buf, nFileSize, FaceDetectBuf) //
            } else {
                fd.FA_Detect_jpg_base64(Buf, nFileSize, FaceDetectBuf) //
            }
            result = detect_rt
            Logcat.d("검출 결과 $detect_rt")
        }
        return result
    }
}