package kr.co.smartbank.app.process

import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import kr.co.coocon.sasapi.SASRunCompletedListener
import kr.co.coocon.sasapi.SASRunStatusChangedListener
import kr.co.coocon.sasapi.util.StringUtil
import kr.co.smartbank.app.R
import kr.co.smartbank.app.config.Constants
import kr.co.smartbank.app.solution.coocon.view.InputCaptchaActivity
import kr.co.smartbank.app.util.CryptoUtil
import kr.co.smartbank.app.util.LoadingUtil
import kr.co.smartbank.app.util.Logcat
import org.json.JSONObject

class Scraping {

    fun homeScraping(commonDTO: CommonDTO, inputPw: String, rbrNo: String){
        val jparams = JSONObject(commonDTO.parmas)
        if(!jparams.isNull("HOME_1")) {
            val home1 : JSONObject = jparams.getJSONObject("HOME_1")
            home1.getJSONObject("Input").put(
                "주민사업자번호", CryptoUtil.getInstace().decrypt(
                    home1.getJSONObject("Input").get(
                        "주민사업자번호"
                    ).toString()
                )
            )
            home1.getJSONObject("Input").getJSONObject("인증서").put(
                "이름",
                commonDTO.userCert.subjectDn
            )
            home1.getJSONObject("Input").getJSONObject("인증서").put(
                "만료일자",
                commonDTO.userCert.expiredTime
            )
            home1.getJSONObject("Input").getJSONObject("인증서").put(
                "비밀번호",
                CryptoUtil.getInstace().decrypt(inputPw)
            )
            val home2 : JSONObject = jparams.getJSONObject("HOME_2")
            home2.getJSONObject("Input").put(
                "주민등록번호", CryptoUtil.getInstace().decrypt(
                    home2.getJSONObject("Input").get(
                        "주민등록번호"
                    ).toString()
                )
            )
            home2.getJSONObject("Input").getJSONObject("인증서").put(
                "이름",
                commonDTO.userCert.subjectDn
            )
            home2.getJSONObject("Input").getJSONObject("인증서").put(
                "만료일자",
                commonDTO.userCert.expiredTime
            )
            home2.getJSONObject("Input").getJSONObject("인증서").put(
                "비밀번호",
                CryptoUtil.getInstace().decrypt(inputPw)
            )
            val home3 : JSONObject = jparams.getJSONObject("HOME_3")
            val home4 : JSONObject = jparams.getJSONObject("HOME_4")
            home4.getJSONObject("Input").put("사업자등록번호", rbrNo)
            Logcat.d("home1 : $home1")
            Logcat.d("home2 : $home2")
            Logcat.d("home3 : $home3")
            Logcat.d("home4 : $home4")
            commonDTO.sasManager?.let {
                it.run(7, home1.toString())
                it.run(8, home2.toString())
                it.run(9, home3.toString())
                it.run(10, home4.toString())
            }
        }else{
            Toast.makeText(commonDTO.activity, "홈텍스 input 404", Toast.LENGTH_LONG).show()
        }
    }
    fun nhisScraping(commonDTO: CommonDTO, inputPw: String){
        val jparams = JSONObject(commonDTO.parmas)
        if(!jparams.isNull("NHIS_1")) {
            val login : JSONObject = jparams.getJSONObject("NHIS_1")

            if (TextUtils.isEmpty(login.getJSONObject("Input").getJSONObject("인증서").toString())){
                Logcat.d("건보 안태움")
                return
            }
            //주민번호 복호화
            //login.getJSONObject("Input").put("주민사업자번호",CryptoUtil.getInstace().decrypt(login.getJSONObject("Input").getJSONObject("주민사업자번호").toString()))
            //실명번호 복호화
            login.getJSONObject("Input").put(
                    "주민사업자번호", CryptoUtil.getInstace().decrypt(
                    login.getJSONObject("Input").get(
                            "주민사업자번호"
                    ).toString()
            )
            )


            login.getJSONObject("Input").getJSONObject("인증서").put(
                "이름",
                commonDTO.userCert.subjectDn
            )
            login.getJSONObject("Input").getJSONObject("인증서").put(
                "만료일자",
                commonDTO.userCert.expiredTime
            )
            login.getJSONObject("Input").getJSONObject("인증서").put(
                "비밀번호",
                CryptoUtil.getInstace().decrypt(inputPw)
            )
            val nh2 : JSONObject = jparams.getJSONObject("NHIS_2")
            val nh3 : JSONObject = jparams.getJSONObject("NHIS_3")
            Logcat.d("nh login : $login")
            Logcat.d("nh nh2 : $nh2")
            Logcat.d("nh nh3 : $nh3")

            commonDTO.sasManager?.let {
                it.run(4, login.toString())
                it.run(5, nh2.toString())
                it.run(6, nh3.toString())
            }

        }else{
            Toast.makeText(commonDTO.activity, "건강보험공단 input 404", Toast.LENGTH_LONG).show()
        }
    }
    private fun minwonScraping(
        commonDTO: CommonDTO,
        secureString: String,
        subjectDn: String,
        expiredTime: String,
        inputPw: String,
        parmas: String
    ){
       // var ds=JSONObject(jparams.get("MinWon_2").toString())
        val jparams = JSONObject(parmas)
        if(!jparams.isNull("MinWon_1")){
            val login : JSONObject = jparams.getJSONObject("MinWon_1")
            login.getJSONObject("Input").put("보안문자", secureString)
            //실명번호 복호화
            login.getJSONObject("Input").put(
                "주민등록번호", CryptoUtil.getInstace().decrypt(
                    login.getJSONObject("Input").get(
                        "주민등록번호"
                    ).toString()
                )
            )

            val chobun : JSONObject = jparams.getJSONObject("MinWon_2")
            chobun.getJSONObject("Input").put("보안문자", secureString)
            chobun.getJSONObject("Input").getJSONObject("인증서").put("이름", subjectDn)
            chobun.getJSONObject("Input").getJSONObject("인증서").put("만료일자", expiredTime)
            chobun.getJSONObject("Input").getJSONObject("인증서").put(
                "비밀번호", CryptoUtil.getInstace().decrypt(
                    inputPw
                )
            )
            val logout : JSONObject = jparams.getJSONObject("MinWon_3")
            Logcat.d("login : $login")
            Logcat.d("chobun : $chobun")
            Logcat.d("logout : $logout")

            commonDTO.sasManager?.let {
                it.run(1, login.toString())
                it.run(2, chobun.toString())
                it.run(3, logout.toString())
            }

        }else{
            Toast.makeText(commonDTO.activity, "민원 input 404", Toast.LENGTH_LONG).show()
        }
    }


