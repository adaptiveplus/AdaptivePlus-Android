<img src="https://user-images.githubusercontent.com/79895718/120859295-d3493c80-c5a5-11eb-8975-91b92ccc4d27.png" width="72" height="72" alt="AdaptivePlus">

# [AdaptivePlus Android SDK](https://adaptive.plus/)

[**AdaptivePlus**](https://adaptive.plus/) is the control center for marketing campaigns in mobile applications

## Requirements
- minSdkVersion 16

*Examples provided in Kotlin programming language*

## Installation
Add the following dependency to your app's `build.gradle` file:

```groovy
dependencies {
    implementation 'plus.adaptive:android-sdk:2.1.1'
}
```

### Maven central
Add the following to your root build.gradle file
```groovy
allprojects {
    repositories {
      mavenCentral()
    }
}
```

*Do not forget to sync project with gradle files afterwards*

## Initialization
Register an account in the admin panel of [AdaptivePlus](https://adaptive.plus/)

Initialize AdaptivePlusSDK on app startup and pass the **API key** that you received upon account registration

```kotlin
// App StartUp
class YourApp: Application() {
    override fun onCreate() {
        super.onCreate()

        AdaptivePlusSDK
            .init(apiKey = "your api key")
    }
}
```

### Initialization Exception
SDK throws `APInitializationException` on `newInstance` method call if **adaptivePlusApiKey** is not provided beforehand via `init` method

## Splash Screen
Show AdaptivePlus Splash Screen on app startup or after user logs in (or at any suitable moment)

```kotlin
class MainActivity: Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        AdaptivePlusSDK
            .newInstance(context)
            .showSplashScreen()
    }
}
```

Now, you can visit the admin panel and create some content. Do not forget to change the status of the content to **active**. 
On the first `showSplashScreen` method call (first app launch), the SDK preloads the splash screen contents to your device, and will have a loading delay before displaying it. On the subsequent method calls, probably, the splash screen contents are already preloaded, and the splash screen will be displayed on the screen instantly

If you are not able to observe the created content - probable reasons are:
- You forgot to activate the content in the AdaptivePlus admin panel
- Check again the integration guide, maybe you missed something out
- The SDK couldn't preload the contents on the previous `showSplashScreen` method calls due to network issues or internal sdk issues

### Personalized Experience
In order to make SDK experience more personalized, you can provide following user data:
```kotlin
AdaptivePlusSDK
    .newInstance(context)
    .setUserId(userId)
    .setUserProperties(userProperties)
    .setLocation(userLocation)
    .showSplashScreen()
```
`userId: String` - id assigned to the user by your own system/service, useful for identifying the same user across multiple devices\
`userProperties: Map<String, String>` - user properties, e.g. - age, gender, etc. User properties help SDK to select and show content relevant to the user. Ex:
```kotlin
val userProperties = mapOf("age" to "25", "gender" to "male")
```
`userLocation: APLocation` - user location (latitude & longitude). Required if you want to display geo-oriented content to the user
```kotlin
data class APLocation(
    val latitude: Double,
    val longitude: Double
) : Serializable
```

### Splash Screen Draft Campaigns
To take a look at splash screen campaigns that are on moderation (not active) state pass `hasDrafts` parameter as `true` to `showSplashScreen` method:
```kotlin
AdaptivePlusSDK
    .newInstance(context)
    .showSplashScreen(hasDrafts = true)
```

### Splash Screen Listener
```kotlin
interface APSplashScreenListener {
    fun onFinish() {}
    fun onRunAPCustomAction(params: HashMap<String, Any>) {}
}
```
For listening of the splash screen events - you should provide your implementation of `APSplashScreenListener`:
```kotlin
AdaptivePlusSDK
    .newInstance(context)
    .setSplashScreenListener(
        object: APSplashScreenListener {
            override fun onFinish() {
                // TODO: actions to do on the splash screen finish
            }

            override fun onRunAPCustomAction(params: HashMap<String, Any>) {
                // TODO: your implementation of Adaptive Plus Custom Actions
            }
        }
    )
    .showSplashScreen()
```

## AdaptivePlus Debug Mode
To observe network logs of the SDK - pass `true` to `setIsDebuggable` method:
```kotlin
AdaptivePlusSDK
    .newInstance(context)
    .setIsDebuggable(true)
```
Do not forget to switch *Debug Mode* off for the release build of your app.

## Permissions
We include the [INTERNET](http://developer.android.com/reference/android/Manifest.permission.html#INTERNET) permission by default as we need it to make network requests:
```xml
<uses-permission android:name="android.permission.INTERNET" />
```
will be added to the manifest file during the app build process

## Dependency graph

Here is our complete dependency graph:
```
# Transitive (shared with your app)
org.jetbrains.kotlin:kotlin-stdlib:1.4.31

androidx.core:core-ktx:1.3.2
androidx.appcompat:appcompat:1.2.0
androidx.lifecycle:lifecycle-extensions:2.2.0
androidx.recyclerview:recyclerview:1.2.0
androidx.constraintlayout:constraintlayout:2.1.0-beta01
androidx.cardview:cardview:1.0.0

com.google.android.material:material:1.3.0
com.google.code.gson:gson:2.8.6

com.squareup.okhttp3:okhttp:4.9.1
com.squareup.okhttp3:logging-interceptor:4.9.1

com.github.bumptech.glide:glide:4.12.0
com.github.bumptech.glide:okhttp3-integration:4.12.0
com.github.bumptech.glide:compiler:4.12.0
```

### Transitive Dependencies
AdaptivePlus Android SDK transitively depends on the above libraries. If your app is using any one of these libraries, they should at least be on the same major version that AdaptivePlus SDK is using.
When there are two versions of a library at build time, Gradle automatically picks the newer version. 
This means if you are currently using say Glide 3.x, your app would automatically get Glide 4.x after including AdaptivePlus.