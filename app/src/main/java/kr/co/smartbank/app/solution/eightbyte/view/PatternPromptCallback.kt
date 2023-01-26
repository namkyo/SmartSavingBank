package kr.co.smartbank.app.solution.eightbyte.view

import eightbyte.safetoken.SafetokenProof

abstract class PatternPromptCallback {
    open fun onStoreCredential() {}
    open fun onGenerateSign(tnp: SafetokenProof?, msg: String?) {}
    fun onRemoveCredential() {}
    open fun onError(code: Int, count: Int, max: Int) {}
    open fun onExceedFail(count: Int, max: Int) {}
    open fun onCancel() {}
}