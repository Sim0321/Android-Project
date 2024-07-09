package com.example.insta

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import com.example.insta.RetrofitClient.retrofitService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SignupActivity : AppCompatActivity() {

    private val retrofitService : RetrofitService by lazy {
        RetrofitClient.retrofitService
    }

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


        findViewById<TextView>(R.id.signup_btn).setOnClickListener {

            val user = HashMap<String, Any>()

            user.put("username", username)
            user.put("password1", password1)
            user.put("password2", password2)
            Log.d("http", user.toString())

            retrofitService.instaSignup(user).enqueue(object : Callback<User> {
                override fun onResponse(call: Call<User>, response: Response<User>) {
                    if (response.isSuccessful) {
                        val user: User? = response.body()

                        val sharedPreferences =
                            getSharedPreferences("user_info", Context.MODE_PRIVATE)
                        val editor: SharedPreferences.Editor = sharedPreferences.edit()
                        editor.putString("token", user?.token)
                        editor.putString("user_id", user?.id.toString())
                        editor.commit()
                        startActivity(Intent(this@SignupActivity, HomeActivity::class.java))
                        Log.d("http", "회원가입 성공!")
                    }
                }

                override fun onFailure(call: Call<User>, t: Throwable) {
                    Toast.makeText(this@SignupActivity, "네트워크 오류: ${t.message}", Toast.LENGTH_SHORT)
                        .show()

                }
            })
        }


    }
}