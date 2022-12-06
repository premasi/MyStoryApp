package rk.enkidu.mystoryapp.logic.helper

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import rk.enkidu.mystoryapp.data.paging3.Injection
import rk.enkidu.mystoryapp.data.StoryRepository
import rk.enkidu.mystoryapp.logic.model.*

class ViewModelFactory(private val storyRepository: StoryRepository) : ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            return HomeViewModel(storyRepository) as T
        } else if (modelClass.isAssignableFrom(RegistrationViewModel::class.java)){
            return RegistrationViewModel(storyRepository) as T
        }else if (modelClass.isAssignableFrom(LoginViewModel::class.java)){
            return LoginViewModel(storyRepository) as T
        }else if (modelClass.isAssignableFrom(SettingsViewModel::class.java)){
            return SettingsViewModel(storyRepository) as T
        }else if (modelClass.isAssignableFrom(DetailViewModel::class.java)){
            return DetailViewModel(storyRepository) as T
        }else if (modelClass.isAssignableFrom(UploadViewModel::class.java)) {
            return UploadViewModel(storyRepository) as T
        }else if (modelClass.isAssignableFrom(MapsViewModel::class.java)) {
            return MapsViewModel(storyRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

    companion object {
        @Volatile
        private var instance: ViewModelFactory? = null
        fun getInstance(dataStore: DataStore<Preferences>): ViewModelFactory =
            instance ?: synchronized(this) {
                instance ?: ViewModelFactory(Injection.provideRepository(dataStore))
            }.also { instance = it }
    }
}