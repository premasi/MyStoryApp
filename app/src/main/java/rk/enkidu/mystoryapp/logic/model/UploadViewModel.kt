package rk.enkidu.mystoryapp.logic.model

import androidx.lifecycle.ViewModel
import okhttp3.MultipartBody
import okhttp3.RequestBody
import rk.enkidu.mystoryapp.data.StoryRepository

class UploadViewModel(private val storyRepository: StoryRepository): ViewModel(){

    fun uploadToServer(auth: String, desc: RequestBody, image: MultipartBody.Part,
                       lat: Float, lon: Float) = storyRepository.uploadStory(auth, desc, image, lat, lon)

}