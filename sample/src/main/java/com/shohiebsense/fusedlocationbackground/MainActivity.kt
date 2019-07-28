package com.shohiebsense.fusedlocationbackground

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.shohiebsense.loclib.LocationResultObservable
import com.shohiebsense.loclib.service.LocationService
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity(), LocationService.LocationServiceListener {


    lateinit var locationService : LocationService


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        locationService = LocationService(this,this).init()
        setListenerOnFields()

    }

    fun setListenerOnFields(){
        button_latitude.setOnClickListener {
            text_latitude.text = locationService.getLatitude() + " " + locationService.getTime()
        }

        button_longitude.setOnClickListener {
            text_longitude.text = locationService.getLongitude()+ " " + locationService.getTime()
        }
    }

    override fun onResume() {
        super.onResume()
        locationService.onResume()
    }

    override fun onDestroy() {
        locationService.onDestroy()
        super.onDestroy()
    }

    override fun onGettingLocation(isImmediateExit: Boolean, time: String, latitude: String, longitude: String) {
        text_indicator.text = "$latitude  $longitude  $time"
    }



    /*override fun update(p0: Observable?, p1: Any?) {
        Log.e("shohiebsense ","update")
        val latitude = locationService.getLatitude()
        val longitude = locationService.getLongitude()
        val time = locationService.getTime()
        text_indicator.text = "$latitude  $longitude  $time"
    }*/
}
