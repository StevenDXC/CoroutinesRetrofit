package com.dx.coroutineretrofit.lib.error

data class RequestError(val code:Int,private val msg:String): Error(msg)
{
    companion object {
        const val JSON_PARSE_ERROR_CODE = 10000
        const val IO_ERROR_CODE = 10001
    }

}