    //민원24 보안문자 input
    private fun secureStr():String{
        var minwon_secure_string=JSONObject()
        minwon_secure_string.put("Module", "MinWon")
        minwon_secure_string.put("Class", "민원신청조회")
        minwon_secure_string.put("Job", "보안문자")
        var minwon_secure_string_in01= JSONObject()
        minwon_secure_string.put("Input", minwon_secure_string_in01)
        return minwon_secure_string.toString()
    }
    fun allScrapingInit(commonDTO: CommonDTO, inputPw: String){
        LoadingUtil.show(commonDTO.customProgressDialog)
//        LoadingUtil.showAndText(
//            commonDTO.customProgressDialog,
//            commonDTO.activity.getString(R.string.sc_txt_01),
//            commonDTO.activity
//        )

        val resultJson = JSONObject() //결과 셋팅


        commonDTO.sasManager?.let {
            it.run(0, secureStr())
            it.addSASRunCompletedListener(object : SASRunCompletedListener {
                override fun onSASRunCompleted(index: Int, outString: String?) {
                    val jsonObject = JSONObject(outString) //결과
                    val output = jsonObject.getJSONObject("Output") //결과2
                    val errorCode = output.optString("ErrorCode", "")
                    val errorMessage = output.optString("ErrorMessage", "")

                    Logcat.d("[scraping result] index : $index , errorCode : $errorCode , result : $jsonObject")

                    if ("8000F107".equals(errorCode, true) && index == 0) {
                        Logcat.d("인터넷 연결오류")
                        return
                    }
                    Logcat.d("=====스크랩핑결과======")
                    Logcat.d("errorCode : $errorCode")
                    Logcat.d("errorMessage : $errorMessage")
                    if ("80003391".equals(errorCode, true) && index == 1) {
                        Logcat.d("보안문자 입력 오류")
                        commonDTO.activity.runOnUiThread {
                            LoadingUtil.hide(commonDTO.customProgressDialog)
                            // 스크래핑 리스너 취소
                            it.addSASRunCompletedListener(null)
                            it.addSASRunStatusChangedListener(null)
                            //commonDTO.sasManager!!.cancel()
                            try {
                                val builder = AlertDialog.Builder(commonDTO.activity)
                                builder.setMessage("보안문자 입력이 잘못되었습니다. 새로운 보안문자를 다시 확인 후 입력해 주세요.")
                                builder.setCancelable(false)
                                builder.setPositiveButton("확인") { dialog, which ->
                                    allScrapingInit(commonDTO, inputPw)
                                }
                                builder.show()
                            } catch (e: WindowManager.BadTokenException) {
                                Logcat.e("에러입니다")
                            } catch (e: Exception) {
                                Logcat.e("에러입니다")
                            }
                        }
                    }
                    //else if ("00000000".equals(errorCode, ignoreCase = true)) {
                    else {
                        when (index) {
                            0 -> {
                                LoadingUtil.hide(commonDTO.customProgressDialog)
                                //보안문자
                                val secuStr = output.getJSONObject("Result").optString("보안문자", "")
                                Logcat.d("secuStr: $secuStr")
                                //보안문자 입력화면 이동
                                val intent = Intent(
                                        commonDTO.activity,
                                        InputCaptchaActivity::class.java
                                )
                                intent.putExtra("captchaImage", secuStr)
                                intent.putExtra("subjectDn", commonDTO.userCert.subjectDn)
                                intent.putExtra(
                                        "expiredTime", commonDTO.userCert.expiredTime.replace(
                                        ".",
                                        ""
                                ).replace(".", "")
                                )
                                intent.putExtra("inputPw", inputPw)
                                intent.putExtra(
                                        Constants.KSW_Activity,
                                        Constants.REQUEST_SCRAPING_CAPTCHA_INPUT
                                )
                                commonDTO.activity.startActivityForResult(
                                        intent,
                                        Constants.REQUEST_SCRAPING_CAPTCHA_INPUT
                                )
                                //commonDTO.//activity.//overridePendingTransition(R.anim.slide_up, R.anim.slide_bottom)
                            }
                            //민원 24 비회원 로그인
                            1 -> {
                                LoadingUtil.showAndText(
                                        commonDTO.customProgressDialog, commonDTO.activity.getString(
                                        R.string.sc_txt_03
                                ), commonDTO.activity
                                )
                                Logcat.d("MinWon_1")
                                resultJson.put("MinWon_1", jsonObject)

                                //보안문자성공 후 건강보험공단
                                nhisScraping(commonDTO, inputPw)
                            }
                            //민원 24 비회원 초본
                            2 -> {
                                LoadingUtil.showAndText(
                                        commonDTO.customProgressDialog, commonDTO.activity.getString(
                                        R.string.sc_txt_04
                                ), commonDTO.activity
                                )
                                Logcat.d("MinWon_2")
                                resultJson.put("MinWon_2", jsonObject)
                            }
                            //민원 24 비회원 로그아웃
                            3 -> {
                                LoadingUtil.showAndText(
                                        commonDTO.customProgressDialog, commonDTO.activity.getString(
                                        R.string.sc_txt_05
                                ), commonDTO.activity
                                )
                                Logcat.d("MinWon_3")
                                resultJson.put("MinWon_3", jsonObject)
                            }
                            //건강보험공단 로그인
                            4 -> {
                                LoadingUtil.showAndText(
                                        commonDTO.customProgressDialog, commonDTO.activity.getString(
                                        R.string.sc_txt_06
                                ), commonDTO.activity
                                )
                                Logcat.d("NHIS_1")
                                resultJson.put("NHIS_1", jsonObject)
                            }
                            //건강보험공단 납부내역서
                            5 -> {
                                LoadingUtil.showAndText(
                                        commonDTO.customProgressDialog, commonDTO.activity.getString(
                                        R.string.sc_txt_07
                                ), commonDTO.activity
                                )
                                Logcat.d("NHIS_2")
                                resultJson.put("NHIS_2", jsonObject)
                            }
                            //건강보험공단 자격득실확인서
                            6 -> {
                                LoadingUtil.showAndText(
                                        commonDTO.customProgressDialog, commonDTO.activity.getString(
                                        R.string.sc_txt_08
                                ), commonDTO.activity
                                )
                                Logcat.d("NHIS_3")
                                resultJson.put("NHIS_3", jsonObject)
                                if ("00000000".equals(errorCode)) {
                                    val rbrNo =
                                            jsonObject.getJSONObject("Output").getJSONObject("Result").get(
                                                    "사업장관리번호"
                                            ).toString()

                                    if (StringUtil.isNull(rbrNo)) {
                                        Logcat.d("사업자번호 미존재")
                                        homeScraping(commonDTO, inputPw, "")
//                                    Toast.makeText(commonDTO.activity,"사업자번호 미존재",Toast.LENGTH_LONG)
                                        //val sendrbrNo = rbrNo?.substring(0, rbrNo.length - 1)
                                    } else {
                                        if(rbrNo.length>10){
                                            homeScraping(commonDTO, inputPw, rbrNo.substring(0, rbrNo.length - 1))
                                        }else{
                                            homeScraping(commonDTO, inputPw, rbrNo)
                                        }
                                    }
                                } else {
                                    //사업자번호 없을때 종료
                                    Logcat.d("자격득실확인서 조회중 오류")
                                    homeScraping(commonDTO, inputPw, "")
                                    // scrapingDataSend(commonDTO,resultJson)
//                                commonDTO.activity.runOnUiThread {
//                                    Toast.makeText(commonDTO.activity,"자격득실확인서 조회중 오류",Toast.LENGTH_LONG)
//                                    scrapingDataSend(commonDTO,resultJson)
//                                }
                                }

                            }
                            7 -> {
                                LoadingUtil.showAndText(
                                        commonDTO.customProgressDialog, commonDTO.activity.getString(
                                        R.string.sc_txt_09
                                ), commonDTO.activity
                                )
                                Logcat.d("HOME_1")
                                resultJson.put("HOME_1", jsonObject)
                            }
                            8 -> {
                                LoadingUtil.showAndText(
                                        commonDTO.customProgressDialog, commonDTO.activity.getString(
                                        R.string.sc_txt_10
                                ), commonDTO.activity
                                )
                                Logcat.d("HOME_2")
                                resultJson.put("HOME_2", jsonObject)
                            }
                            9 -> {
                                LoadingUtil.showAndText(
                                        commonDTO.customProgressDialog, commonDTO.activity.getString(
                                        R.string.sc_txt_11
                                ), commonDTO.activity
                                )
                                Logcat.d("HOME_3")
                                resultJson.put("HOME_3", jsonObject)
                            }
                            10 -> {
                                LoadingUtil.showAndText(
                                        commonDTO.customProgressDialog, commonDTO.activity.getString(
                                        R.string.sc_txt_12
                                ), commonDTO.activity
                                )
                                Logcat.d("HOME_4")
                                resultJson.put("HOME_4", jsonObject)
                                scrapingDataSend(commonDTO, resultJson)
                            }
                        }
                    }
                }
            })
            it.addSASRunStatusChangedListener(object : SASRunStatusChangedListener {
                override fun onSASRunStatusChanged(action: Int, percent: Int) {
                    Logcat.d("onSASRunStatusChanged : $action , $percent");
                }
            })
        }

    }
    fun allScrapingRun(commonDTO: CommonDTO, data: Intent){
        // 입력된 코드 데이터
        val code = data!!.getStringExtra("code")!!
        val subjectDn = data!!.getStringExtra("subjectDn")!!
        val inputPw = data!!.getStringExtra("inputPw")!!
        val expiredTime = data!!.getStringExtra("expiredTime")!!

        Logcat.d("code: $code")
        Logcat.d("subjectDn: $subjectDn")
        Logcat.d("inputPw: $inputPw")
        Logcat.d("expiredTime: $expiredTime")

        //민원24
        minwonScraping(commonDTO, code, subjectDn, expiredTime, inputPw, commonDTO.parmas)
        //건강보험
        //nhisScraping(commonDTO, inputPw)
        //국세청
        //homeScraping(commonDTO,inputPw)
    }

    private fun scrapingDataSend(commonDTO: CommonDTO, resultJson: JSONObject){
        var intent = Intent()
        intent.putExtra("resultJson", resultJson.toString())
            commonDTO.activity.runOnUiThread {
                Handler(Looper.myLooper()!!).postDelayed({
                   LoadingUtil.hide(commonDTO.customProgressDialog)
                    commonDTO.customProgressDialog.dismiss()
                    Logcat.d("===runOnUiThread 스크래핑 데이터 송신===")
                    commonDTO.activity.setResult(AppCompatActivity.RESULT_OK, intent)
                    commonDTO.activity.finish()
                    //commonDTO.//activity.//overridePendingTransition(R.anim.slide_up, R.anim.slide_bottom)
                }, 4000) // 0.5초 정도 딜레이를 준 후 시작
            }

    }
}