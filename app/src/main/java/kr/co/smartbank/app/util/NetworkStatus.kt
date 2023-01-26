package kr.co.smartbank.app.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build

enum class NETWORK_STATUS {
    TYPE_WIFE,
    TYPE_MOBILE,
    TYPE_NOT_CONNECTED
}

class NetworkStatus{
    companion object{
        fun getConnectivityStatus(content:Context):NETWORK_STATUS{
            val manager:ConnectivityManager? = content.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            if (manager != null){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    val networkCapabilities = manager.getNetworkCapabilities(manager.activeNetwork)

                    if (networkCapabilities != null){
                        if(networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)){
                            return NETWORK_STATUS.TYPE_WIFE
                        }else if(networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)){
                            return NETWORK_STATUS.TYPE_MOBILE
                        }
                    }else {
                        val networkInfo = manager.activeNetworkInfo
                        if (networkInfo != null) {
                            val type = networkInfo.type

                            if (type==ConnectivityManager.TYPE_WIFI){
                                return NETWORK_STATUS.TYPE_WIFE
                            }else if (type==ConnectivityManager.TYPE_MOBILE){
                                return NETWORK_STATUS.TYPE_MOBILE
                            }
                        }
                    }
                }
            }
            return NETWORK_STATUS.TYPE_NOT_CONNECTED
        }
    }
}