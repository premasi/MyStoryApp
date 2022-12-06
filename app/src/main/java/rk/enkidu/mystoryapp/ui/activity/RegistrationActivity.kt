package rk.enkidu.mystoryapp.ui.activity

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import rk.enkidu.mystoryapp.R
import rk.enkidu.mystoryapp.databinding.ActivityRegistrationBinding
import rk.enkidu.mystoryapp.data.Result
import rk.enkidu.mystoryapp.logic.helper.ViewModelFactory
import rk.enkidu.mystoryapp.logic.model.RegistrationViewModel

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class RegistrationActivity : AppCompatActivity() {

    private var _binding : ActivityRegistrationBinding? = null
    private val binding get() = _binding

    private lateinit var registrationViewModel: RegistrationViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityRegistrationBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        //viewModel
        registrationViewModel = ViewModelProvider(this, ViewModelFactory.getInstance(dataStore))[RegistrationViewModel::class.java]

        //state loading
        showLoading(false)

        //close topbar
        setupView()

        //animation
        setupAnimation()

        //registration action
        binding?.btnSignUp?.setOnClickListener {
            setupAction()
        }

        //back button
        binding?.ivBack?.setOnClickListener {
            finish()
        }

        //go to login
        binding?.tvLogin?.setOnClickListener {
            finish()
        }
    }

    private fun setupAction(){
        val username = binding?.etRegisUsername?.text.toString()
        val email = binding?.etRegisEmail?.text.toString()
        val password = binding?.etRegisPassword?.text.toString()

        when{
            username.isEmpty() -> {
                binding?.etRegisUsername?.error = getString(R.string.error_message_username)
            }
            email.isEmpty() -> {
                binding?.etRegisEmail?.error = getString(R.string.error_message_email)
            }
            password.isEmpty() -> {
                binding?.etRegisEmail?.error = getString(R.string.error_message_password2)
            }
            else -> {
                registrationViewModel.createUser(username, email, password).observe(this@RegistrationActivity){ result ->
                    if (result != null) {
                        when (result) {
                            is Result.Loading -> {
                                showLoading(true)
                            }
                            is Result.Success -> {
                                showLoading(false)
                                Toast.makeText(this@RegistrationActivity, getString(R.string.message_success), Toast.LENGTH_SHORT).show()
                                CoroutineScope(Dispatchers.Main).launch {
                                    delay(2000)
                                    finish()
                                }
                            }
                            is Result.Error -> {
                                showLoading(false)
                                Toast.makeText(this@RegistrationActivity, getString(R.string.message_create_account), Toast.LENGTH_SHORT).show()
                            }
                        }
                    }

                }
            }
        }
    }

    private fun setupAnimation() {
        val title = ObjectAnimator.ofFloat(binding?.tvRegistration, View.ALPHA, 1F).setDuration(500)
        val username = ObjectAnimator.ofFloat(binding?.tvRegisUsername, View.ALPHA, 1F).setDuration(500)
        val etUsername = ObjectAnimator.ofFloat(binding?.etRegisUsername, View.ALPHA, 1F).setDuration(500)
        val email = ObjectAnimator.ofFloat(binding?.tvRegisEmail, View.ALPHA, 1F).setDuration(500)
        val etEmail = ObjectAnimator.ofFloat(binding?.etRegisEmail, View.ALPHA, 1F).setDuration(500)
        val password = ObjectAnimator.ofFloat(binding?.tvRegisPassword, View.ALPHA, 1F).setDuration(500)
        val etPassword = ObjectAnimator.ofFloat(binding?.etRegisPassword, View.ALPHA, 1F).setDuration(500)
        val registration = ObjectAnimator.ofFloat(binding?.tvLogin, View.ALPHA, 1F).setDuration(500)
        val button = ObjectAnimator.ofFloat(binding?.btnSignUp, View.ALPHA, 1F).setDuration(500)

        AnimatorSet().apply {
            playSequentially(title, username, etUsername, email, etEmail, password, etPassword, registration, button)
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

    private fun showLoading(isLoading: Boolean){ binding?.pbRegis?.visibility = if (isLoading) View.VISIBLE else View.GONE }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}