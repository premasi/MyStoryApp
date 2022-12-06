package rk.enkidu.mystoryapp.logic.model


import androidx.lifecycle.ViewModel
import rk.enkidu.mystoryapp.data.StoryRepository

class RegistrationViewModel(private val storyRepository: StoryRepository) : ViewModel() {
    fun createUser(username: String, email: String, password: String) = storyRepository.createUser(username, email, password)
}