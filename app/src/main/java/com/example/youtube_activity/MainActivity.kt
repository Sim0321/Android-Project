package com.example.youtube_activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.example.youtube_activity.RetrofitService
import com.example.youtube_activity.YoutubeItem
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val retrofit = Retrofit.Builder()
            .baseUrl("http://mellowcode.org/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val retrofitService = retrofit.create(RetrofitService::class.java)
        
        retrofitService.getYoutubeItemList().enqueue(object:Callback<ArrayList<YoutubeItem>>{
            override fun onResponse(
                call: Call<ArrayList<YoutubeItem>>,
                response: Response<ArrayList<YoutubeItem>>
            ) {
                val youtubeItemList = response.body()
                youtubeItemList!!.forEach{
//                    Log.d("testt", "여기서 video 불러봄" + it.video)
                }
                val glide = Glide.with(this@MainActivity)
                val adapter = YoutubeListAdapter(
                    youtubeItemList!!,
                    LayoutInflater.from(this@MainActivity),
                    glide,
                    this@MainActivity

                )
                findViewById<RecyclerView>(R.id.youtube_item_list).adapter = adapter
            }

            override fun onFailure(call: Call<ArrayList<YoutubeItem>>, t: Throwable) {
                Log.d("testt", "통신 안됨" + t.message)
            }
        })
    }
}


class YoutubeListAdapter(
    val youtubeItemList : ArrayList<YoutubeItem>,
    val inflater : LayoutInflater,
    val glide : RequestManager,
    val context : Context
): RecyclerView.Adapter<YoutubeListAdapter.ViewHolder>(){

    inner class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        val title : TextView
        val thumbnail : ImageView
        val content : TextView

        init {
            title = itemView.findViewById(R.id.title)
            thumbnail = itemView.findViewById(R.id.thumbnail)
            content = itemView.findViewById(R.id.content)

            itemView.setOnClickListener {
                // 영상 재생에 필요한 정보도 같이 보내야 함
                val intent = Intent(context, YoutubeItemActivity::class.java)
                intent.putExtra("video_url", youtubeItemList.get(adapterPosition).video)
                context.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = inflater.inflate(R.layout.youtube_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.title.text = youtubeItemList.get(position).title
        holder.content.text = youtubeItemList.get(position).content
        glide.load(youtubeItemList.get(position).thumbnail).centerCrop().into(holder.thumbnail)
    }

    override fun getItemCount(): Int {
        return youtubeItemList.size
    }
}


















