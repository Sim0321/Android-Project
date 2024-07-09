package com.example.insta

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class FeedFragment : Fragment() {

    private val retrofitService : RetrofitService by lazy {
        RetrofitClient.retrofitService
    }

    // override 한 후에
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
//        원하는 fragment return
        return inflater.inflate(R.layout.feed_fragment, container, false)
    }

    fun postLike(post_id:Int){
        retrofitService.postLike(post_id).enqueue(object:Callback<Any>{
            override fun onResponse(call: Call<Any>, response: Response<Any>) {
                Toast.makeText(activity, "좋아요!", Toast.LENGTH_LONG)
                Log.d("http", "좋아요 성공!")
            }

            override fun onFailure(call: Call<Any>, t: Throwable) {
                Toast.makeText(activity, "좋아요 실패!", Toast.LENGTH_LONG)
                Log.d("http", "좋아요 실패: ${t.message}")
            }
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        val feedListView = view.findViewById<RecyclerView>(R.id.feed_list)


        // 싱글톤 패턴으로 refrofit 따로 뺌
//        val httpClient = OkHttpClient.Builder()
//            .addInterceptor(HttpLoggingInterceptor().apply {
//                setLevel(HttpLoggingInterceptor.Level.BODY)
//            })
//            .build()
//
//
//        val retrofit = Retrofit.Builder()
//            .baseUrl("http://mellowcode.org/")
//            .client(httpClient)
//            .addConverterFactory(GsonConverterFactory.create())
//            .build()
//
//        retrofitService = retrofit.create(RetrofitService::class.java)

        retrofitService.getPosts().enqueue(object : Callback<ArrayList<Post>> {
            override fun onResponse(
                call: Call<ArrayList<Post>>,
                response: Response<ArrayList<Post>>
            ) {
                if (response.isSuccessful) {
                    val postList = response.body()
                    val postRecyclerView = view.findViewById<RecyclerView>(R.id.feed_list)
                    postRecyclerView.adapter = PostRecyclerAdapter(
                        postList!!,
                        LayoutInflater.from(activity),
                        Glide.with(activity!!)

                    )
                    Log.d("http", "포스트 로드 성공!")
                } else {
                    Log.d("http", "Response Error: ${response.errorBody()?.string()}")
                }

            }

            override fun onFailure(call: Call<ArrayList<Post>>, t: Throwable) {
                Log.d("http", "Failure: ${t.message}")

            }
        })
    }

    class PostRecyclerAdapter(
        val postList: ArrayList<Post>,
        val inflater: LayoutInflater,
        val glide: RequestManager
    ) : RecyclerView.Adapter<PostRecyclerAdapter.ViewHolder>() {
        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val ownerImg: ImageView
            val ownerUsername: TextView
            val postImg: ImageView
            val postContent: TextView

            init {
                ownerImg = itemView.findViewById(R.id.owner_img)
                ownerUsername = itemView.findViewById(R.id.owner_username)
                postImg = itemView.findViewById(R.id.post_img)
                postContent = itemView.findViewById(R.id.post_content)
            }
        }

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int,

            ): PostRecyclerAdapter.ViewHolder {
            return ViewHolder(
                inflater.inflate(R.layout.post_item, parent, false)
            )
        }

        override fun onBindViewHolder(holder: PostRecyclerAdapter.ViewHolder, position: Int) {
            val post = postList.get(position)

            post.owner_profile.image?.let {
                glide.load(it).centerCrop().circleCrop().into(holder.ownerImg)
            }

            post.image?.let {
                glide.load(it).centerCrop().into(holder.postImg)
            }
            post.content?.let {
                holder.postContent.text = post.content

            }

            holder.ownerUsername.text = post.owner_profile.username


        }

        override fun getItemCount(): Int {
            return postList.size
        }
    }
}
