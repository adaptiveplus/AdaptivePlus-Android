# [AdaptivePlus Android SDK](https://adaptive.plus/)

[**AdaptivePlus**](https://adaptive.plus/) is the control center for marketing campaigns in mobile applications

### Requirements
- minSdkVersion 16

*Examples provided in Kotlin programming language*

## Guide for integration of AdaptivePlus SDK

### Step 1
Include AdaptivePlus SDK dependency into app module **build.gradle**:

```groovy
dependencies {
    implementation 'plus.adaptive:android-sdk:2.1.1'
}
```

Do not forget to sync project with gradle files afterwards

### Step 2
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

### Step 3
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

## More information on the features of AdaptivePlus SDK

### SDK Permissions
Be aware that AdaptivePlus SDK requires internet to work properly, therefore
```xml
<uses-permission android:name="android.permission.INTERNET" />
```
will be added to the manifest file during the app build process

### Initialization Exception
SDK throws `APInitializationException` on `newInstance` method call if **adaptivePlusApiKey** is not provided beforehand via `init` method

### AdaptivePlus Personalized Experience
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
`userLocation: APLocation` - user location (latitude & longitude). Required to display geo-oriented content to the user
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

### AdaptivePlus Debug Mode
To observe network logs of the SDK - pass `true` to `setIsDebuggable` method:
```kotlin
AdaptivePlusSDK
    .newInstance(context)
    .setIsDebuggable(true)
```
Do not forget to switch *Debug Mode* off for the release build of your app.

##  Android SDK version - 2.1.0
1) Shows SDK generated Splash Screen with countdown timer: able to display Images & GIFs & Texts, execute simplest set of actions on click
2) Action list contains:\
(1) *Web URL Opening in WebView dialog window*,\
(2) *DeepLink call to Android Operating System*,\
(3) *Send SMS & Call Phone*,\
(4) *Custom Action (you should implement it, nothing will happen otherwise)*