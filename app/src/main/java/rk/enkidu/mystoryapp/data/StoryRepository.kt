package rk.enkidu.mystoryapp.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.liveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import rk.enkidu.mystoryapp.data.paging3.StoryPagingSource
import rk.enkidu.mystoryapp.data.response.*
import rk.enkidu.mystoryapp.data.retrofit.ApiService

class StoryRepository(private val apiService: ApiService, private val userPreferences: UserPreferences) {

    fun getUser(): LiveData<User> = userPreferences.getUser().asLiveData()

    fun logout(){
        CoroutineScope(Dispatchers.Main).launch {
            userPreferences.logout()
        }
    }

    fun createUser(username: String, email: String, password: String): LiveData<Result<RegistrationResponse>> = liveData{
        emit(Result.Loading)
        try {
            val service = apiService.createUser(username, email, password)
            emit(Result.Success(service))
        } catch (e: Exception) {
            emit(Result.Error(e.message.toString()))
        }
    }

    fun login(email: String, password: String): LiveData<Result<LoginResponse>> = liveData{
        emit(Result.Loading)
        try {
            val service = apiService.login(email, password)

            userPreferences.saveUser(service.loginResult?.token!!, true)

            emit(Result.Success(service))
        } catch (e: Exception) {
            emit(Result.Error(e.message.toString()))
        }

    }

    fun getAllStory(token: String): LiveData<PagingData<ListStoryItem>> {
        return Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            pagingSourceFactory = {
                StoryPagingSource(apiService, token)
            }
        ).liveData
    }

    fun getStoryDetail(auth: String, id: String): LiveData<Result<Story>> = liveData {
        emit(Result.Loading)
        try{
            val service = apiService.getStoryDetail("Bearer $auth", id)
            val response = service.story!!
            val story = Story(
                response.photoUrl,
                response.createdAt,
                response.name,
                response.description,
                response.id
            )


            emit(Result.Success(story))
        } catch (e: Exception){
            emit(Result.Error(e.message.toString()))
        }
    }

    fun uploadStory(auth: String, desc: RequestBody, image: MultipartBody.Part, lat: Float, lon: Float): LiveData<Result<UploadResponse>> = liveData {
        emit(Result.Loading)
        try{
            val service = apiService.uploadImage("Bearer $auth",image, desc, lat, lon)
            emit(Result.Success(service))
        } catch(e: Exception){
            emit(Result.Error(e.message.toString()))
        }
    }

    fun getStoryWithLocation(auth: String): LiveData<Result<List<ListMapItem>>> = liveData {
        emit(Result.Loading)
        try{
            val service = apiService.getStoriesWithLocation("Bearer $auth")
            val response = service.listStory
            val map = response.map {
                ListMapItem(
                    it.name,
                    it.description,
                    it.lon,
                    it.lat
                )
            }
            emit(Result.Success(map))
        } catch (e: Exception){
            emit(Result.Error(e.message.toString()))
        }
    }

}