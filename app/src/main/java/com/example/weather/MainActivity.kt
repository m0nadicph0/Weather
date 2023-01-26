package com.example.weather

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.location.*
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener


class MainActivity : AppCompatActivity(), MultiplePermissionsListener, LocationListener {
    private lateinit var locationClient: FusedLocationProviderClient
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        locationClient = LocationServices.getFusedLocationProviderClient(this)
        setContentView(R.layout.activity_main)

        if (!isLocationEnabled()) {
            notifyLocationDisabled()
        } else {
            askForPermissions()
        }
    }

    private fun askForPermissions() {
        Dexter.withContext(this)
            .withPermissions(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ).withListener(this).onSameThread().check()
    }

    private fun notifyLocationDisabled() {
        Toast.makeText(this, "Your location is turned off. Please turn it on.", Toast.LENGTH_LONG)
            .show()
        startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
    }

    private fun isLocationEnabled():Boolean {
        val lm = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return lm.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
        if(report!!.areAllPermissionsGranted()) {
            findViewById<TextView>(R.id.tv_message).text = "All permissions granted, the app is ready for use."
            requestLocationData()
        } else {
            findViewById<TextView>(R.id.tv_message).text = "Permissions denied, the app is not ready for use."
        }
    }

    @SuppressLint("MissingPermission")
    fun requestLocationData() {
        var request = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000).build()
        locationClient.requestLocationUpdates(request, this, Looper.myLooper())
    }



    override fun onPermissionRationaleShouldBeShown(
        requests: MutableList<PermissionRequest>?,
        token: PermissionToken?
    ) {
        AlertDialog.Builder(this)
            .setTitle("Permissions")
            .setMessage("Required permissions must me enabled for this app to work")
            .setPositiveButton("Go to Settings"){_, _ ->
                var intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                intent.data = Uri.fromParts("package", packageName, null)
                startActivity(intent)
            }
            .setNegativeButton("Cancel"){dlg, _ -> dlg.dismiss()}
            .show()
    }

    override fun onLocationChanged(location: Location) {
        Log.i("LOCATION", "onLocationChanged: latitude = ${location.latitude}, longitude = ${location.longitude}")
    }
}