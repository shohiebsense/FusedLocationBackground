package com.shohiebsense.loclib.service

import android.Manifest
import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.os.Parcelable
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.shohiebsense.loclib.FusedLocationResult
import com.shohiebsense.loclib.LocationRequestHelper
import com.shohiebsense.loclib.LocationResultObservable

import java.text.DateFormat
import java.util.*

class LocationService(
    var listener: LocationServiceListener,
    var context: AppCompatActivity) : Observer, GoogleApiClient.ConnectionCallbacks,
    GoogleApiClient.OnConnectionFailedListener,  LocationListener {

    //Not using GoogleAPICLIENT Anymore
    //Not using Fusedlocationprovider client
    //Instead using locationManager


    companion object {
        val REQUEST_CODE_PERMISSION = 33
        val SETTINGS_REQUEST_CODE = 0X1
        val UPDATE_INTERVAL_IN_MS = 8000L
        val FASTEST_UPDATE_INTERVAL_IN_MS = UPDATE_INTERVAL_IN_MS / 2

        val KEY_REQUESTING_LOCATION_UPDATES = "requesting-location-updates"
        val KEY_LOCATION = "location"
        val KEY_LAST_UPDATED_TIME_STRING = "last_updated_time_string"
    }

    private var fusedLocationProviderClient: FusedLocationProviderClient? = null
    private var settingsClient: SettingsClient? = null
    private var locationRequest: LocationRequest? = null
    private var locationSettingsRequest: LocationSettingsRequest? = null
    private var locationCallback: LocationCallback? = null

    private var currentLocation: Location? = null
    private var lastUpdateTime: String? = ""
    private var savedInstanceState: Bundle? = null
    private var isExecuting = false
    private var isImmediateExit = false
    private val fusedLocationResult = FusedLocationResult(context)

    private var mGoogleApiClient: GoogleApiClient? = null
    private var locationManager : LocationManager? = null

    val isLocationPermissionGranted: Boolean
        get() {
            val permissionState = ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
            return permissionState == PackageManager.PERMISSION_GRANTED
        }

    init {
        val permissionList = arrayListOf<String>()
        permissionList.add(getLocationPermissionList())
        if (permissionList.isNotEmpty() && permissionList.get(0).isNotEmpty()) {
            doRequestLocationPermission(permissionList)
        }

    }

    fun init() : LocationService {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
        settingsClient = LocationServices.getSettingsClient(context)
        //createLocationCallback()
        createLocationRequest()
        buildLocationSettingsRequest()
        LocationResultObservable.instance.addObserver(this)
        //buildGoogleApiClient()
        locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return this
    }

    fun doRequestLocationPermission(permissions: List<String>) {
        ActivityCompat.requestPermissions(
            context,
            permissions.toTypedArray(), REQUEST_CODE_PERMISSION
        )
    }


    fun updateLocationAndExit() {
        isImmediateExit = true
        if(isExecuting){
            isExecuting = false
            return
        }
       onResume()
    }

    fun getLocationPermissionList(): String {
        return if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) !== PackageManager.PERMISSION_GRANTED
        ) {
            Manifest.permission.ACCESS_FINE_LOCATION
        } else ""
    }




    fun createLocationCallback() {
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                super.onLocationResult(locationResult)
                currentLocation = locationResult!!.lastLocation
                lastUpdateTime = DateFormat.getTimeInstance().format(Date())
                Log.e("shohiebsense ","callback result "+currentLocation!!.latitude)
            }
        }
    }

    internal fun createLocationRequest() {
        locationRequest = LocationRequest()
        locationRequest!!.interval = UPDATE_INTERVAL_IN_MS
        locationRequest!!.fastestInterval = FASTEST_UPDATE_INTERVAL_IN_MS
        locationRequest!!.priority = LocationRequest.PRIORITY_NO_POWER
        locationRequest!!.smallestDisplacement = 5F
        locationRequest!!.maxWaitTime = UPDATE_INTERVAL_IN_MS
    }

    private fun getPendingIntent(): PendingIntent {
        val intent = Intent(context, LocationUpdateBroadcastReceiver::class.java)
        intent.setAction(LocationUpdateBroadcastReceiver.ACTION_PROCESS_UPDATES)
        return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    fun buildLocationSettingsRequest() {
        val builder = LocationSettingsRequest.Builder()
        builder.addLocationRequest(locationRequest!!)
        locationSettingsRequest = builder.build()
    }

    /*private fun buildGoogleApiClient() {
        if (mGoogleApiClient != null) {
            return
        }
        mGoogleApiClient = GoogleApiClient.Builder(context)
            .addConnectionCallbacks(this)
            .enableAutoManage(context, this)
            .addApi(LocationServices.API)
            .build()
    }*/


    fun onDestroy() {
        if(fusedLocationProviderClient == null) return
        //#OPT 1
        LocationRequestHelper.setRequesting(context, false)
        locationManager?.removeUpdates(getPendingIntent())

        /*if(mGoogleApiClient!!.isConnected){
            LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient,
                getPendingIntent()
            )
        }*/




        //#OPT 2
        /*fusedLocationProviderClient!!.removeLocationUpdates(getPendingIntent())
            .addOnCompleteListener {

                LocationRequestHelper.setRequesting(context, false)
            }*/
    }



    fun onResume() {
        if (isLocationPermissionGranted) {
            LocationRequestHelper.getIsRequesting(context)
            startLocationUpates()
            Log.e("locations","start location updates")
        } else if (!isLocationPermissionGranted) {
            Toast.makeText(context, "Location Permission Is Not Accepted", Toast.LENGTH_SHORT).show()
            return
        }
    }


    @SuppressLint("MissingPermission")
    internal fun startLocationUpates() {
        LocationRequestHelper.setRequesting(context, true)
        //$0 OPTION 0
        locationManager!!.requestLocationUpdates(
            LocationManager.GPS_PROVIDER, 4000, 10F, getPendingIntent()
        )

        //#1 OPTION 1

        /*if(mGoogleApiClient!!.isConnected){
            Log.e("loc ser","is connected true")
            LocationServices.FusedLocationApi.requestLocationUpdates(
                LocationManager.GPS_PROVIDER, 0,0, getPendingIntent()
            )
        }
        else{
            Log.e("loc servi","not conn")
        }
*/

        //#OPTION 2

        /*settingsClient!!.checkLocationSettings(locationSettingsRequest)
            .addOnSuccessListener {
                Log.e("locations ","settings client succ")
                fusedLocationProviderClient!!.requestLocationUpdates(
                    locationRequest,
                    getPendingIntent()
                )
            }.addOnFailureListener { e ->
                val statusCode = (e as ApiException).statusCode
                when (statusCode) {
                    LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> try {
                        (e as ResolvableApiException).startResolutionForResult(context, SETTINGS_REQUEST_CODE)
                    } catch (exception: IntentSender.SendIntentException) {
                        Log.e("exception ",exception.toString())
                    }

                }
            }.addOnCompleteListener {
                Log.e("complete ","settings client succ")
            }*/
    }


    interface LocationServiceListener {
        fun onGettingLocation(isImmediateExit : Boolean, time: String, latitude: String, longitude: String)
    }

    fun getLatitude() : String{
        var latitude = fusedLocationResult.getLatitude()
        if(latitude.isEmpty()){
            updateLocationAndExit()
        }
        return latitude
    }

    fun getLongitude() : String{
        var longitude = fusedLocationResult.getLongitude()
        if(longitude.isEmpty()){
            updateLocationAndExit()
        }
        return longitude
    }

    fun getTime() : String{
        var time = fusedLocationResult.getTime()
        if(time.isEmpty()){
            updateLocationAndExit()
        }
        return time
    }

    fun getDate() : String{
        var date = fusedLocationResult.getDate()
        if(date.isEmpty()){
            updateLocationAndExit()
        }
        return date
    }


    override fun update(p0: Observable?, p1: Any?) {
        val latitude = getLatitude()
        val longitude = getLongitude()
        val time = getTime()
        val date = getDate()
        listener.onGettingLocation(false, "$date $time" , latitude, longitude)
    }

    override fun onConnectionFailed(p0: ConnectionResult) {
        Log.e("LocationSer ","failed conn")

    }

    @SuppressLint("MissingPermission")
    override fun onConnected(p0: Bundle?) {
        Log.e("LocationSer ","onConnected")
        /*LocationServices.FusedLocationApi.requestLocationUpdates(
            mGoogleApiClient, locationRequest, this
        )*/
    }

    override fun onConnectionSuspended(p0: Int) {
        Log.e("LocationSer ","conn susp")
    }

    override fun onLocationChanged(location: Location?) {
        Log.e("locservic","long "+location!!.longitude+"  "+location!!.latitude)
    }



}
