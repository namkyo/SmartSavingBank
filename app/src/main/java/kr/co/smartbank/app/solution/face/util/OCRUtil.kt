package kr.co.smartbank.app.solution.face.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import kr.co.smartbank.app.util.Logcat
import java.io.ByteArrayOutputStream

object OCRUtil {
    fun byteArrayToBitmap(mbyteArray: ByteArray): Bitmap {
        Logcat.d("변환전 용량 : " + mbyteArray.size)
        val options = BitmapFactory.Options()
        options.inMutable = true
        options.inSampleSize = 1
        val image = BitmapFactory.decodeByteArray(mbyteArray, 0, mbyteArray.size, options)
        val width = options.outWidth
        val height = options.outHeight
        Logcat.d("신분증 width : $width")
        Logcat.d("신분증 height : $height")
        return image
    }

    fun bitmapToByteArray(mbitmap: Bitmap): ByteArray {
        val stream = ByteArrayOutputStream()
        mbitmap.compress(Bitmap.CompressFormat.JPEG, 70, stream)
        Logcat.d("변환후 용량 : " + stream.toByteArray().size)
        return stream.toByteArray()
    }

    fun bitmapToByteArray(mbitmap: Bitmap, quality: Int): ByteArray {
        val stream = ByteArrayOutputStream()
//        bitmapResize(mbitmap)?.let {
//            it.compress(Bitmap.CompressFormat.JPEG, quality, stream)
//        }
        mbitmap.compress(Bitmap.CompressFormat.JPEG,quality,stream)
        Logcat.d("변환후 용량 : " + stream.toByteArray().size);
        return stream.toByteArray();
    }

    fun bitmapResize(bitmap: Bitmap): Bitmap {
        val options = BitmapFactory.Options()
        options.inSampleSize = 4

        val width = 510 // 축소시킬 너비
        val height = 316 // 축소시킬 높이

        var bmpWidth = bitmap.width.toFloat()
        var bmpHeight = bitmap.height.toFloat()

        if (bmpWidth > width) {
            // 원하는 너비보다 클 경우의 설정
            val mWidth = bmpWidth / 100
            val scale = width / mWidth
            bmpWidth *= scale / 100
            bmpHeight *= scale / 100
        } else if (bmpHeight > height) {
            // 원하는 높이보다 클 경우의 설정
            val mHeight = bmpHeight / 100
            val scale = height / mHeight
            bmpWidth *= scale / 100
            bmpHeight *= scale / 100
        }
        val resizedBmp = Bitmap.createScaledBitmap(
            bitmap,
            bmpWidth.toInt(), bmpHeight.toInt(), true
        )
        return resizedBmp
    }

    fun bitmapResize(bitmap: Bitmap,_width:Int, _height : Int): Bitmap {
        val options = BitmapFactory.Options()
        options.inSampleSize = 4

        var bmpWidth = bitmap.width.toFloat()
        var bmpHeight = bitmap.height.toFloat()

        if (bmpWidth > _width) {
            // 원하는 너비보다 클 경우의 설정
            val mWidth = bmpWidth / 100
            val scale = _width / mWidth
            bmpWidth *= scale / 100
            bmpHeight *= scale / 100
        } else if (bmpHeight > _height) {
            // 원하는 높이보다 클 경우의 설정
            val mHeight = bmpHeight / 100
            val scale = _height / mHeight
            bmpWidth *= scale / 100
            bmpHeight *= scale / 100
        }
        val resizedBmp = Bitmap.createScaledBitmap(
                bitmap,
                bmpWidth.toInt(), bmpHeight.toInt(), true
        )
        return resizedBmp
    }
}