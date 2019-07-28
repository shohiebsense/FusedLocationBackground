package com.shohiebsense.loclib

import android.content.Context
import android.location.Location

class LocationRequestHelper {

   companion object{
       fun setRequesting(context: Context, isRequesting: Boolean){
           PreferenceManager(context).saveIsRequesting(isRequesting)
       }

       fun getIsRequesting(context: Context){
           PreferenceManager(context).getIsRequesting()
       }
   }

}