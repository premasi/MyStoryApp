package rk.enkidu.mystoryapp.logic.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import rk.enkidu.mystoryapp.data.User
import rk.enkidu.mystoryapp.data.StoryRepository

class SettingsViewModel(private val storyRepository: StoryRepository) : ViewModel() {
    fun getUser(): LiveData<User> = storyRepository.getUser()

    fun logout() = storyRepository.logout()
}