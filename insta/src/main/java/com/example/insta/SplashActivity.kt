package com.example.insta

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val sharedPreferenes = getSharedPreferences("user_info", Context.MODE_PRIVATE)
        val token = sharedPreferenes.getString("token", "empty")
        Log.d("instaa", token!!)

        when (token) {
            // 로그인이 되어있는 경우
            "empty" -> {
                startActivity(Intent(this, LoginActivity::class.java))
            }

            else -> {
                // 로그인이 되어있지 않은 경우
                startActivity(Intent(this, HomeActivity::class.java))
            }
        }
    }
}