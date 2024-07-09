package com.example.insta

import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.FieldMap
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.HeaderMap
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path


class User(
    val username: String, val token: String, val id: Int
)

class Post(
    val id: Int, val content: String?, val image: String?, val owner_profile: OwnerProfile
)

class OwnerProfile(
    val username: String, val image: String?,
)

interface RetrofitService {

    //    회원가입
    @POST("user/signup/")
    @FormUrlEncoded
    fun instaSignup(
        @FieldMap params: HashMap<String, Any>
    ): Call<User>


    //     로그인
    @POST("user/login/")
    @FormUrlEncoded
    fun instaLogin(
        @FieldMap params: HashMap<String, Any>
    ): Call<User>

    //    post 리스트 불러오기
    @GET("instagram/post/list/all/")
    fun getPosts(

    ): Call<ArrayList<Post>>

    //    좋아요
    @POST("instagram/post/like/{post_id}")
    fun postLike(
        @Path("post_id") post_id: Int
    ): Call<Any>

    // 업로드
    @Multipart
    @POST("instagram/post/")
    fun uploadPost(
        @HeaderMap headers : Map<String, String>,
        @Part image : MultipartBody.Part,
        @Part("content") content:RequestBody
    ): Call<ResponseBody>
}