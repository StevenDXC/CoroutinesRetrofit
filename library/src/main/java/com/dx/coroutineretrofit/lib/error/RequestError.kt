package com.dx.coroutineretrofit.lib.error

data class RequestError(val code:Int,private val msg:String): Error(msg)