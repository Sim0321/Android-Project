package com.example.insta

import retrofit2.Call
import retrofit2.http.FieldMap
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST


class User(
    val username: String, val token: String, val id: Int
)

class Post(
    val content : String, val image : String, val owner_profile : OwnerProfile
)

class OwnerProfile (
    val username : String, val image : String,
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

    ):Call<ArrayList<Post>>
}