package com.dx.coroutineretrofit.lib

import com.dx.coroutineretrofit.lib.adapter.CoroutinesAdapterFactory
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitService {

    fun createCoroutinesRetrofit(baseUrl: String, isDebug: Boolean, headers: Map<String,String>? = null): Retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(CoroutinesAdapterFactory.create())
            .client(createHttpClient(isDebug, headers))
            .build()

    private fun createHttpClient(isDebug: Boolean, headers: Map<String,String>? = null): OkHttpClient {
        val httpClientBuilder = OkHttpClient.Builder()

        /**
         * 添加log拦截，用来在logcat中查看log
         */
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = if(isDebug) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
        httpClientBuilder.addInterceptor(loggingInterceptor)

        /**
         * 设置header
         */
        httpClientBuilder.addInterceptor(Interceptor { chain ->
            if (headers == null) {
                return@Interceptor chain.proceed(chain.request())
            }
            var request = chain.request()
            val builder = request.newBuilder()
            headers.forEach {
                builder.addHeader(it.key, it.value)
            }
            request = builder.build()
            chain.proceed(request)
        })

        return httpClientBuilder.build()
    }

}