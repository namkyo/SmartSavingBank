package kr.co.smartbank.app.solution.everspin.secureKeypad.util

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import kr.co.everspin.eversafe.keypad.params.ESKeypadParams
import kr.co.everspin.eversafe.keypad.widget.params.ESDefaultKeypadMagAppearanceManager
import kr.co.everspin.eversafe.keypad.widget.params.ESKeypadMagAppearanceManageable

class CustomMagAppearance(context: Context?) : ESDefaultKeypadMagAppearanceManager(context), ESKeypadMagAppearanceManageable {
    override fun getMagBackground(alignment: Int): Drawable {
        return when (alignment) {
            ESKeypadParams.KEYMAG_ALIGN_LEFT, ESKeypadParams.KEYMAG_ALIGN_RIGHT -> ColorDrawable(Color.GREEN)
            ESKeypadParams.KEYMAG_ALIGN_CENTER -> ColorDrawable(Color.BLUE)
            else -> ColorDrawable(Color.BLUE)
        }
    }

    override fun getMagForgroundForCharacter(character: Char): Drawable {
        return super.getMagForgroundForCharacter(character)
    }
}
