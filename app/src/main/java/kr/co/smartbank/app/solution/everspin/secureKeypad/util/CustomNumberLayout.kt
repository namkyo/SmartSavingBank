package kr.co.smartbank.app.solution.everspin.secureKeypad.util

import kr.co.everspin.eversafe.keypad.widget.params.ESKeypadLayoutManageable
import kr.co.everspin.eversafe.keypad.widget.params.ESSpecialKeyTypes

class CustomNumberLayout : ESKeypadLayoutManageable() {
    init {
        // setSpecialKeyAt - (type, x, y, len)
        super.setSpecialKeyAt(ESSpecialKeyTypes.Done, 5, 0, 1)
        super.setSpecialKeyAt(ESSpecialKeyTypes.BackSpace, 5, 1, 1)
        super.setSpecialKeyAt(ESSpecialKeyTypes.Shuffle, 5, 2, 1)
    }
}