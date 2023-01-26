package kr.co.smartbank.app.solution.everspin.everSafe

import android.os.Handler
import android.os.Looper
import android.widget.Toast
import kr.co.everspin.eversafe.EversafeHelper
import kr.co.everspin.eversafe.EversafeThreat
import kr.co.everspin.eversafe.subscriber.AbstractEversafeSubscriber
import kr.co.smartbank.app.config.Constants
import kr.co.smartbank.app.util.Logcat
import kr.co.smartbank.app.view.BaseActivity
import java.util.*
import kotlin.system.exitProcess


object EverSafeManager{
    fun run(baseActivity: BaseActivity)  {
        val additionalInfo: HashMap<String, Any> = hashMapOf(
            Pair(
                "phoneNum", false
            ) //폰번호 수집시도 여부를 결정하는 옵션으로 ture 이면 수집코드가 동작되며 그렇지 않은 경우는 수집코드는 실행되지않는다.
            //,Pair("blkdg",true) //: 디버거가 탐지되는 경우 앱 종료를 결정하는 옵션으로, true 인경우 디버거가 탐지되면 앱을 종료한다. false 인 경우는 보안 정책 설정을 따른다.
            // ,Pair("adb",true) //: 서버 보안정책이 동기화되지 않은 상태에서 하이브리드보안모드로 동작중일때 ADB 활성화 탐지 시 차단하는 옵션이며 true 설정된 경우 해당 위협이 차단된다.
        )
        Logcat.e("에버세이프 : 실행")
        //운영
        if (Constants.MODE == Constants.MODE_D || Constants.MODE == Constants.MODE_H){
            Logcat.e("에버세이프 D : ${Constants.EVERSAFE_SERVER_URL_D}")
            EversafeHelper.getInstance().initialize(
                Constants.EVERSAFE_SERVER_URL_D,
                Constants.EVERSAFE_APP_ID_D,
                additionalInfo
            )
        }
        //개발
        else if (Constants.MODE == Constants.MODE_R){
            Logcat.e("에버세이프 R : ${Constants.EVERSAFE_SERVER_URL_R}")

            EversafeHelper.getInstance().initialize(
                Constants.EVERSAFE_SERVER_URL_R,
                Constants.EVERSAFE_APP_ID_R,
                additionalInfo
            )
        }

        EversafeHelper.getInstance().setSubscriber(object : AbstractEversafeSubscriber() {
            override fun onEversafeError(errCode: String?, errMsg: String?) {
                /*
                    * 기본 보안 프로세스가 제대로 구동되지 못하고 에러가 발생함
                    * errorCode : 에러 코드
                    * "UNSPECIFIED", "NON-REGISTERED", "INITIALIZE_TIMEOUT", “MODULE_LOADING_ERROR”,
                    * "INTERNAL"
                    *
                    * errorMessage : errorCode 에 해당하는 에러메시지
                */

                Logcat.e("에버세이프 Error errCode : $errCode")
                Logcat.e("에버세이프 Error errMsg : $errMsg")

                Toast.makeText(baseActivity, "[$errCode]$errMsg", Toast.LENGTH_SHORT).show()
                Handler(Looper.getMainLooper()).postDelayed({
                    baseActivity.finishAffinity()
                    exitProcess(0)
                    //  딜레이를 준 후 시작
                }, 1000)
            }

            override fun onEversafeThreatFound(threats: ArrayList<EversafeThreat>?) {
                /*
                    * 보안차단 이벤트가 발생함
                    * Format : {"localized_description":"탐지된 항목에 대한 설명","relevant_items":[추가적인
                    정보가 있을경우 표시됨],"code":"위협코드"}
                    *t
                    * threatCode : 보안 위협코드는 threats.get(index).getCode() 호출로 취득
                    * "APP", "ADB", "WIFI", "USB", "DEBUGGER", "OS", "C-ROM", “UNKNOWN”
                    * localDescription : threatCode 에 해당하는
                    에러메시지는.get(index).getLocalizedDescription() 호출로 취득
                */
                Logcat.e("에버세이프 onEversafeThreatFound : $threats")

                threats?.let {
                    val errStr = StringBuilder()
                    errStr.append("[위협발견]")
                    for (error in it) {
                        errStr.append("$error.localizedDescription ,")
                    }
                    Logcat.e("에버세이프 ThreatFound size : $errStr")
                    println("에버세이프 ThreatFound size : $errStr")
                    Toast.makeText(baseActivity, errStr, Toast.LENGTH_SHORT).show()
                    Handler(Looper.getMainLooper()).postDelayed({
                        baseActivity.finishAffinity()
                        exitProcess(0)
                        //  딜레이를 준 후 시작
                    }, 1000)
                }
            }
        })
        Logcat.e("에버세이프 실행상태1 : ${EversafeHelper.getInstance().status}")
    }
}