package com.dx.coroutineretrofit.lib.request

import com.dx.coroutineretrofit.lib.error.RequestError

typealias FinallyHandler = () -> Unit
typealias ErrorHandler = (RequestError) -> Unit

/**
 * handle request result
 */
class AsyncRequestHandler{

    var errorHandler:ErrorHandler? = null
    var finallyHandler:FinallyHandler? = null

    @Suppress("unused")
    fun onError(errorHandler: (RequestError) -> Unit):AsyncRequestHandler{
        this.errorHandler = errorHandler
        return this
    }

    @Suppress("unused")
    fun finally(finallyHandler: FinallyHandler){
        this.finallyHandler = finallyHandler
    }

}