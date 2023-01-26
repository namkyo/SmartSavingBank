package kr.co.smartbank.app.process

import android.content.Context

data class FaceImageResult(var score: Int = 0
                           , var msg: String = ""
                           , var resultImage: String = ""
                           , var faceImage: ByteArray
                           , var context: Context)
