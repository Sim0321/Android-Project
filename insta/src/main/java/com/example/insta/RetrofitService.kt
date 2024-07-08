package com.example.insta

import retrofit2.Call
import retrofit2.http.FieldMap
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST


class User(
    val username: String, val token: String, val id: Int
)

interface RetrofitService {

    @POST("user/signup/")
    @FormUrlEncoded
    fun instaSignup(
        @FieldMap params: HashMap<String, Any>
    ): Call<User>

    @POST("user/login/")
    @FormUrlEncoded
    fun instaLogin(
        @FieldMap params: HashMap<String, Any>
    ): Call<User>
}