package rk.enkidu.mystoryapp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import rk.enkidu.mystoryapp.data.User
import rk.enkidu.mystoryapp.data.response.ListMapItem
import rk.enkidu.mystoryapp.data.response.ListStoryItem
import rk.enkidu.mystoryapp.data.response.Story

object DataDummy {
    fun generateDummyStory(): List<ListStoryItem> {
        val items: MutableList<ListStoryItem> = arrayListOf()
        for (i in 0..100) {
            val user = ListStoryItem(
                i.toString(),
                "photo $i",
                "created $i",
                "name $i"
            )
            items.add(user)
        }
        return items
    }

    fun generateUser(): LiveData<User> {
        val mutable = MutableLiveData<User>()
        val user = User("asdwq", true)
        mutable.value = user
        return mutable

    }

    fun generateDummyDetail(): Story{
        val story = Story(
            "asdwe",
            "1234",
            "ser",
            "ser",
            "12"
        )

        return story

    }

    fun generateDummyMaps(): List<ListMapItem>{
        val maps : MutableList<ListMapItem> = arrayListOf()
        for (i in 0..100) {
            val data = ListMapItem(
                "name $i",
                "desc $i",
                1.0,
                1.0
            )
            maps.add(data)
        }
        return maps

    }
}