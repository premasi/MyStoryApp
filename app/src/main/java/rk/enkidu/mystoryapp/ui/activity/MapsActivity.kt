package rk.enkidu.mystoryapp.ui.activity

import android.content.ContentValues.TAG
import android.content.Context
import android.content.res.Resources
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import rk.enkidu.mystoryapp.R
import rk.enkidu.mystoryapp.data.Result
import rk.enkidu.mystoryapp.data.response.ListMapItem
import rk.enkidu.mystoryapp.databinding.ActivityMapsBinding
import rk.enkidu.mystoryapp.logic.helper.ViewModelFactory
import rk.enkidu.mystoryapp.logic.model.MapsViewModel
import rk.enkidu.mystoryapp.logic.model.SettingsViewModel

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding

    private lateinit var settingsViewModel: SettingsViewModel
    private lateinit var mapsViewModel: MapsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "Maps"

        //viewModel
        settingsViewModel = ViewModelProvider(this, ViewModelFactory.getInstance(dataStore))[SettingsViewModel::class.java]
        mapsViewModel = ViewModelProvider(this, ViewModelFactory.getInstance(dataStore))[MapsViewModel::class.java]

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        setMapStyle()

        settingsViewModel.getUser().observe(this@MapsActivity){
            mapsViewModel.getStoryWithLocation(it.token).observe(this@MapsActivity){ result ->
                if (result != null) {
                    when (result) {
                        is Result.Loading -> {
                            Toast.makeText(this@MapsActivity, getString(R.string.loading), Toast.LENGTH_SHORT).show()
                        }
                        is Result.Success -> {
                            val data = result.data
                            addMarker(data)
                            Toast.makeText(this@MapsActivity, getString(R.string.story_sukses), Toast.LENGTH_SHORT).show()
                        }
                        is Result.Error -> {
                            Toast.makeText(this@MapsActivity, getString(R.string.story_gagal), Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }

        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isIndoorLevelPickerEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true
    }

    private fun setMapStyle() {
        try {
            val success =
                mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style))
            if (!success) {
                Log.e(TAG, "Style parsing failed.")
            }
        } catch (exception: Resources.NotFoundException) {
            Log.e(TAG, "Can't find style. Error: ", exception)
        }
    }

    private fun addMarker(list: List<ListMapItem>) {
        for (data in list){
            val latLng = LatLng(data.lat!!.toDouble(), data.lon!!.toDouble())
            mMap.addMarker(MarkerOptions().position(latLng).title(data.name).snippet(data.description))
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
        }


    }

}