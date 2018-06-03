# CoroutinesRetrofit

Retrofit with coroutines Adapter add kotlin extension,easy to use Retrofit in Kotlin

[![](https://jitpack.io/v/StevenDXC/CoroutinesRetrofit.svg)](https://jitpack.io/#StevenDXC/CoroutinesRetrofit)

## usage

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

## dependency

Add it in your root build.gradle at the end of repositories:

```
allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```

Add the dependency

```
dependencies {
    implementation 'com.github.StevenDXC:CoroutinesRetrofit:1.0'
}
```