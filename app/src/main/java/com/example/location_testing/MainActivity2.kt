package com.example.location_testing

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import com.example.location_testing.databinding.ActivityMain2Binding
import fr.quentinklein.slt.LocationTracker
import fr.quentinklein.slt.ProviderError

class MainActivity2 : AppCompatActivity() {
    private lateinit var binding: ActivityMain2Binding
    private lateinit var locationTracker: LocationTracker

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main2)
        locationTracker = LocationTracker(
            minTimeBetweenUpdates = 1 * 5 * 1000.toLong(),
            minDistanceBetweenUpdates = 1f,
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
                    this@MainActivity2, "Location => Latitude: ${location.latitude}" +
                            " Longitude: ${location.longitude}", Toast.LENGTH_LONG
                ).show()
            }

            override fun onProviderError(providerError: ProviderError) {
                Toast.makeText(
                    this@MainActivity2,
                    "Error: ${providerError.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }

        })
    }
}