package rk.enkidu.mystoryapp.logic.model

import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import rk.enkidu.mystoryapp.data.StoryRepository
import rk.enkidu.mystoryapp.data.response.ListStoryItem

class HomeViewModel(private val storyRepository: StoryRepository): ViewModel() {

    fun getStories(token: String): LiveData<PagingData<ListStoryItem>> =
        storyRepository.getAllStory(token).cachedIn(viewModelScope)

}