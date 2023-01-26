package kr.co.smartbank.app.service

import kr.co.smartbank.app.config.Constants
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.security.SecureRandom
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager


object ServiceGenerator {
    private val httpClient = OkHttpClient.Builder()
    private val builder: Retrofit.Builder = Retrofit.Builder()
            //.baseUrl(Constants.WEB_MAIN_D)
            .addConverterFactory(GsonConverterFactory.create())

    fun <S> createService(serviceClass: Class<S>?): S {

        when (Constants.MODE){
            Constants.MODE_H -> {
                builder.baseUrl(Constants.WEB_MAIN_H)
            }
            Constants.MODE_D -> {
                builder.baseUrl(Constants.WEB_MAIN_D)
            }
            Constants.MODE_R -> {
                builder.baseUrl(Constants.WEB_MAIN_R)
            }
        }

        val retrofit = builder.client( // todo: ssl 인증서 우회 시 getUnsafeOkHttpClient()
                //unsafeOkHttpClient.addInterceptor { chain ->
                httpClient.addInterceptor { chain ->
                    val original = chain.request()
                    val request = original.newBuilder()
                            .method(original.method(), original.body())
                            .build()
                    chain.proceed(request)
                } //                        .hostnameVerifier(new HostnameVerifier() {
                        //                            @Override
                        //                            public boolean verify(String hostname, SSLSession session) {
                        //                                if(hostname.startsWith(Constants.WEB_MAIN))
                        //                                    return true;
                        //                                else
                        //                                    return false;
                        //                            }
                        //                        })
                        // .addInterceptor(new AddCookiesInterceptor(context))
                        // .addInterceptor(new ReceivedCookiesInterceptor(context))
                        .connectTimeout(2, TimeUnit.MINUTES)
                        .readTimeout(2, TimeUnit.MINUTES)
                        .writeTimeout(2, TimeUnit.MINUTES)
                        .build()
        ).build()
        return retrofit.create(serviceClass)
    }

    // Create a trust manager that does not validate certificate chains
    val unsafeOkHttpClient: OkHttpClient

    // Install the all-trusting trust manager

    // Create an ssl socket factory with our all-trusting manager
    .Builder
        get() = try {
            // Create a trust manager that does not validate certificate chains
            val trustAllCerts = arrayOf<TrustManager>(
                    object : X509TrustManager {
                        @Throws(CertificateException::class)
                        override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {
                        }

                        @Throws(CertificateException::class)
                        override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {
                        }

                        override fun getAcceptedIssuers(): Array<X509Certificate> {
                            return arrayOf()
                        }
                    }
            )

            // Install the all-trusting trust manager
            val sslContext = SSLContext.getInstance("SSL")
            sslContext.init(null, trustAllCerts, SecureRandom())

            // Create an ssl socket factory with our all-trusting manager
            val sslSocketFactory = sslContext.socketFactory
            val builder = OkHttpClient.Builder()
            builder.sslSocketFactory(sslSocketFactory, trustAllCerts[0] as X509TrustManager)
            builder.hostnameVerifier { _, _ -> true }
            builder
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
}
