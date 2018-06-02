@file:Suppress("NOTHING_TO_INLINE")

package com.dx.coroutineretrofit.lib.request

import com.dx.coroutineretrofit.lib.error.RequestError
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.cancelAndJoin
import java.lang.ref.WeakReference
import java.util.*


//缓存对象中执行的异步Job. 对象被回收的时候取消job,防止内存泄露
val jobHolder = WeakHashMap<String, ArrayList<WeakReference<Deferred<Unit?>>>>()

/**
 * do request
 * @param action: the request action
 */
@Suppress("unused")
inline fun Any.request(noinline action: suspend () -> Unit): AsyncRequestHandler{
    val handler = AsyncRequestHandler()
    val deferred = async(UI) {
        try {
            action.invoke()
        }catch (e: RequestError){
            e.printStackTrace()
            handler.errorHandler?.invoke(e)
        }finally {
            handler.finallyHandler?.invoke()
        }
    }
    val key = this.hashCode().toString()
    val list = jobHolder.getOrElse(key) {
        val newList = ArrayList<WeakReference<Deferred<Unit?>>>()
        jobHolder[key] = newList
        newList
    }
    list.add(WeakReference(deferred))
    return handler
}

/**
 * cancel all not complete async job
 */
@Suppress("unused")
inline fun Any.cancelAllJobs(){
    val key = this.hashCode().toString()
    val list = jobHolder[key]
    async (CommonPool){
        list?.forEach {
            val deferred = it.get()
            if(deferred != null && deferred.isActive){
                deferred.cancelAndJoin()
            }
        }
        list?.clear()
        jobHolder.remove(key)
    }
}

