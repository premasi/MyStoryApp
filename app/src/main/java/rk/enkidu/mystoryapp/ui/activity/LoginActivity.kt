package rk.enkidu.mystoryapp.ui.activity

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
import android.widget.Toast
import androidx.core.app.ActivityOptionsCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import rk.enkidu.mystoryapp.R
import rk.enkidu.mystoryapp.data.Result
import rk.enkidu.mystoryapp.databinding.ActivityLoginBinding
import rk.enkidu.mystoryapp.logic.helper.ViewModelFactory
import rk.enkidu.mystoryapp.logic.model.LoginViewModel

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class LoginActivity : AppCompatActivity() {
    private var _binding : ActivityLoginBinding? = null
    private val binding get() = _binding

    private lateinit var loginViewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        //viewModel
        loginViewModel = ViewModelProvider(this, ViewModelFactory.getInstance(dataStore))[LoginViewModel::class.java]

        //loading
        showLoading(false)

        //close topbar
        setupView()

        //animation
        setupAnimation()

        //intent to registration
        binding?.tvRegistrasi?.setOnClickListener {
            intentToRegistration()
        }

        //setup action
        binding?.btnLogin?.setOnClickListener {
            setupAction()
        }

    }

    private fun setupAction() {
        val email = binding?.etEmail?.text.toString()
        val password = binding?.etPassword?.text.toString()

        loginViewModel.login(email, password).observe(this@LoginActivity){ result ->
            if (result != null) {
                when (result) {
                    is Result.Loading -> {
                        showLoading(true)
                    }
                    is Result.Success -> {
                        showLoading(false)
                        Toast.makeText(this@LoginActivity, getString(R.string.ok), Toast.LENGTH_SHORT).show()
                        CoroutineScope(Dispatchers.Main).launch {
                            delay(2000)
                            val intent =
                                Intent(this@LoginActivity, HomeActivity::class.java)
                            intent.flags =
                                Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                            startActivity(
                                intent,
                                ActivityOptionsCompat.makeSceneTransitionAnimation(this@LoginActivity as Activity)
                                    .toBundle()
                            )
                        }
                    }
                    is Result.Error -> {
                        showLoading(false)
                        Toast.makeText(this@LoginActivity, getString(R.string.badrequest), Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

    }

    private fun intentToRegistration(){
        val intent = Intent(this@LoginActivity, RegistrationActivity::class.java)
        startActivity(intent, ActivityOptionsCompat.makeSceneTransitionAnimation(this@LoginActivity as Activity).toBundle())
    }

    private fun setupAnimation() {
        val image = ObjectAnimator.ofFloat(binding?.ivIllustration, View.ALPHA, 1F).setDuration(500)
        val title = ObjectAnimator.ofFloat(binding?.tvLogin, View.ALPHA, 1F).setDuration(500)
        val username = ObjectAnimator.ofFloat(binding?.tvEmail, View.ALPHA, 1F).setDuration(500)
        val etUsername = ObjectAnimator.ofFloat(binding?.etEmail, View.ALPHA, 1F).setDuration(500)
        val password = ObjectAnimator.ofFloat(binding?.tvPassword, View.ALPHA, 1F).setDuration(500)
        val etPassword = ObjectAnimator.ofFloat(binding?.etPassword, View.ALPHA, 1F).setDuration(500)
        val registration = ObjectAnimator.ofFloat(binding?.tvRegistrasi, View.ALPHA, 1F).setDuration(500)
        val button = ObjectAnimator.ofFloat(binding?.btnLogin, View.ALPHA, 1F).setDuration(500)

        AnimatorSet().apply {
            playSequentially(image, title, username, etUsername, password, etPassword, registration, button)
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

    private fun showLoading(isLoading: Boolean){ binding?.pbLogin?.visibility = if (isLoading) View.VISIBLE else View.GONE }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}