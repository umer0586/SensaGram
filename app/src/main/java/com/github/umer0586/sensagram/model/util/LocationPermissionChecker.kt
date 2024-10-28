package com.github.umer0586.sensagram.model.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build

interface LocationPermissionChecker {
    fun isLocationPermissionGranted(): Boolean
}

class LocationPermissionCheckerImp(private val context: Context) : LocationPermissionChecker {

    override fun isLocationPermissionGranted(): Boolean {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
            context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            )
            return false


        return true
    }

}