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
import org.mockito.Mockito.lenient
import org.mockito.junit.MockitoJUnitRunner
import rk.enkidu.mystoryapp.DataDummy
import rk.enkidu.mystoryapp.MainDispatcherRule
import rk.enkidu.mystoryapp.data.Result
import rk.enkidu.mystoryapp.data.StoryRepository
import rk.enkidu.mystoryapp.data.response.Story
import rk.enkidu.mystoryapp.getOrAwaitValue

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class DetailViewModelTest{
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var storyRepository: StoryRepository
    private lateinit var detailViewModel: DetailViewModel
    private val auth = "auth"
    private val id = "id"

    @Before
    fun setup(){
        detailViewModel = DetailViewModel(storyRepository)
    }

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()


    @Test
    fun `when get detail is not null and Return Success`() = runTest{
        val dummy = DataDummy.generateDummyDetail()
        val expected = MutableLiveData<Result<Story>>()
        expected.value = Result.Success(dummy)

        `when`(storyRepository.getStoryDetail("Bearer $auth", id)).thenReturn(expected)
        val result = detailViewModel.getDetail("Bearer $auth", id).getOrAwaitValue()

        Mockito.verify(storyRepository).getStoryDetail("Bearer $auth", id)
        assertNotNull(result)
        assertTrue(result is Result.Success)
    }

    @Test
    fun `when get detail is null and Return Failed`() = runTest {
        val expectedDetail = MutableLiveData<Result<Story>>()
        expectedDetail.value = Result.Error("failed")

        lenient().`when`(storyRepository.getStoryDetail("Bearer $auth", id)).thenReturn(expectedDetail)
        val result = detailViewModel.getDetail("Bearer $auth", id).getOrAwaitValue()

        Mockito.verify(storyRepository).getStoryDetail("Bearer $auth", id)
        assertNotNull(result)
        assertTrue(result is Result.Error)
    }
}