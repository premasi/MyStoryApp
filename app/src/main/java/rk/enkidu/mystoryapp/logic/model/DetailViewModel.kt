package rk.enkidu.mystoryapp.logic.model

import androidx.lifecycle.ViewModel
import rk.enkidu.mystoryapp.data.StoryRepository

class DetailViewModel(private val storyRepository: StoryRepository) : ViewModel() {

    fun getDetail(auth: String, id: String) = storyRepository.getStoryDetail(auth, id)
}