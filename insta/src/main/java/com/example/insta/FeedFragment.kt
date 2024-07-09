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
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FeedFragment : Fragment() {

    private val retrofitService: RetrofitService by lazy {
        RetrofitClient.retrofitService
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d("FeedFragment", "onCreateView called")
        return inflater.inflate(R.layout.feed_fragment, container, false)
    }

    fun postLike(post_id: Int, callback: () -> Unit) {
        retrofitService.postLike(post_id).enqueue(object : Callback<Any> {
            override fun onResponse(call: Call<Any>, response: Response<Any>) {
                Toast.makeText(activity, "좋아요!", Toast.LENGTH_LONG).show()
                Log.d("FeedFragment", "좋아요 성공!")
                callback()
            }

            override fun onFailure(call: Call<Any>, t: Throwable) {
                Toast.makeText(activity, "좋아요 실패!", Toast.LENGTH_LONG).show()
                Log.d("FeedFragment", "좋아요 실패: ${t.message}")
            }
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("FeedFragment", "onViewCreated called")

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
                        Glide.with(activity!!),
                        this@FeedFragment,
                        activity as HomeActivity
                    )
                    Log.d("FeedFragment", "포스트 로드 성공!")
                } else {
                    Log.d("FeedFragment", "Response Error: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<ArrayList<Post>>, t: Throwable) {
                Log.d("FeedFragment", "Failure: ${t.message}")
            }
        })
    }

    class PostRecyclerAdapter(
        val postList: ArrayList<Post>,
        val inflater: LayoutInflater,
        val glide: RequestManager,
        val feedFragment: FeedFragment,
        val activity: HomeActivity
    ) : RecyclerView.Adapter<PostRecyclerAdapter.ViewHolder>() {
        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val ownerImg: ImageView = itemView.findViewById(R.id.owner_img)
            val ownerUsername: TextView = itemView.findViewById(R.id.owner_username)
            val postImg: ImageView = itemView.findViewById(R.id.post_img)
            val postContent: TextView = itemView.findViewById(R.id.post_content)
            val postLayer: ImageView = itemView.findViewById(R.id.post_layer)
            val postHeart: ImageView = itemView.findViewById(R.id.post_heart)

            init {
                postImg.setOnClickListener {
                    Log.d("PostRecyclerAdapter", "Image clicked at position: $adapterPosition")
                    feedFragment.postLike(postList[adapterPosition].id) {
                        activity.runOnUiThread {
                            postLayer.visibility = View.VISIBLE
                            postHeart.visibility = View.VISIBLE
                        }
                        Thread {
                            Thread.sleep(2000)
                            activity.runOnUiThread {
                                postLayer.visibility = View.INVISIBLE
                                postHeart.visibility = View.INVISIBLE
                            }
                        }.start()
                    }
                }
            }
        }

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): PostRecyclerAdapter.ViewHolder {
            Log.d("PostRecyclerAdapter", "onCreateViewHolder called")
            return ViewHolder(
                inflater.inflate(R.layout.post_item, parent, false)
            )
        }

        override fun onBindViewHolder(holder: PostRecyclerAdapter.ViewHolder, position: Int) {
            Log.d("PostRecyclerAdapter", "onBindViewHolder called for position: $position")
            val post = postList[position]

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