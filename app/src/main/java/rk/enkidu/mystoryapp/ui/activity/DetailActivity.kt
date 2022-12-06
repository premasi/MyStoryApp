package rk.enkidu.mystoryapp.ui.activity

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import rk.enkidu.mystoryapp.R
import rk.enkidu.mystoryapp.data.Result
import rk.enkidu.mystoryapp.data.response.Story
import rk.enkidu.mystoryapp.databinding.ActivityDetailBinding
import rk.enkidu.mystoryapp.logic.helper.ViewModelFactory
import rk.enkidu.mystoryapp.logic.model.DetailViewModel
import rk.enkidu.mystoryapp.logic.model.SettingsViewModel

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class DetailActivity : AppCompatActivity() {

    private var _binding : ActivityDetailBinding? = null
    private val binding get() = _binding

    private lateinit var settingsViewModel: SettingsViewModel
    private lateinit var detailViewModel: DetailViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        supportActionBar?.title = "Detail"

        //loading
        showLoading(false)

        //ViewModel
        settingsViewModel = ViewModelProvider(this, ViewModelFactory.getInstance(dataStore))[SettingsViewModel::class.java]
        detailViewModel = ViewModelProvider(this, ViewModelFactory.getInstance(dataStore))[DetailViewModel::class.java]

        val id = intent.getStringExtra(EXTRA_ID)
        Log.v("id", id.toString())

        //action
        setupAction(id)
    }

    private fun setupAction(id: String?) {
        settingsViewModel.getUser().observe(this@DetailActivity){
            detailViewModel.getDetail(it.token, id!!).observe(this@DetailActivity){ result ->
                if (result != null) {
                    when (result) {
                        is Result.Loading -> {
                            showLoading(true)
                        }
                        is Result.Success -> {
                            showLoading(false)
                            val data = result.data
                            setupView(data)
                            Toast.makeText(this@DetailActivity, getString(R.string.story_sukses), Toast.LENGTH_SHORT).show()
                        }
                        is Result.Error -> {
                            showLoading(false)
                            Toast.makeText(this@DetailActivity, getString(R.string.story_gagal), Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }

    }

    private fun setupView(story: Story){
        Glide.with(this)
            .load(story.photoUrl) // URL Gambar
            .into(binding?.ivImageDetail!!) // imageView mana yang akan diterapkan
        Log.v("story view", story.name.toString())
        binding?.tvNameDetail?.text = story.name
        binding?.tvDescDetail?.text = story.description
        binding?.tvDateDetail?.text = story.createdAt
    }

    private fun showLoading(isLoading: Boolean){ binding?.pbDetail?.visibility = if (isLoading) View.VISIBLE else View.GONE }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object{
        const val EXTRA_ID = "extra_id"
    }
}