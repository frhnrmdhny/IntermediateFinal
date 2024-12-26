package com.dicoding.picodiploma.loginwithanimation.ui.maps

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.dicoding.picodiploma.loginwithanimation.R
import com.dicoding.picodiploma.loginwithanimation.data.remote.response.ListStoryItem
import com.dicoding.picodiploma.loginwithanimation.databinding.ActivityMapsBinding
import com.dicoding.picodiploma.loginwithanimation.di.Injection
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var binding: ActivityMapsBinding
    private lateinit var googleMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val mapFragment =
            supportFragmentManager.findFragmentById(R.id.map) as com.google.android.gms.maps.SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map

        googleMap.uiSettings.isZoomControlsEnabled = true
        val storyRepository = Injection.provideStoryRepository(this)
        lifecycleScope.launchWhenCreated {
            val storiesWithLocation = storyRepository.getStoriesWithLocation()

            for (story in storiesWithLocation) {
                addStoryMarker(story)
            }


            if (storiesWithLocation.isNotEmpty()) {
                val firstStoryLocation = LatLng(
                    storiesWithLocation[0].lat ?: 0.0,
                    storiesWithLocation[0].lon ?: 0.0
                )
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(firstStoryLocation, 10f))
            }
        }
    }

    private fun addStoryMarker(story: ListStoryItem) {
        val location = LatLng(story.lat ?: 0.0, story.lon ?: 0.0)
        val markerOptions = MarkerOptions()
            .position(location)
            .title(story.name)
            .snippet(story.description)
        googleMap.addMarker(markerOptions)
    }
}
