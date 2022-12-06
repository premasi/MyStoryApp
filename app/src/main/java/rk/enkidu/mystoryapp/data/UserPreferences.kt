package rk.enkidu.mystoryapp.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserPreferences private constructor(private val dataStore: DataStore<Preferences>){
    fun getUser(): Flow<User>{
        return dataStore.data.map {
            User(
                it[TOKEN] ?: "",
                it[STATE] ?: false
            )
        }
    }

    suspend fun saveUser(token: String,state: Boolean){
        dataStore.edit {
            it[TOKEN] = token
            it[STATE] = state
        }
    }

    suspend fun logout(){
        dataStore.edit {
            it[TOKEN] = ""
            it[STATE] = false
        }
    }

    companion object{
        private val TOKEN = stringPreferencesKey("token")
        private val STATE = booleanPreferencesKey("state")

        @Volatile
        private var INSTANCE: UserPreferences? = null
        fun getInstance(dataStore: DataStore<Preferences>): UserPreferences{
            return INSTANCE ?: synchronized(this){
                val instance = UserPreferences(dataStore)
                INSTANCE = instance
                instance
            }
        }
    }
}