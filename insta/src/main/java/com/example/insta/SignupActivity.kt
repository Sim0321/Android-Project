package com.example.insta

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SignupActivity : AppCompatActivity() {

    var username: String = ""
    var password1: String = ""
    var password2: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        findViewById<TextView>(R.id.login_txt).setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        findViewById<EditText>(R.id.id_input).doAfterTextChanged {
            username = it.toString()
        }

        findViewById<EditText>(R.id.pw_input1).doAfterTextChanged {
            password1 = it.toString()
        }


        findViewById<EditText>(R.id.pw_input2).doAfterTextChanged {
            password2 = it.toString()
        }


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


        findViewById<TextView>(R.id.signup_btn).setOnClickListener {

            val user = HashMap<String, Any>()

            user.put("username", username)
            user.put("password1", password1)
            user.put("password2", password2)
            Log.d("http", user.toString())

            retrofitService.instaSignup(user).enqueue(object:Callback<UserToken>{
                override fun onResponse(call: Call<UserToken>, response: Response<UserToken>) {
                    if(response.isSuccessful){
                        val userToken : UserToken? = response.body()
                        Toast.makeText(this@SignupActivity, "성공적!", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<UserToken>, t: Throwable) {
                    TODO("Not yet implemented")
                }
            })
        }



    }
}