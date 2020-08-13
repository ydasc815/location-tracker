package com.example.location_testing

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentActivity
import com.example.location_testing.databinding.ActivityMainBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import fr.quentinklein.slt.LocationTracker
import fr.quentinklein.slt.ProviderError
import java.time.LocalDateTime

class MainActivity : FragmentActivity(), OnMapReadyCallback {
    private lateinit var binding: ActivityMainBinding
    private lateinit var locationTracker: LocationTracker

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        val intent = Intent(this, LocationService::class.java)
        intent.action = LocationService.ACTION_START_FOREGROUND_SERVICE

        startService(intent)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent)
        }


        var mapFragment: SupportMapFragment? =
            supportFragmentManager.findFragmentById(R.id.google_map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
        binding.progressBar.visibility = View.VISIBLE
        binding.progressText.visibility = View.VISIBLE
    }

    @SuppressLint("MissingPermission")
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onMapReady(p0: GoogleMap?) {
        locationTracker = LocationTracker(
            minTimeBetweenUpdates = 1 * 2 * 1000.toLong(),
            minDistanceBetweenUpdates = 0f,
            shouldUseGPS = true,
            shouldUseNetwork = true,
            shouldUsePassive = true
        )
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                ), 1
            )
        } else {
            locationTracker.startListening(this)

        }
        locationTracker.addListener(object : LocationTracker.Listener {


            override fun onLocationFound(location: Location) {

                Toast.makeText(
                    this@MainActivity, "Location => Latitude: ${location.latitude}" +
                            " Longitude: ${location.longitude}", Toast.LENGTH_LONG
                ).show()
                p0.let {
                    it?.addMarker(
                        MarkerOptions().position(
                            LatLng(
                                location.latitude,
                                location.longitude
                            )
                        ).title("current position")
                    )
                    it?.animateCamera(
                        CameraUpdateFactory.newLatLngZoom(
                            LatLng(
                                location.latitude,
                                location.longitude
                            ), 16f
                        )
                    )
                    binding.progressBar.visibility = View.GONE
                    binding.progressText.visibility = View.GONE
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        Log.d("test", LocalDateTime.now().toString())
                    }
                    Toast.makeText(
                        this@MainActivity,
                        "lat  ${location.latitude}  long ${location.longitude}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onProviderError(providerError: ProviderError) {
                Toast.makeText(
                    this@MainActivity,
                    "Error: ${providerError.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        val intent = Intent(this@MainActivity, LocationService::class.java)
        intent.action = LocationService.ACTION_STOP_FOREGROUND_SERVICE
        startService(intent)
    }

}