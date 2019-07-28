package com.shohiebsense.loclib

import android.content.Context
import android.util.Log
import java.sql.Date
import java.sql.Timestamp
import java.text.SimpleDateFormat

class FusedLocationResult(val context : Context) {
    private val preferenceManager = PreferenceManager(context)





    fun getLatitude(): String{
        return preferenceManager.getLatitude()
    }

    fun getLongitude(): String{
        return preferenceManager.getLongitude()
    }

    fun getTime() : String{
        return preferenceManager.getTime()
    }
}