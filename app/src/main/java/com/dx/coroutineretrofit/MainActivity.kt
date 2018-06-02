package com.dx.coroutineretrofit

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.dx.coroutineretrofit.api.GithubService
import com.dx.coroutineretrofit.lib.RetrofitService
import com.dx.coroutineretrofit.lib.request.cancelAllJobs
import com.dx.coroutineretrofit.lib.request.request

import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        val baseUrl = "https://api.github.com/"
        val retrofit = RetrofitService.createCoroutinesRetrofit(baseUrl,BuildConfig.DEBUG)
        val service = retrofit.create(GithubService::class.java)

        request {
            val result = service.getRepos()
            val data = result.await()
            Log.e("data",data.toString())
        }.onError {
            Log.e("onError",it.toString())
        }.finally {
            Log.e("finally","--->")
        }

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroy() {
        cancelAllJobs()
        super.onDestroy()
    }
}
