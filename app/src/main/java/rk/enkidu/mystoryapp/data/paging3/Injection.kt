package rk.enkidu.mystoryapp.data.paging3

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import rk.enkidu.mystoryapp.data.StoryRepository
import rk.enkidu.mystoryapp.data.UserPreferences
import rk.enkidu.mystoryapp.data.retrofit.ApiConfig

object Injection {
    fun provideRepository(dataStore: DataStore<Preferences>): StoryRepository {
        val apiService = ApiConfig.getApiService()
        val userPreferences = UserPreferences.getInstance(dataStore)
        return StoryRepository(apiService, userPreferences)
    }
}