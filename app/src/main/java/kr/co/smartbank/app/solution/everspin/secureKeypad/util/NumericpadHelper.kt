package kr.co.everspin.eversafe.eversafenumericpadexample.keypad

import android.content.res.Resources
import android.util.Log
import kr.co.everspin.eversafe.keypad.params.ESEditTextParams
import kr.co.everspin.eversafe.keypad.params.ESKeypadParams
import kr.co.everspin.eversafe.keypad.widget.ESEditText
import kr.co.smartbank.app.solution.everspin.secureKeypad.util.CustomQwertyAppearance
import kr.co.smartbank.app.solution.everspin.secureKeypad.util.CustomQwertyLayout
import kr.co.smartbank.app.util.CryptoUtil
import kr.co.smartbank.app.util.Logcat

class NumericpadHelper (
    val eset: ESEditText,
    val doneCallback: NumericpadCallback,
    val maxNum: Int,
    val key: String,
    val type: Int
){
    private val TAG = NumericpadHelper::class.java.simpleName

    interface NumericpadCallback{
        fun onChanged(count: Int)
        fun onDone(data: String,count: Int)
    }

    val mListener = object: ESEditText.OnKeypadListener{
        override fun secureKeypadDone(p0: ESEditText?) {
            Log.d(TAG, "secureKeypadDone")
            val mResult = p0!!.secureKeypadResult
            val numChar = p0!!.secureKeypadResult.enteredCharacters

            //closeKeypad()
            Logcat.d("보안키패드확인")
            if("".equals(key)) {
                CryptoUtil.getInstace().encrypt(mResult.plainText)?.let{ doneCallback.onDone(CryptoUtil.getInstace().encrypt(mResult.plainText),numChar) }
            }else{
                mResult.encryptedString?.let{ doneCallback.onDone(mResult.encryptedString,numChar) }
            }
        }

        override fun secureKeypadTextLengthChanged(p0: ESEditText?) {
            Log.d(TAG, "secureKeypadChanged")
            val numChar = p0!!.secureKeypadResult.enteredCharacters
            Log.d(TAG, "secureKeypadChanged $numChar")
            doneCallback.onChanged(numChar)
            if(numChar == maxNum && 1==type){
                secureKeypadDone(p0!!)
            }
        }

        override fun secureKeypadCancel() {
            Log.d(TAG, "secureKeypadCancel")
            closeKeypad()
        }
    }

    fun buildKeypad(resources: Resources){
        //넘버패드
        if(1==type){
            if("".equals(key)) {
                val builder = ESEditTextParams.Builder(eset.params)
                        .setOnKeypadListener(mListener)
                        .setKeypadType(ESKeypadParams.KeypadType.e_numericpad)
                        .setKeypadAppearanceManager(CustomAppearance(resources))
                        .setButtonPadding(1)
                        .setEncryptMethodWithKey(ESKeypadParams.EncryptMethod.e_seed, "MDEyMzQ1Njc4OUFCQ0RFRg==")
                        .setMaxInputLength(maxNum)
                eset.init(builder.build())
            }else{
                val builder = ESEditTextParams.Builder(eset.params)
                        .setOnKeypadListener(mListener)
                        .setKeypadType(ESKeypadParams.KeypadType.e_numericpad)
                        .setKeypadAppearanceManager(CustomAppearance(resources))
                        .setButtonPadding(1)
                        .setEncryptMethodWithKey(ESKeypadParams.EncryptMethod.e_rsa, key)
                        .setMaxInputLength(maxNum)
                eset.init(builder.build())
            }
        }
        //쿼티패드
        else if(2==type){
            if("".equals(key)) {
                val builder = ESEditTextParams.Builder(eset.params)
                        .setOnKeypadListener(mListener)
                        .setKeypadType(ESKeypadParams.KeypadType.e_qwerty)
                        .setKeypadAppearanceManager(CustomQwertyAppearance(resources))
                        .setKeypadLayoutManager(CustomQwertyLayout())
                        .setButtonPadding(1)
                        .setEncryptMethodWithKey(ESKeypadParams.EncryptMethod.e_seed, "MDEyMzQ1Njc4OUFCQ0RFRg==")
                        .setMaxInputLength(maxNum)
                eset.init(builder.build())
            }else{
                val builder = ESEditTextParams.Builder(eset.params)
                        .setOnKeypadListener(mListener)
                        .setKeypadType(ESKeypadParams.KeypadType.e_qwerty)
                        .setKeypadAppearanceManager(CustomQwertyAppearance(resources))
                        .setKeypadLayoutManager(CustomQwertyLayout())
                        .setButtonPadding(1)
                        .setEncryptMethodWithKey(ESKeypadParams.EncryptMethod.e_rsa, key)
                        .setMaxInputLength(maxNum)
                eset.init(builder.build())
            }
        }



    }

    private var isKeypadActive = false

    fun startKeypad(){
        if(!isKeypadActive){
            isKeypadActive = true
            eset.hideSecureKeypad()
            eset.showSecureKeypad()
            eset.clear()
        }
    }

    fun closeKeypad(){
        if(isKeypadActive){
           // isKeypadActive = false
           // eset.hideSecureKeypad()
        }
    }
}