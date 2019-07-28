package com.shohiebsense.loclib.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.location.Location
import android.util.Log
import com.google.android.gms.location.LocationResult
import com.shohiebsense.loclib.LocationResultCallback
import com.shohiebsense.loclib.LocationResultHelper
import com.shohiebsense.loclib.LocationResultObservable

class LocationUpdateBroadcastReceiver() : BroadcastReceiver(){

    companion object {
        val TAG = LocationUpdateBroadcastReceiver::class.java.simpleName
        val ACTION_PROCESS_UPDATES = "com.shohiebsense.loclib.action.PROCESS_UPDATES"
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        if(intent == null){
            return
        }
        val action = intent.action
        if(!action.equals(ACTION_PROCESS_UPDATES)){
            return
        }
        val locationResult = LocationResult.extractResult(intent) ?: return
        val locationList = locationResult.locations
        if(context == null){
            return
        }
        val locationResultHelper = LocationResultHelper(context, locationList)
        locationResultHelper.saveResult()
        LocationResultObservable.instance.updateValue(intent)
    }


}