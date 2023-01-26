package kr.co.smartbank.app.solution.everspin.secureKeypad.util

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import androidx.core.content.ContextCompat
import kr.co.everspin.eversafe.keypad.widget.params.ESKeyViewInfo
import kr.co.everspin.eversafe.keypad.widget.params.ESKeypadAppearanceManageable
import kr.co.everspin.eversafe.keypad.widget.params.ESSpecialKeyTypes
import kr.co.smartbank.app.R

class CustomNumberAppearance(val context: Context) : ESKeypadAppearanceManageable {

    override fun getKeypadBackground(): Drawable {
        return ColorDrawable(Color.DKGRAY)
    }

    override fun getCustomImageForNullKeys(): Drawable? {
        return ContextCompat.getDrawable(context,R.drawable.es_bc_skm_b)
    }

    override fun getCustomForSpecialKey(type: ESSpecialKeyTypes): ESKeyViewInfo {
        var viewInfo = ESKeyViewInfo()
        when (type) {
            ESSpecialKeyTypes.BackSpace -> viewInfo.forgroundNormal = ContextCompat.getDrawable(context,R.drawable.es_bc_skm_bs2)
            ESSpecialKeyTypes.Done -> viewInfo.forgroundNormal = ContextCompat.getDrawable(context,R.drawable.es_bc_skm_ic2)
            ESSpecialKeyTypes.Shuffle -> viewInfo.forgroundNormal = ContextCompat.getDrawable(context,R.drawable.es_bc_skm_ckba2)
            else -> {}
        }
        viewInfo.backgroundNormal = ColorDrawable(Color.LTGRAY)
        viewInfo.backgroundPressed = ColorDrawable(Color.BLACK)
        return viewInfo
    }

    override fun getCustomForCharacter(character: Char): ESKeyViewInfo {
        var viewInfo = ESKeyViewInfo()
        if ('0' <= character && character <= '9') {
            viewInfo = ESKeyViewInfo()
            viewInfo.forgroundNormal = ContextCompat.getDrawable(context,customNumResIdMap[character.toInt()])
            viewInfo.backgroundNormal = ColorDrawable(Color.LTGRAY)
            viewInfo.backgroundPressed = ColorDrawable(Color.BLACK)
        }
        return viewInfo
    }

    companion object {
        private val customNumResIdMap = IntArray(128)

        init {
            customNumResIdMap['0'.toInt()] = R.drawable.es_bc_skm_10
            customNumResIdMap['1'.toInt()] = R.drawable.es_bc_skm_1
            customNumResIdMap['2'.toInt()] = R.drawable.es_bc_skm_2
            customNumResIdMap['3'.toInt()] = R.drawable.es_bc_skm_3
            customNumResIdMap['4'.toInt()] = R.drawable.es_bc_skm_4
            customNumResIdMap['5'.toInt()] = R.drawable.es_bc_skm_5
            customNumResIdMap['6'.toInt()] = R.drawable.es_bc_skm_6
            customNumResIdMap['7'.toInt()] = R.drawable.es_bc_skm_7
            customNumResIdMap['8'.toInt()] = R.drawable.es_bc_skm_8
            customNumResIdMap['9'.toInt()] = R.drawable.es_bc_skm_9
        }
    }
}