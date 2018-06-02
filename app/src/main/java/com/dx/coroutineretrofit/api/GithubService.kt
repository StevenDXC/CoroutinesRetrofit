package com.dx.coroutineretrofit.api

import com.dx.coroutineretrofit.data.GithubRepository
import retrofit2.http.GET
import retrofit2.http.Path
import kotlinx.coroutines.experimental.Deferred

interface GithubService{
    @GET("users/StevenDXC/repos")
    fun getRepos(): Deferred<List<GithubRepository>>
}