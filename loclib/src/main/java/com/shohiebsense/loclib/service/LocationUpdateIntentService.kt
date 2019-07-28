package com.shohiebsense.loclib.service

import android.app.IntentService
import android.content.Intent
import android.util.Log
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.shohiebsense.loclib.LocationResultCallback
import com.shohiebsense.loclib.LocationResultHelper

class LocationUpdateIntentService() : IntentService(TAG) {
    companion object {
        val TAG = LocationUpdateIntentService::class.java.simpleName
        val ACTION_PROCESS_UPDATES = "com.shohiebsense.loclib.action.PROCESS_UPDATES"
    }

    override fun onHandleIntent(intent: Intent?) {
        if(intent == null) return
        val action = intent.action
        if(!action.equals(ACTION_PROCESS_UPDATES)) return
        val locationResult = LocationResult.extractResult(intent)
        val locationList = locationResult.locations
        val locationResultHelper = LocationResultHelper(this, locationList)
        locationResultHelper.saveResult()
    }

}