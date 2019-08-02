package com.shohiebsense.loclib.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationManager
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
        Log.e("receiver ","intent is null "+(intent == null).toString())
        if(intent == null){
            return
        }
        val action = intent.action
        if(!action.equals(ACTION_PROCESS_UPDATES)){
            return
        }
        Log.e("receiver ","intent action "+action.toString())
        Log.e("receiver ","extract result "+LocationResult.extractResult(intent))
        if(context == null){
            return
        }
        //#OPT 0
        var locationList = arrayListOf<Location>()


        if(intent.extras == null) return

        var location : Location?  = intent.extras!!.get(LocationManager.KEY_LOCATION_CHANGED) as Location? ?: return
        locationList.add(location!!)
        //#OPT 1
        /*val locationResult = LocationResult.extractResult(intent) ?: return
        val locationList = locationResult.locations*/

        val locationResultHelper = LocationResultHelper(context, locationList)
        locationResultHelper.saveResult()
        Log.e("receiver ",location.latitude.toString()+"   "+location.longitude)
        LocationResultObservable.instance.updateValue(intent)
    }


}