package com.example.melon


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
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.Serializable

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val retrofit = Retrofit.Builder()
            .baseUrl("http://mellowcode.org/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val retrofitService = retrofit.create(RetrofitService::class.java)

        retrofitService.getMelonItemList().enqueue(object : Callback<ArrayList<MelonItem>>{
            override fun onResponse(
                call: Call<ArrayList<MelonItem>>,
                response: Response<ArrayList<MelonItem>>
            ) {
//                Log.d("mellon", "분기 앞")
                if(response.isSuccessful){
                    val melonItemList = response.body()
//                    melonItemList!!.forEach{
//                        Log.d("mellon", it.thumbnail )
//                    }
                    findViewById<RecyclerView>(R.id.melon_list_view).apply{
                        this.adapter = MelonItemRecyclerAdapter(
                            melonItemList!!,
                            LayoutInflater.from(this@MainActivity),
                            Glide.with(this@MainActivity),
                            this@MainActivity
                        )
                    }
                }
            }

            override fun onFailure(call: Call<ArrayList<MelonItem>>, t: Throwable) {
                Log.d("mellon", "요청 실패" + t.message)
            }
        })
    }
}

class MelonItemRecyclerAdapter(
    val melonItemList : ArrayList<MelonItem>,
    val inflater: LayoutInflater,
    val glid : RequestManager,
    val context : Context
): RecyclerView.Adapter<MelonItemRecyclerAdapter.ViewHolder>(){
    inner class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        val title : TextView
        val thumbnail : ImageView
        val play: ImageView

        init {
            title = itemView.findViewById(R.id.title)
            thumbnail = itemView.findViewById(R.id.thumbnail)
            play = itemView.findViewById(R.id.play)

            play.setOnClickListener {
                val intent = Intent(context, DetailActivity::class.java)
//                intent.putExtra("melon_item_list", melonItemList) 굳이 다 완성된 객체를 보내지 않고 deSerializable로 분해해서 보내는게더 효율적
                intent.putExtra("melon_item_list", melonItemList as Serializable)
                intent.putExtra("position", adapterPosition)
                context.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            inflater.inflate(R.layout.melon_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.title.text = melonItemList.get(position).title
        glid.load(melonItemList.get(position).thumbnail).into(holder.thumbnail)
//        glid.load(melonItemList.get(position).thumbnail).centerCrop().into(holder.thumbnail)
//        glid.load(melonItemList.get(position).thumbnail)
//            .placeholder(R.drawable.placeholder) // 로딩 중일 때 표시할 이미지
//            .error(R.drawable.error) // 로드 실패 시 표시할 이미지
//            .centerCrop()
//            .into(holder.thumbnail)
    }

    override fun getItemCount(): Int {
        return melonItemList.size
    }
}