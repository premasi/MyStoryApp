package rk.enkidu.mystoryapp.data.retrofit

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*
import rk.enkidu.mystoryapp.data.response.*

interface ApiService {
    @FormUrlEncoded
    @POST("/v1/register")
    suspend fun createUser(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String,
    ): RegistrationResponse

    @FormUrlEncoded
    @POST("/v1/login")
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String,
    ): LoginResponse

    @GET("v1/stories")
    suspend fun getStories(
        @Header("Authorization") auth: String,
        @Query("page") page: Int,
        @Query("size") size: Int
    ): GetStoryResponse

    @GET("v1/stories/{id}")
    suspend fun getStoryDetail(
        @Header("Authorization") auth: String,
        @Path("id") id: String,
    ): DetailResponse

    @Multipart
    @POST("/v1/stories")
    suspend fun uploadImage(
        @Header("Authorization") auth: String,
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
        @Part("lat") lat : Float,
        @Part("lon") lon : Float,

    ) : UploadResponse

    @GET("v1/stories?location=1.")
    suspend fun getStoriesWithLocation(
        @Header("Authorization") auth: String
    ): GetStoryWithLocationResponse
}