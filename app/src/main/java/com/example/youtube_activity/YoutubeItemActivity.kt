package com.example.youtube_activity

import android.os.Bundle
import android.util.Log
import android.widget.MediaController
import android.widget.VideoView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class YoutubeItemActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_youtube_item)

        val videoUrl = intent.getStringExtra("video_url")

        Log.d("testt", "videoUrl은" + videoUrl)

        val mediaController = MediaController(this)

        findViewById<VideoView>(R.id.youtube_video_view).apply{
            Log.d("testt", "url은" + videoUrl.toString())
            this.setVideoPath(videoUrl)
            this.requestFocus()
            this.start()
//            mediaController.show()
     }

    }
}