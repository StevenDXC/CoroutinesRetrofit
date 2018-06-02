package com.dx.coroutineretrofit.data

class GithubRepository {

    var id: Long = 0
    var owner: RepositoryOwner? = null
    var name: String? = null
    var full_name: String? = null
    var description: String? = null
    var fork: Boolean = false
    var url: String? = null
    var html_url: String? = null
}

class RepositoryOwner{
    var id: Long = 0
    var login: String? = null
    var avatar_url: String? = null
    var url: String? = null
    var type: String? = null
}