package com.shohiebsense.loclib

import android.content.Context
import android.location.Location
import android.util.Log
import java.sql.Date
import java.text.SimpleDateFormat

class LocationResultHelper(val context : Context, val locationList : List<Location>) {
    val preferenceManager = PreferenceManager(context)
    val CURRENT_TIMESTAMP_FORMAT = "HH:mm:ss"
    val CURRENT_DATE_FORMAT = "yyyy-MM-dd"

    fun saveResult(){
        if(locationList.isEmpty()){
            return
        }
        val location = locationList.last()
        preferenceManager.saveLatitude(location.latitude.toString())
        preferenceManager.saveLongitude(location.longitude.toString())
        preferenceManager.saveTime(longToTimeStamp(location.time, CURRENT_TIMESTAMP_FORMAT))
        preferenceManager.saveDate(longToTimeStamp(location.time, CURRENT_DATE_FORMAT))
    }


    fun longToTimeStamp(timeStamp : Long, format: String) : String{
        try {
            val sdf = SimpleDateFormat(format)
            val netDate = Date(timeStamp)
            return sdf.format(netDate)
        } catch (ex: Exception) {
            Log.e("shohiebsense","exception "+ex.toString())
            return android.text.format.DateFormat.format(CURRENT_TIMESTAMP_FORMAT, java.util.Date().time).toString()
        }
    }

}