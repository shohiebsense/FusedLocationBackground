# FusedLocationBackground
Running Fused Location on background


# Add FusedLocationBackground to your project
As the library is having its debut in 28/07. It had yet to upload to jcenter waiting for the approval. Add the following configuration to your Android project. In the root (project level) build.gradle file

```
maven {
         url "https://dl.bintray.com/shohiebsense/com.shohiebsense.loclib"
}
```

In your app level gradle:

```
dependencies{
    implementation 'com.shohiebsense.loclib:fusedlocationbackground:0.9.1'
}
```
Usage

In your activity file, implement LocationService.LocationServiceListener :

```
  lateinit var locationService : LocationService

   override fun onCreate(savedInstanceState: Bundle?) {
          locationService = LocationService(this,this).init()
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
        //this is called when the fused location service updates according to its interval..
     }


 ```
 
 
