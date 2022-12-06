package rk.enkidu.mystoryapp

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.core.app.ActivityOptionsCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import rk.enkidu.mystoryapp.databinding.ActivityMainBinding
import rk.enkidu.mystoryapp.logic.helper.ViewModelFactory
import rk.enkidu.mystoryapp.logic.model.SettingsViewModel
import rk.enkidu.mystoryapp.ui.activity.HomeActivity
import rk.enkidu.mystoryapp.ui.activity.LoginActivity

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!

    private lateinit var settingsViewModel: SettingsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //viewModel
        settingsViewModel = ViewModelProvider(this, ViewModelFactory.getInstance(dataStore))[SettingsViewModel::class.java]

        // close topbar
        setupView()

        //animation
        setupAnimation()

        //check user if already login
        settingsViewModel.getUser().observe(this@MainActivity){
            if(it != null){
                if(it.token.isNotEmpty() && it.isLogin){
                    CoroutineScope(Dispatchers.Main).launch {
                        val intent = Intent(this@MainActivity, HomeActivity::class.java)
                        startActivity(intent,
                            ActivityOptionsCompat.makeSceneTransitionAnimation(this@MainActivity as Activity)
                                .toBundle()
                        )

                        delay(4500)
                        finish()
                    }
                } else {
                    CoroutineScope(Dispatchers.Main).launch {
                        val intent = Intent(this@MainActivity, LoginActivity::class.java)
                        startActivity(intent, ActivityOptionsCompat.makeSceneTransitionAnimation(this@MainActivity as Activity).toBundle())

                        delay(4500)
                        finish()
                    }
                }
            }
        }




    }

    private fun setupAnimation() {
        val title = ObjectAnimator.ofFloat(binding.tvAppName, View.ALPHA, 1F).setDuration(3000)

        AnimatorSet().apply {
            playSequentially(title)
            start()
        }
    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        }
        supportActionBar?.hide()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}