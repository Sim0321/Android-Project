package com.example.insta

import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class LoginActivity : AppCompatActivity() {
    var username : String = ""
    var password : String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // 로그 인터셉터 추가
        val logging = HttpLoggingInterceptor().apply {
            setLevel(HttpLoggingInterceptor.Level.BODY)
        }

        // OKHttpClient에 로그 인터셉터 추가
        val httpClient = OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()


        val retrofit = Retrofit.Builder()
            .baseUrl("http://mellowcode.org/")
            .client(httpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val retrofitService = retrofit.create(RetrofitService::class.java)


        findViewById<EditText>(R.id.id_input).doAfterTextChanged {
            username = it.toString()

        }

        findViewById<EditText>(R.id.pw_input).doAfterTextChanged {
            password = it.toString()

        }

        findViewById<TextView>(R.id.login_btn).setOnClickListener {
            val user = HashMap<String, Any>()
            user.put("username",username)
            user.put("password", password)
//            Log.d("console", user.toString())
            retrofitService.instaLogin(user).enqueue(object: Callback<Token>{
                override fun onResponse(call: Call<Token>, response: Response<Token>) {
                    if (response.isSuccessful) {
                        val token:Token? = response.body()
                        Log.d("console", token.toString())
                    } else {
                        Log.d("http", "Response Error: ${response.errorBody()?.string()}")
                    }
                }

                override fun onFailure(call: Call<Token>, t: Throwable) {
                    Log.d("http", "Failure: ${t.message}")
                }
            })
        }



    }
}