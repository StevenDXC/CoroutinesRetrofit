@file:Suppress("NOTHING_TO_INLINE")

package com.dx.coroutineretrofit.lib.adapter

import com.dx.coroutineretrofit.lib.error.RequestError
import com.google.gson.JsonIOException
import com.google.gson.JsonParseException
import com.google.gson.JsonSyntaxException
import kotlinx.coroutines.*
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Response
import retrofit2.Retrofit
import java.io.IOException
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

internal class CoroutinesAdapterFactory: CallAdapter.Factory(){
    companion object {
        fun create() = CoroutinesAdapterFactory()
    }

    override fun get(returnType: Type, annotations: Array<out Annotation>?, retrofit: Retrofit?): CallAdapter<*, *>? {
        if (Deferred::class.java != getRawType(returnType)) {
            return null
        }

        if (returnType !is ParameterizedType) {
            throw IllegalStateException(
                    "Deferred return type must be parameterized as Deferred<Foo> or Deferred<out Foo>")
        }

        val responseType = getParameterUpperBound(0, returnType)
        val rawDeferredType = getRawType(responseType)
        return if (rawDeferredType == Response::class.java) {
            if (responseType !is ParameterizedType) {
                throw IllegalStateException(
                        "Response must be parameterized as Response<Foo> or Response<out Foo>")
            }
            ResponseCallAdapter<Any>(getParameterUpperBound(0, responseType))
        } else {
            BodyCallAdapter<Any>(responseType)
        }
    }

    private class BodyCallAdapter<T:Any>(private val responseType: Type) : CallAdapter<T, Deferred<T>> {
        override fun responseType() = responseType

        override fun adapt(call: Call<T>): Deferred<T> {
            val deferred = CompletableDeferred<T>()
            deferred.invokeOnCompletion {
                if (deferred.isCancelled) {
                    call.cancel()
                }
            }

            GlobalScope.launch(Dispatchers.IO){
                try {
                    val response = call.execute()
                    if(response.isSuccessful){
                        deferred.complete(response.body()!!)
                    }else{
                        deferred.completeExceptionally(response.failedError())
                    }
                }catch (e: IOException){
                    deferred.completeExceptionally(e.toRequestError())
                }
            }
            return deferred
        }
    }


    private class ResponseCallAdapter<T>(private val responseType: Type) : CallAdapter<T, Deferred<Response<T>>> {
        override fun responseType() = responseType

        override fun adapt(call: Call<T>): Deferred<Response<T>> {
            val deferred = CompletableDeferred<Response<T>>()
            deferred.invokeOnCompletion {
                if (deferred.isCancelled) {
                    call.cancel()
                }
            }
            GlobalScope.launch(Dispatchers.IO){
                try {
                    val response = call.execute()
                    if(response.isSuccessful){
                        deferred.complete(response)
                    }else{
                        deferred.completeExceptionally(response.failedError())
                    }
                }catch (e: IOException){
                    deferred.completeExceptionally(e.toRequestError())
                }
            }
            return deferred
        }
    }
}
/**
 * 将请求过程中抛出的异常转换为RequestError
 */
internal inline fun Throwable.toRequestError(): RequestError {

    if(this is JsonSyntaxException
            || this is IllegalArgumentException
            || this is JsonIOException
            || this is JsonParseException){
        return RequestError(RequestError.JSON_PARSE_ERROR_CODE,message ?: "Parse json error")
    }
    return RequestError(RequestError.IO_ERROR_CODE, message ?: "io error")
}

/**
 * 根据HTTP响应码生成对应的错误
 */
internal inline fun <T> Response<T>.failedError(): RequestError = RequestError(code(), message())