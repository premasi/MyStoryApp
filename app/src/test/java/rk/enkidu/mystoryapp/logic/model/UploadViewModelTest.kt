package rk.enkidu.mystoryapp.logic.model

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner
import rk.enkidu.mystoryapp.MainDispatcherRule
import rk.enkidu.mystoryapp.data.Result
import rk.enkidu.mystoryapp.data.StoryRepository
import rk.enkidu.mystoryapp.data.response.UploadResponse
import rk.enkidu.mystoryapp.getOrAwaitValue

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class UploadViewModelTest{
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRules = MainDispatcherRule()

    @Mock
    private lateinit var storyRepository: StoryRepository

    private lateinit var uploadViewModel: UploadViewModel

    @Mock
    private lateinit var file: MultipartBody.Part
    @Mock
    private lateinit var desc : RequestBody
    private val auth = "asdwe"
    private val lat = 1F
    private val lon = 1F


    @Before
    fun setup(){
        uploadViewModel = UploadViewModel(storyRepository)
    }

    @Test
    fun `when upload is success`() = runTest {
        val resultMutable = MutableLiveData<Result<UploadResponse>>()
        resultMutable.value = Result.Success(UploadResponse())

        Mockito.lenient().`when`(
            storyRepository.uploadStory(
                "Bearer $auth",desc,file,lat,lon
            )
        ).thenReturn(resultMutable)
        val result = uploadViewModel.uploadToServer("Bearer $auth",desc,file,lat,lon).getOrAwaitValue()

        Assert.assertNotNull(result)
        Assert.assertTrue(result is Result.Success)
    }

    @Test
    fun `when upload is return error`() = runTest {
        val resultMutable = MutableLiveData<Result<UploadResponse>>()
        resultMutable.value = Result.Error("Error")

        Mockito.lenient().`when`(
            storyRepository.uploadStory(
                "Bearer $auth",desc,file,lat,lon
            )
        ).thenReturn(resultMutable)
        val result = uploadViewModel.uploadToServer("Bearer $auth",desc,file,lat,lon).getOrAwaitValue()

        Assert.assertNotNull(result)
        Assert.assertTrue(result is Result.Error)
    }
}