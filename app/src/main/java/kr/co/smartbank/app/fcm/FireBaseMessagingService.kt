package kr.co.smartbank.app.fcm


import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.media.RingtoneManager
import android.os.Build
import android.os.PowerManager
import androidx.core.app.NotificationCompat
import com.bumptech.glide.Glide
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kr.co.smartbank.app.R
import kr.co.smartbank.app.config.Constants
import kr.co.smartbank.app.process.DBHelper
import kr.co.smartbank.app.util.Logcat
import kr.co.smartbank.app.util.SharedPreferenceHelper
import kr.co.smartbank.app.view.PushActivity
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*


class FireBaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(token)
    }

    private fun sendRegistrationToServer(token: String?) {
        // 푸시 토큰이 갱신되었을 때 내부 데이터도 갱신한다.
        val sp = SharedPreferenceHelper(this)
        sp.put(SharedPreferenceHelper.PUSH_KEY, token!!)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {

        if (remoteMessage.data.isNotEmpty()) {
            if (true) {

            } else {
                handleNow()
            }
        }
        //푸시울렸을때 화면깨우기.

        val pm = getSystemService(POWER_SERVICE) as PowerManager
        @SuppressLint("InvalidWakeLockTag")
        val wakeLock = pm.newWakeLock(
            PowerManager.SCREEN_DIM_WAKE_LOCK or PowerManager.ACQUIRE_CAUSES_WAKEUP,
            "TAG"
        )
        wakeLock.acquire(3000)

        if (remoteMessage.getNotification() != null) {
            val title=remoteMessage.getNotification()!!.title
            val body=remoteMessage.getNotification()!!.body
            Logcat.d( "Message Notification title: " + title);
            Logcat.d( "Message Notification Body: " + body);
            sendNotification(remoteMessage)

            if (remoteMessage.data["pushImgUrl"] == null || remoteMessage.data["pushImgUrl"] == "") {
                pushDbInsert(title!!,body!!,"")
            }else{
                Logcat.d( "Message getNotification data: " + remoteMessage.data);
                val pushImgUrl = remoteMessage.data["pushImgUrl"]
                pushDbInsert(title!!,body!!,pushImgUrl!!)
            }

        }

    }

    private fun handleNow() {
    }

    private fun pushDbInsert(title:String,body:String,image:String){

        var dbHelper : DBHelper = DBHelper(this, "newdb.db", null, 1)
        var database : SQLiteDatabase = dbHelper.writableDatabase
        var contentValues = ContentValues()
        contentValues.put("title",title)
        contentValues.put("body",body)
        contentValues.put("image",image)

        val long_now = System.currentTimeMillis()
        // 현재 시간을 Date 타입으로 변환
        val t_date = Date(long_now)
        // 날짜, 시간을 가져오고 싶은 형태 선언
        val t_dateFormat = SimpleDateFormat("yyyyMMddkkmmss", Locale("ko", "KR"))
        // 현재 시간을 dateFormat 에 선언한 형태의 String 으로 변환
        val str_date = t_dateFormat.format(t_date)
        contentValues.put("date",str_date)

        Logcat.d("dbinsert : "+contentValues.toString())

        database.insert("push_hist",null,contentValues)
    }




    private fun sendNotification(remoteMessage: RemoteMessage) {

        val intent = Intent(this, PushActivity::class.java)
        intent.putExtra(Constants.PURPOSE_OF_INTENT, Constants.PURPOSE_OF_INTENT_PUSH)

        val title = remoteMessage.notification?.title
        val content = remoteMessage.notification?.body
        var pushImgUrl: String? = ""
        var pushLnkUrl: String? = ""

        Logcat.d("push content : "+remoteMessage.notification)

        try {

            pushImgUrl = remoteMessage.data["pushImgUrl"]
            pushLnkUrl = remoteMessage.data["pushLnkUrl"]

            intent.putExtra("pushImgUrl", pushImgUrl)
            intent.putExtra("pushLnkUrl", pushLnkUrl)

        } catch (e: Exception) {
            Logcat.e("에러입니다 [${e.message}]")
        }

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(
            this, System.currentTimeMillis().toInt(), intent,
            PendingIntent.FLAG_CANCEL_CURRENT
        )
        val channelId = getString(R.string.default_notification_channel_id)
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.mipmap.ic_smart_new_app_icon)
            .setContentTitle(title)
            .setContentText(content)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)

        if (pushImgUrl.isNullOrEmpty()) {
            notificationBuilder.setContentText(content)
            notificationBuilder.setStyle(NotificationCompat.BigTextStyle().bigText(content))
        } else {
            try {
                notificationBuilder.setContentText("두 손가락을 이용해 아래로 당겨주세요↓")

                val bitmap = Glide
                    .with(this)
                    .asBitmap()
                    .load(pushImgUrl)
                    .submit()
                    .get()
                notificationBuilder.setStyle(
                    NotificationCompat.BigPictureStyle().bigPicture(bitmap)
                )
            } catch (e: Exception) {
                Logcat.e("에러입니다 [${e.message}]")
                notificationBuilder.setContentText(content)
                notificationBuilder.setStyle(NotificationCompat.BigTextStyle().bigText(content))
            }
        }
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelName = getString(R.string.default_notification_channel_name)
            val channel =
                NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(channel)
        }
        notificationManager.notify(System.currentTimeMillis().toInt(), notificationBuilder.build())

    }
}