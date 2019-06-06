package com.dx.coroutineretrofit.api

import com.dx.coroutineretrofit.data.GithubRepository
import kotlinx.coroutines.Deferred
import retrofit2.http.GET

interface GithubService{
    @GET("users/StevenDXC/repos")
    fun getRepos(): Deferred<List<GithubRepository>>
}