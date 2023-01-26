package kr.co.smartbank.app.solution.everspin.secureKeypad.util

import kr.co.everspin.eversafe.keypad.widget.params.ESKeypadLayoutManageable
import kr.co.everspin.eversafe.keypad.widget.params.ESSpecialKeyTypes

class CustomQwertyLayout : ESKeypadLayoutManageable() {
    init {
        // setSpecialKeyAt - (type, x, y, len)
        super.setSpecialKeyAt(ESSpecialKeyTypes.BackSpace, 9, 3, 2)
        super.setSpecialKeyAt(ESSpecialKeyTypes.Shift, 0, 3, 1)
        super.setSpecialKeyAt(ESSpecialKeyTypes.Toggle, 0, 4, 2)
        super.setSpecialKeyAt(ESSpecialKeyTypes.Space, 2, 4, 5)
        super.setSpecialKeyAt(ESSpecialKeyTypes.Shuffle, 7, 4, 2) //, false);
        super.setSpecialKeyAt(ESSpecialKeyTypes.Done, 9, 4, 2)
    }
}