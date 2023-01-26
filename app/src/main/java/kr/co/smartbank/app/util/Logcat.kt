package kr.co.smartbank.app.util

import android.os.Environment
import android.util.Log
import kr.co.smartbank.app.BuildConfig
import java.io.*
import java.text.SimpleDateFormat
import java.util.*


class Logcat {
    companion object {
         const val TAG: String = "SmartLog"

        fun d(message: String) {
            if (BuildConfig.DEBUG_MODE) Log.d(TAG, buildLogMessage(message))
        }


        fun e(message: String) {
            if (BuildConfig.DEBUG_MODE) Log.e(TAG, buildLogMessage(message))
        }


        fun i(message: String) {
            if (BuildConfig.DEBUG_MODE) Log.i(TAG, buildLogMessage(message))
        }


        fun w(message: String) {
            if (BuildConfig.DEBUG_MODE) Log.w(TAG, buildLogMessage(message))
        }


        fun v(message: String) {
            if (BuildConfig.DEBUG_MODE) Log.v(TAG, buildLogMessage(message))
        }


        private fun buildLogMessage(message: String): String {
            val ste = Thread.currentThread().stackTrace[4]
            val sb = StringBuilder()
            sb.append("[")
            sb.append(ste.fileName)
            sb.append("] ")
            sb.append(ste.methodName)
            sb.append(" #")
            sb.append(ste.lineNumber)
            sb.append(": ")
            sb.append(message)
            // writeLog(sb.toString())
            return sb.toString()
        }

        private fun writeLog(str: String) {
            var result = 0
            val dirPath = Environment.getExternalStorageDirectory().absolutePath + "/log/smartSavingBank/"
            val file = File(dirPath)
            if (!file.exists())
                file.mkdirs()
            val nowDate =
                SimpleDateFormat("yyyy-MM-dd").format(Date(System.currentTimeMillis()))
            val savefile = File("$dirPath$nowDate.txt")
            try {
                val nowTime =
                    SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date(System.currentTimeMillis()))

                val bfw = BufferedWriter(FileWriter("$dirPath$nowDate.txt", true))
                bfw.write("++ Time: $nowTime\n")
                bfw.write(str)
                bfw.write("\n")
                bfw.flush()
                bfw.close()
            } catch (e: FileNotFoundException) {
                result = 1
                println("문제가 발생하였습니다.")
            } catch (e: IOException) {
                result = 1
                println("문제가 발생하였습니다.")
            }
        }
        @JvmStatic
        fun dd(message: String) {
            if (BuildConfig.DEBUG_MODE) Log.d(TAG, buildLogMessage(message))
        }
        @JvmStatic
        fun ee(message: String) {
            if (BuildConfig.DEBUG_MODE) Log.e(TAG, buildLogMessage(message))
        }
    }
}