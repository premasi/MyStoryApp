package rk.enkidu.mystoryapp.ui.activity

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import rk.enkidu.mystoryapp.R
import rk.enkidu.mystoryapp.databinding.ActivityHomeBinding
import rk.enkidu.mystoryapp.logic.helper.ViewModelFactory
import rk.enkidu.mystoryapp.logic.model.HomeViewModel
import rk.enkidu.mystoryapp.logic.model.SettingsViewModel
import rk.enkidu.mystoryapp.ui.adapter.ListStoryAdapter

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class HomeActivity : AppCompatActivity() {

    private var _binding : ActivityHomeBinding? = null
    private val binding get() = _binding

    private lateinit var homeViewModel: HomeViewModel
    private lateinit var settingsViewModel: SettingsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        //ViewModel
        settingsViewModel = ViewModelProvider(this, ViewModelFactory.getInstance(dataStore))[SettingsViewModel::class.java]
        homeViewModel = ViewModelProvider(this, ViewModelFactory.getInstance(dataStore))[HomeViewModel::class.java]

        //loading
        showLoading(false)

        binding?.rvStory?.layoutManager = LinearLayoutManager(this)

        //action
        setupAction()
        binding?.fabAdd?.setOnClickListener {
            val intent = Intent(this, UploadActivity::class.java)
            startActivity(intent)
        }

    }

    override fun onResume() {
        super.onResume()
        showLoading(false)
        setupAction()
    }


    private fun setupAction() {
        val adapter = ListStoryAdapter()
        binding?.rvStory?.adapter = adapter

        settingsViewModel.getUser().observe(this@HomeActivity){
            showLoading(true)
            homeViewModel.getStories(it.token).observe(this@HomeActivity){ data ->
                showLoading(false)
                adapter.submitData(lifecycle, data)
            }
        }

    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.option_menu, menu)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.settings -> {
                val intent = Intent(this@HomeActivity, SettingsActivity::class.java)
                startActivity(intent)
            }
            R.id.maps -> {
                val intent = Intent(this@HomeActivity, MapsActivity::class.java)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showLoading(isLoading: Boolean){ binding?.pbHome?.visibility = if (isLoading) View.VISIBLE else View.GONE }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}