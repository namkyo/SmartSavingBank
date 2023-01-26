package kr.co.smartbank.app.process

import android.content.Context
import com.nprotect.seculog.IxLogMessage
import kr.co.everspin.eversafe.EversafeHelper
import kr.co.everspin.eversafe.subscriber.AbstractEversafeSubscriber
import kr.co.smartbank.app.util.Logcat

object FdsHelper {

   fun getFds(applicationContext:Context,key:String) : IxLogMessage{
       val ixLogMessage = IxLogMessage(applicationContext) // 1.기기정보 수집 클래스
       ixLogMessage.setServerKey(key)//서버에서가져온 서버키
       ixLogMessage.setCheckApp("")//패키지명설정 생략가능
       return ixLogMessage
   }
}