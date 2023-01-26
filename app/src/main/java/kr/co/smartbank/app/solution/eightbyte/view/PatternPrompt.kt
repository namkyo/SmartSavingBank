package kr.co.smartbank.app.solution.eightbyte.view

import android.R
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isGone
import androidx.fragment.app.DialogFragment
import eightbyte.safetoken.SafetokenClientInterface
import eightbyte.safetoken.SafetokenProof
import eightbyte.safetoken.SafetokenRef
import eightbyte.safetoken.pattern.SafetokenPatternAuth
import eightbyte.safetoken.pattern.SafetokenPatternAuthCallback
import eightbyte.safetoken.pattern.SafetokenPatternAuthError
import eightbyte.safetoken.pattern.view.PatternLockView
import eightbyte.safetoken.pattern.view.listener.PatternLockViewListener
import eightbyte.safetoken.pattern.view.utils.PatternLockUtils

class PatternPrompt : DialogFragment() {
    // 패턴 상태
    enum class PatternState {
        PatternStateSign, PatternStateRemove, PatternStateEntry, PatternStateReEntry, PatternStateValidate
    }
    var helpTextView: TextView? = null
    private lateinit var close2:TextView
    var patternLockView: PatternLockView? = null
    private lateinit var patternPromptCallback: PatternPromptCallback
    // Safetoken
    var tokenClient: SafetokenClientInterface? = null
    var tokenRef: SafetokenRef? = null
    var credential: String? = null
    var rnd: String? = null
    var msg: String? = null
    // 패턴 입력 상태
    var patternState: PatternState? = null
    var firstPattern: String? = null
    fun setSafetokenClient(tokenClient: SafetokenClientInterface?): PatternPrompt {
        this.tokenClient = tokenClient
        return this
    }

    fun setSafetokenRef(tokenRef: SafetokenRef?): PatternPrompt {
        this.tokenRef = tokenRef
        return this
    }

    fun setRandomForSign(rnd: String?): PatternPrompt {
        this.rnd = rnd
        return this
    }

    fun setMsgForSign(msg: String?): PatternPrompt {
        this.msg = msg
        return this
    }

