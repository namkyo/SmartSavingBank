package kr.co.smartbank.app.util

import android.app.Activity
import android.content.Context
import kr.co.smartbank.app.BuildConfig
import java.util.*

/**
 * Created by June on 2016-01-14.
 */
class SharedPreferenceHelper(private val mContext: Context) {
    private val PREF_NAME = BuildConfig.APPLICATION_ID

    fun put(key: String, value: String) {
        val pref = mContext.getSharedPreferences(
            PREF_NAME,
            Activity.MODE_PRIVATE
        )
        val editor = pref.edit()
        editor.putString(key, value)
        editor.commit()
    }

    fun put(key: String, value: Boolean) {
        val pref = mContext.getSharedPreferences(
            PREF_NAME,
            Activity.MODE_PRIVATE
        )
        val editor = pref.edit()
        editor.putBoolean(key, value)
        editor.commit()
    }

    fun put(key: String, value: Int) {
        val pref = mContext.getSharedPreferences(
            PREF_NAME,
            Activity.MODE_PRIVATE
        )
        val editor = pref.edit()

        editor.putInt(key, value)
        editor.commit()
    }

    fun put(key: String, value: HashSet<String>) {
        val pref = mContext.getSharedPreferences(
            PREF_NAME,
            Activity.MODE_PRIVATE
        )
        val editor = pref.edit()

        editor.putStringSet(key, value)
        editor.commit()
    }

    fun getValue(key: String, dftValue: String): String? {
        try {
            val pref = mContext.getSharedPreferences(PREF_NAME, Activity.MODE_PRIVATE)
            return pref.getString(key, dftValue)
        } catch (e: NullPointerException) {
            Logcat.e("에러입니다 [${e.message}]")
            return dftValue
        } catch (e: Exception) {
            Logcat.e("에러입니다 [${e.message}]")
            return dftValue
        }

    }

    fun getValue(key: String, dftValue: Int): Int {
        try {
            val pref = mContext.getSharedPreferences(PREF_NAME, Activity.MODE_PRIVATE)
            return pref.getInt(key, dftValue)
        } catch (e: NullPointerException) {
            Logcat.e("에러입니다 [${e.message}]")
            return dftValue
        } catch (e: Exception) {
            Logcat.e("에러입니다 [${e.message}]")
            return dftValue
        }

    }

    fun getValue(key: String, dftValue: Boolean): Boolean {
        try {
            val pref = mContext.getSharedPreferences(PREF_NAME, Activity.MODE_PRIVATE)
            return pref.getBoolean(key, dftValue)
        } catch (e: NullPointerException) {
            Logcat.e("에러입니다 [${e.message}]")
            return dftValue
        } catch (e: Exception) {
            Logcat.e("에러입니다 [${e.message}]")
            return dftValue
        }

    }

    fun getValue(key: String, dftValue: HashSet<String>): Set<String>? {
        try {
            val pref = mContext.getSharedPreferences(PREF_NAME, Activity.MODE_PRIVATE)
            return pref.getStringSet(key, dftValue)
        } catch (e: NullPointerException) {
            Logcat.e("에러입니다 [${e.message}]")
            return dftValue
        } catch (e: Exception) {
            Logcat.e("에러입니다 [${e.message}]")
            return dftValue
        }

    }

    // 값(ALL Data) 삭제하기
    fun removeAllPreferences() {
        val pref = mContext.getSharedPreferences(
            PREF_NAME,
            Activity.MODE_PRIVATE
        )
        val editor = pref.edit()
        editor.clear()
        editor.commit()
    }

    companion object {
        // 앱 환경 변수
        val IS_FIRST_INSTALL_PERMISSION = "IS_FIRST_INSTALL_PERMISSION"

        // 고객정보 변수
        val CUST_NO = "custNo"
        val JWT = "authorization"
        val PUSH_KEY = "pushKey"
    }
}