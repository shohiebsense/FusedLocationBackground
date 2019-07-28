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
