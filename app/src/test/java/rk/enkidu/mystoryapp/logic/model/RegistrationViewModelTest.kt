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
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner
import rk.enkidu.mystoryapp.MainDispatcherRule
import rk.enkidu.mystoryapp.data.Result
import rk.enkidu.mystoryapp.data.StoryRepository
import rk.enkidu.mystoryapp.data.response.RegistrationResponse
import rk.enkidu.mystoryapp.getOrAwaitValue

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class RegistrationViewModelTest{
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRules = MainDispatcherRule()

    @Mock
    private lateinit var storyRepository: StoryRepository

    private lateinit var registrationViewModel: RegistrationViewModel

    val dummyUsername = "tester"
    val dummyEmail = "tester@gmail.com"
    var dummyPassword = "tester1"

    @Before
    fun setup(){
        registrationViewModel = RegistrationViewModel(storyRepository)
    }

    @Test
    fun `when create user return success`() = runTest {
        val resultMutable = MutableLiveData<Result<RegistrationResponse>>()
        resultMutable.value = Result.Success(RegistrationResponse())

        lenient().`when`(
            storyRepository.createUser(
                dummyUsername,
                dummyEmail,
                dummyPassword
            )
        ).thenReturn(resultMutable)
        val result = registrationViewModel.createUser(dummyUsername, dummyEmail, dummyPassword).getOrAwaitValue()

        assertNotNull(result)
        assertTrue(result is Result.Success)
    }

    @Test
    fun `when create user return error`() = runTest {
        dummyPassword = "1"
        val resultMutable = MutableLiveData<Result<RegistrationResponse>>()
        resultMutable.value = Result.Error("failed")

        lenient().`when`(
            storyRepository.createUser(
                dummyUsername,
                dummyEmail,
                dummyPassword
            )
        ).thenReturn(resultMutable)
        val result = registrationViewModel.createUser(dummyUsername, dummyEmail, dummyPassword).getOrAwaitValue()

        assertNotNull(result)
        assertTrue(result is Result.Error)
    }
}