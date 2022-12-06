package rk.enkidu.mystoryapp.logic.model

import androidx.lifecycle.*
import rk.enkidu.mystoryapp.data.StoryRepository

class LoginViewModel(private val storyRepository: StoryRepository): ViewModel() {

    fun login(email: String, password: String) = storyRepository.login(email, password)

}