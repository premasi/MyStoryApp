package rk.enkidu.mystoryapp.logic.model

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner
import rk.enkidu.mystoryapp.DataDummy
import rk.enkidu.mystoryapp.MainDispatcherRule
import rk.enkidu.mystoryapp.data.Result
import rk.enkidu.mystoryapp.data.StoryRepository
import rk.enkidu.mystoryapp.data.response.ListMapItem
import rk.enkidu.mystoryapp.getOrAwaitValue

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class MapsViewModelTest{
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var storyRepository: StoryRepository
    private lateinit var mapsViewModel: MapsViewModel
    private val auth = "auth"
    @Before
    fun setup(){
        mapsViewModel = MapsViewModel(storyRepository)
    }

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `when maps is not null and Return Success`() = runTest {
        val dummy = DataDummy.generateDummyMaps()
        val expected = MutableLiveData<Result<List<ListMapItem>>>()
        expected.value = Result.Success(dummy)

        `when`(storyRepository.getStoryWithLocation("Bearer $auth")).thenReturn(expected)

        val actualMaps = mapsViewModel.getStoryWithLocation("Bearer $auth").getOrAwaitValue()

        Mockito.verify(storyRepository).getStoryWithLocation("Bearer $auth")
        assertNotNull(actualMaps)
        assertTrue(actualMaps is Result.Success)
    }

    @Test
    fun `when network error Return Error`() = runTest {
        val expected = MutableLiveData<Result<List<ListMapItem>>>()
        expected.value = Result.Error("Error")

        `when`(storyRepository.getStoryWithLocation("Bearer $auth")).thenReturn(expected)
        val actualMaps = mapsViewModel.getStoryWithLocation("Bearer $auth").getOrAwaitValue()

        Mockito.verify(storyRepository).getStoryWithLocation("Bearer $auth")
        assertNotNull(actualMaps)
        assertTrue(actualMaps is Result.Error)
    }

}