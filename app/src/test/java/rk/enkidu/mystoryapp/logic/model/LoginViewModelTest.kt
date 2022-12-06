package rk.enkidu.mystoryapp.logic.model

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import junit.framework.Assert.assertNotNull
import junit.framework.Assert.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner
import rk.enkidu.mystoryapp.data.StoryRepository
import rk.enkidu.mystoryapp.data.response.LoginResponse
import rk.enkidu.mystoryapp.data.Result
import rk.enkidu.mystoryapp.getOrAwaitValue

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class LoginViewModelTest{
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var storyRepository: StoryRepository

    private lateinit var loginViewModel: LoginViewModel
    val dummyEmail = "tester@gmail.com"
    var dummyPassword = "tester1"


    @Before
    fun setup(){
        loginViewModel = LoginViewModel(storyRepository)
    }

    @Test
    fun `when login is success`() = runTest {
        val result = MutableLiveData<Result<LoginResponse>>()
        result.value = Result.Success(LoginResponse())

        Mockito.lenient().`when`(storyRepository.login(dummyEmail, dummyPassword)).thenReturn(result)
        val actual = loginViewModel.login(dummyEmail, dummyPassword).getOrAwaitValue()

        assertNotNull(actual)
        assertTrue(actual is Result.Success)

    }

    @Test
    fun `when login is failed`() = runTest {
        dummyPassword = "1"
        val result = MutableLiveData<Result<LoginResponse>>()
        result.value = Result.Error("failed")

        Mockito.lenient().`when`(storyRepository.login(dummyEmail, dummyPassword)).thenReturn(result)
        val actual = loginViewModel.login(dummyEmail, dummyPassword).getOrAwaitValue()

        assertNotNull(actual)
        assertTrue(actual is Result.Error)
    }
}