# CoroutinesRetrofit

Retrofit with coroutines Adapter add kotlin extension,easy to use Retrofit in Kotlin


## usage:

* API interface

```java

interface GithubService{
    @GET("users/StevenDXC/repos")
    fun getRepos(): Deferred<List<GithubRepository>>
}

```

* create Retrofit and api

```java
val retrofit = RetrofitService.createCoroutinesRetrofit(baseUrl,BuildConfig.DEBUG)
val gitHubService = retrofit.create(GithubService::class.java)
```

* request

```java
request {
    loadingDialog.show()
    val data = service.getRepos().await()
    ...
}.onError {
   showToast(it.message)
}.finally {
   loadingDialog.cancel()
}
```



