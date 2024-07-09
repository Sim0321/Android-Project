package com.example.insta

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
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

    private val retrofitService : RetrofitService by lazy {
        RetrofitClient.retrofitService
    }

    var username: String = ""
    var password: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // 로그 인터셉터 추가
        val logging = HttpLoggingInterceptor().apply {
            setLevel(HttpLoggingInterceptor.Level.BODY)
        }




        findViewById<EditText>(R.id.id_input).doAfterTextChanged {
            username = it.toString()

        }

        findViewById<EditText>(R.id.pw_input).doAfterTextChanged {
            password = it.toString()

        }

        findViewById<TextView>(R.id.signup_txt).setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
        }

        findViewById<TextView>(R.id.login_btn).setOnClickListener {
            val user = HashMap<String, Any>()
            user.put("username", username)
            user.put("password", password)
//            Log.d("console", user.toString())
            retrofitService.instaLogin(user).enqueue(object : Callback<User> {
                override fun onResponse(call: Call<User>, response: Response<User>) {
                    if (response.isSuccessful) {
                        val user: User? = response.body()

                        val sharedPreferences =
                            getSharedPreferences("user_info", Context.MODE_PRIVATE)
                        val editor: SharedPreferences.Editor = sharedPreferences.edit()
                        editor.putString("token", user?.token)
                        editor.putString("user_id", user?.id.toString())
                        editor.commit()
                        startActivity(Intent(this@LoginActivity, HomeActivity::class.java))

                        Log.d("http", "로그인 성공!")
                    } else {
                        Log.d("http", "Response Error: ${response.errorBody()?.string()}")
                    }
                }

                override fun onFailure(call: Call<User>, t: Throwable) {
                    Log.d("http", "Failure: ${t.message}")
                }
            })
        }
    }
}