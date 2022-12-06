package rk.enkidu.mystoryapp.logic.model

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
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
import rk.enkidu.mystoryapp.data.StoryRepository
import rk.enkidu.mystoryapp.getOrAwaitValue


@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class SettingsViewModelTest{
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var storyRepository: StoryRepository

    private lateinit var settingsViewModel: SettingsViewModel

    @Before
    fun setup(){
        settingsViewModel = SettingsViewModel(storyRepository)
    }

    @Test
    fun `when user in preferences is not null and return success`() = runTest {
        val user = DataDummy.generateUser()

        `when`(storyRepository.getUser()).thenReturn(user)
        val result = settingsViewModel.getUser().getOrAwaitValue()

        Mockito.verify(storyRepository).getUser()
        assertNotNull(result.token)
        assertTrue(result.isLogin)
    }

    @Test
    fun `when user is logout is true`() = runTest {
        settingsViewModel.logout()
        Mockito.verify(storyRepository).logout()
    }
    

}