    fun setCredentialForStore(credential: String?): PatternPrompt {
        this.credential = credential
        return this
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 다이얼로그를 전체화면으로 표시
        setStyle(STYLE_NO_FRAME, R.style.Theme_Translucent)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return LayoutInflater.from(context).inflate(kr.co.smartbank.app.R.layout.fragment_pattern, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkNotNull(arguments) { "No arguments found." }
        //Set the title
        if (arguments!!.containsKey(ARG_TITLE)) {
            val txvTbTitle = view.findViewById<TextView>(kr.co.smartbank.app.R.id.patturn_at_title)
            txvTbTitle.text = arguments!!.getString(ARG_TITLE)
            txvTbTitle.isSelected = true
        } else {
            throw IllegalStateException("Title cannot be null.")
        }
        //Set state
        patternState = if (arguments!!.containsKey(ARG_PATTERN_STATE)) {
            PatternState.valueOf(arguments!!.getString(ARG_PATTERN_STATE)!!)
        } else {
            throw IllegalStateException("PatternState cannot be null.")
        }
        //Set the help text
        helpTextView = view.findViewById(kr.co.smartbank.app.R.id.patturn_at_sub_title)
        if (patternState == PatternState.PatternStateEntry) {
            setHelpText(getString(kr.co.smartbank.app.R.string.pattern_reg_hint))
        } else if (patternState == PatternState.PatternStateValidate) {
            setHelpText(getString(kr.co.smartbank.app.R.string.pattern_validate_hint))
        } else {
            setHelpText(getString(kr.co.smartbank.app.R.string.pattern_draw))
        }
        // Pattern
        patternLockView = view.findViewById(kr.co.smartbank.app.R.id.pattern_lock_view)
        patternLockView!!.setAspectRatioEnabled(true)
        patternLockView!!.setAspectRatio(PatternLockView.AspectRatio.ASPECT_RATIO_HEIGHT_BIAS)
        patternLockView!!.setViewMode(PatternLockView.PatternViewMode.CORRECT)
        patternLockView!!.setInStealthMode(false)
        patternLockView!!.setTactileFeedbackEnabled(true)
        patternLockView!!.setInputEnabled(true)
//        patternLockView!!.normalStateColor=Color.parseColor("#DBDBDB")
//        patternLockView!!.correctStateColor=Color.parseColor("#200C49")
        patternLockView!!.dotOuterCircleColor=Color.parseColor("#D2DAF6")
        patternLockView!!.setPathColor(Color.parseColor("#200C49"))
        patternLockView!!.addPatternLockListener(mPatternLockViewListener)
        //Set the negative button text
        if (arguments!!.containsKey(ARG_NEGATIVE_BUTTON_TITLE)) { //final Button negativeButton = view.findViewById(R.id.close_btn);
            //negativeButton.setText(getArguments().getString(ARG_NEGATIVE_BUTTON_TITLE));
            val ibtnRightbar = view.findViewById<ImageView>(kr.co.smartbank.app.R.id.patturn_at_btn_close)
            ibtnRightbar.setOnClickListener {
                if (patternPromptCallback != null) {
                    patternPromptCallback!!.onCancel()
                }
                //Close the dialog
                dismiss()
            }
        } else {
            throw IllegalStateException("NegativeButtonTitle cannot be null.")
        }



        close2 = view.findViewById<TextView>(kr.co.smartbank.app.R.id.patturn_at_btn_close2)
        close2.setOnClickListener{
            if (patternPromptCallback != null) {
                patternPromptCallback!!.onCancel()
            }
            //Close the dialog
            dismiss()
        }
        close2.visibility=View.GONE
    }

    override fun onStart() {
        super.onStart()
        val window = dialog!!.window ?: return
        //        //Display the dialog full width of the screen
        //        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        //        window.setLayout(getResources().getDisplayMetrics().widthPixels,
        //                WindowManager.LayoutParams.WRAP_CONTENT);
        //
        //        //Display the at the bottom of the screen
        //        WindowManager.LayoutParams wlp = window.getAttributes();
        //        wlp.gravity = Gravity.BOTTOM;
        //        wlp.windowAnimations = R.style.DialogAnimation;
        //        window.setAttributes(wlp);
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

    private val mPatternLockViewListener: PatternLockViewListener = object :
        PatternLockViewListener {
        override fun onStarted() { // 안내메시지 초기화
            setHelpText("")
        }

        override fun onProgress(progressPattern: List<PatternLockView.Dot>) {}
        override fun onComplete(pattern: List<PatternLockView.Dot>) {
            if (pattern.size < 4) { // 안내 메시지 표시
                setHelpText(getString(kr.co.smartbank.app.R.string.pattern_not_enuough))
                return
            }
            if (patternState == PatternState.PatternStateEntry) { // 패턴 임시 저장
                firstPattern = PatternLockUtils.patternToSha1(patternLockView, pattern)
                patternLockView!!.clearPattern()
                // 패턴 입력 상태 변경
                patternState = PatternState.PatternStateReEntry
                // 안내 메시지 표시
                setHelpText(getString(kr.co.smartbank.app.R.string.pattern_reg_re_hint))
            } else if (patternState == PatternState.PatternStateReEntry) { // 이전에 입력한 패턴과 비교
                val secondPattern = PatternLockUtils.patternToSha1(patternLockView, pattern)
                if (firstPattern.equals(secondPattern, ignoreCase = true)) { // 안내 메시지 표시
                    setHelpText(getString(kr.co.smartbank.app.R.string.success_pattern))
                    // 패턴 초기화
                    patternLockView!!.clearPattern()
                    // 패턴 인증 정보 저장
                    // 토큰에 패턴인증 정보 저장
                    val patternAuth = SafetokenPatternAuth(activity)
                    patternAuth.storeCredential(tokenClient, tokenRef, credential, secondPattern, object : SafetokenPatternAuthCallback() {
                        override fun onStoreCredential() {
                            patternPromptCallback!!.onStoreCredential()
                        }

                        override fun onError(code: Int, errMsg: String, count: Int) {
                            patternPromptCallback!!.onError(code, -1, -1)
                        }
                    })
                } else { // 두번째 패턴과 맞지 않음. 처음부터 다시 입력
                    setErrorText(getString(kr.co.smartbank.app.R.string.error_pattern_reg_re_hint))
                    // 패턴 입력 상태 변경
                    patternState = PatternState.PatternStateEntry
                    firstPattern = null
                    patternLockView!!.clearPattern()
                }
            } else if (patternState == PatternState.PatternStateValidate) { // 패턴 확인
                val inputPattern = PatternLockUtils.patternToSha1(patternLockView, pattern)
                val patternAuth = SafetokenPatternAuth(activity)
                patternAuth.validatePattern(tokenClient, tokenRef, inputPattern, object : SafetokenPatternAuthCallback() {
                    override fun onValidatePattern(pattern: String) { // 패턴 확인 성공
                        credential = pattern
                        // 패턴 입력 상태 변경
                        patternState = PatternState.PatternStateEntry
                        // 패턴 초기화
                        patternLockView!!.clearPattern()
                        // 패턴 재입력 메시지 표시
                        setHelpText(getString(kr.co.smartbank.app.R.string.pattern_reg_hint))
                    }

                    override fun onError(code: Int, errMsg: String, count: Int) { // 패턴 초기화
                        patternLockView!!.clearPattern()
                        if (count >= PATTERN_MAX_FAIL_COUNT) { // 오류 횟수 초과
                            setErrorText(getString(kr.co.smartbank.app.R.string.error_exceed_pattern_fail))
                            patternPromptCallback!!.onExceedFail(count, PATTERN_MAX_FAIL_COUNT)
                        } else {
                            val errText =
                                String.format("%s(%d/%d)", getString(kr.co.smartbank.app.R.string.error_pattern), count, PATTERN_MAX_FAIL_COUNT)
                            setErrorText(errText)
                            // 오류 콜백
                            patternPromptCallback!!.onError(SafetokenPatternAuthError.ERROR_AUTHENTICATION, count, PATTERN_MAX_FAIL_COUNT)
                        }
                    }
                })
            } else if (patternState == PatternState.PatternStateSign) { // 전자 서명
                val inputPattern = PatternLockUtils.patternToSha1(patternLockView, pattern)
                val patternAuth = SafetokenPatternAuth(activity)
                patternAuth.generateSign(tokenClient,
                    tokenRef,
                    rnd,
                    msg!!.toByteArray(),
                    inputPattern,
                    false,
                    object : SafetokenPatternAuthCallback() {
                        override fun onGenerateSign(tnp: SafetokenProof) { // 전자 서명 성공
                            // 안내 메시지 표시
                            setHelpText(getString(kr.co.smartbank.app.R.string.success_pattern))
                            // 패턴 초기화
                            patternLockView!!.clearPattern()
                            // 콜백 호출
                            patternPromptCallback!!.onGenerateSign(tnp, msg)
                        }

                        // generateSign 호출시 failCheck 를 false 로 설정하면 onError 는 호출되지 않는다.
                        override fun onError(code: Int, errMsg: String, count: Int) { // 패턴 초기화
                            patternLockView!!.clearPattern()
                            // 전자 서명 실패
                            if (code == SafetokenPatternAuthError.ERROR_AUTHENTICATION) {
                                if (count >= PATTERN_MAX_FAIL_COUNT) { // 오류 횟수 초과
                                    setErrorText(getString(kr.co.smartbank.app.R.string.error_exceed_pattern_fail))
                                    patternPromptCallback!!.onExceedFail(count, PATTERN_MAX_FAIL_COUNT)
                                } else {
                                    val errText =
                                        String.format("%s(%d/%d)", getString(kr.co.smartbank.app.R.string.error_pattern), count, PATTERN_MAX_FAIL_COUNT)
                                    setErrorText(errText)
                                    // 오류 콜백
                                    patternPromptCallback!!.onError(SafetokenPatternAuthError.ERROR_AUTHENTICATION, count, PATTERN_MAX_FAIL_COUNT)
                                }
                            } else {
                                patternPromptCallback!!.onError(code, -1, -1)
                            }
                        }
                    })
            } else if (patternState == PatternState.PatternStateRemove) { // 패턴 확인
                val inputPattern = PatternLockUtils.patternToSha1(patternLockView, pattern)
                val patternAuth = SafetokenPatternAuth(activity)
                patternAuth.removeCredential(tokenClient, tokenRef, inputPattern, object : SafetokenPatternAuthCallback() {
                    override fun onRemoveCredential() { // 패턴 초기화
                        patternLockView!!.clearPattern()
                        // 콜백 호출
                        patternPromptCallback!!.onRemoveCredential()
                    }

                    override fun onError(code: Int, errMsg: String, count: Int) { // 패턴 초기화
                        patternLockView!!.clearPattern()
                        // 패턴 인증 삭제 실패
                        if (code == SafetokenPatternAuthError.ERROR_AUTHENTICATION) {
                            if (count >= PATTERN_MAX_FAIL_COUNT) { // 오류 횟수 초과
                                setErrorText(getString(kr.co.smartbank.app.R.string.error_exceed_pattern_fail))
                                patternPromptCallback!!.onExceedFail(count, PATTERN_MAX_FAIL_COUNT)
                            } else {
                                val errText =
                                    String.format("%s(%d/%d)", getString(kr.co.smartbank.app.R.string.error_pattern), count, PATTERN_MAX_FAIL_COUNT)
                                setErrorText(errText)
                                // 오류 콜백
                                patternPromptCallback!!.onError(SafetokenPatternAuthError.ERROR_AUTHENTICATION, count, PATTERN_MAX_FAIL_COUNT)
                            }
                        } else {
                            patternPromptCallback!!.onError(code, -1, -1)
                        }
                    }
                })
            }
        }

        override fun onCleared() {}
    }

    fun setPatternPromptCallback(callback: PatternPromptCallback) {
        patternPromptCallback = callback
    }

    private fun setHelpText(helpText: String) {
        helpTextView!!.setTextColor(resources.getColor(kr.co.smartbank.app.R.color.textBlack_363636))
        helpTextView!!.text = helpText
    }

    private fun setErrorText(errorText: String) {
        helpTextView!!.setTextColor(Color.RED)
        helpTextView!!.text = errorText
    }

    companion object {
        const val PATTERN_MAX_FAIL_COUNT = 5
        // Keys of the arguments.
        private const val ARG_TITLE = "arg_title"
        private const val ARG_NEGATIVE_BUTTON_TITLE = "arg_negative_button_title"
        private const val ARG_PATTERN_STATE = "arg_pattern_state"
        private fun createDialog(title: String, negativeButtonTitle: String, state: PatternState): PatternPrompt {
            val patternDialog = PatternPrompt()
            //Set the arguments
            val bundle = Bundle()
            bundle.putString(ARG_TITLE, title)
            bundle.putString(ARG_NEGATIVE_BUTTON_TITLE, negativeButtonTitle)
            bundle.putString(ARG_PATTERN_STATE, state.name)
            patternDialog.arguments = bundle
            return patternDialog
        }

        // 패턴 인증 저장 다이얼로그 생성
        fun createStoreCredentialDialog(title: String, negativeButtonTitle: String): PatternPrompt {
            return createDialog(title, negativeButtonTitle, PatternState.PatternStateEntry)
        }

        // 전자서명 다이얼로그 생성
        fun createGenerateSignDialog(title: String, negativeButtonTitle: String): PatternPrompt {
            return createDialog(title, negativeButtonTitle, PatternState.PatternStateSign)
        }

        // 패턴 변경 다이얼로그 생성
        fun createChangePatternDialog(title: String, negativeButtonTitle: String): PatternPrompt {
            return createDialog(title, negativeButtonTitle, PatternState.PatternStateValidate)
        }

        // 패턴 삭제 다이얼로그 생성
        fun createRemovePatternDialog(title: String, negativeButtonTitle: String): PatternPrompt {
            return createDialog(title, negativeButtonTitle, PatternState.PatternStateRemove)
        }

    }
}