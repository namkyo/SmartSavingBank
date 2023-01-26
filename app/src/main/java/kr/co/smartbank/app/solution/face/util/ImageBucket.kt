package kr.co.smartbank.app.solution.face.util

import android.graphics.Bitmap
import java.util.*

object ImageBucket {

    private var mBitmapList: ArrayList<Bitmap>? = null
    private var mCapacity = 0

    private fun ImageBucket() {
        mBitmapList = ArrayList()
        mCapacity = 1
    }

    fun init(capacity: Int) {
        mCapacity = capacity
        clean()
    }

    fun addImage(bitmap: Bitmap) {
        if (!isFull()) {
            mBitmapList!!.add(bitmap)
        }
    }

    fun removeImage(index: Int) {
        val bitmap = mBitmapList!!.removeAt(index)
        recycleBitmap(bitmap)
    }

    fun setImage(index: Int, bitmap: Bitmap) {
        mBitmapList!![index] = bitmap
    }

    fun getImage(index: Int): Bitmap? {
        return mBitmapList!![index]
    }

    fun getImageList(): ArrayList<Bitmap>? {
        return mBitmapList
    }

    fun getImageSize(): Int {
        return mBitmapList!!.size
    }

    fun isFull(): Boolean {
        return mBitmapList!!.size == mCapacity
    }

    fun copyImage(index: Int): Bitmap? {
        val bitmap = mBitmapList!![index]
        return if (bitmap != null) {
            bitmap.copy(bitmap.config, false)
        } else null
    }

    fun copyImageList(): ArrayList<Bitmap?>? {
        val bitmapList = ArrayList<Bitmap?>()
        val imageSize = getImageSize()
        for (i in 0 until imageSize) {
            bitmapList.add(copyImage(i))
        }
        return bitmapList
    }

    fun clean() {
        for (i in mBitmapList!!.indices) {
            val bitmap = mBitmapList!!.removeAt(i)
            recycleBitmap(bitmap)
        }
        mBitmapList!!.clear()
    }

    private fun recycleBitmap(bitmap: Bitmap) {
        var bitmap: Bitmap? = bitmap
        if (bitmap != null) {
            bitmap.recycle()
            bitmap = null
        }
    }
}