package rk.enkidu.mystoryapp.ui.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityOptionsCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import rk.enkidu.mystoryapp.R
import rk.enkidu.mystoryapp.databinding.ActivitySettingsBinding
import rk.enkidu.mystoryapp.logic.helper.ViewModelFactory
import rk.enkidu.mystoryapp.logic.model.SettingsViewModel

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SettingsActivity : AppCompatActivity() {

    private var _binding : ActivitySettingsBinding? = null
    private val binding get() = _binding

    private lateinit var settingsViewModel: SettingsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        //ViewModel
        settingsViewModel = ViewModelProvider(this, ViewModelFactory.getInstance(dataStore))[SettingsViewModel::class.java]

        supportActionBar?.title = "Settings"

        binding?.tvLogout?.setOnClickListener {
            AlertDialog.Builder(this).apply {
                setTitle(getString(R.string.alert))
                setMessage(getString(R.string.confirmation))
                setNegativeButton(getString(R.string.no)){ _, _ -> }
                setPositiveButton(getString(R.string.yes)) { _, _ ->
                    settingsViewModel.logout()

                    val intent = Intent(this@SettingsActivity, LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent, ActivityOptionsCompat.makeSceneTransitionAnimation(this@SettingsActivity as Activity).toBundle())
                    finish()
                }
                create()
                show()
            }
        }

        binding?.tvBahasa?.setOnClickListener {
            startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}