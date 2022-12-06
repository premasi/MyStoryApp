package rk.enkidu.mystoryapp.logic.model

import androidx.lifecycle.ViewModel
import rk.enkidu.mystoryapp.data.StoryRepository

class MapsViewModel(private val storyRepository: StoryRepository) : ViewModel() {

    fun getStoryWithLocation(auth: String) = storyRepository.getStoryWithLocation(auth)
}