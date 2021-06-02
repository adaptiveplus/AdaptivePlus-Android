package plus.adaptive.example

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import plus.adaptive.sdk.AdaptivePlusSDK
import plus.adaptive.sdk.data.listeners.APSplashScreenListener
import plus.adaptive.sdk.data.models.APLocation


class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // Default Use Case
        AdaptivePlusSDK
            .newInstance(this)
            .showSplashScreen()

        // User-Centric Use Case
        AdaptivePlusSDK
            .newInstance(this)
            .setUserId("app_user_id")
            .setUserProperties(
                mapOf("age" to "25", "gender" to "male")
            )
            .setLocation(
                APLocation(
                    latitude = 37.7749,
                    longitude = 122.4194
                )
            )
            .showSplashScreen()

        // Drafts Enabled Mode Use Case
        AdaptivePlusSDK
            .newInstance(this)
            .showSplashScreen(hasDrafts = true)

        // Splash Screen Listener Use Case
        AdaptivePlusSDK
            .newInstance(this)
            .setSplashScreenListener(
                object: APSplashScreenListener {
                    override fun onFinish() {
                        // TODO: actions to do on the splash screen finish
                    }

                    override fun onRunAPCustomAction(params: HashMap<String, Any>) {
                        // TODO: your implementation of Adaptive Plus Custom Action
                    }
                }
            )
            .showSplashScreen()

        // All-in Use Case
        AdaptivePlusSDK
            .newInstance(this)
            .setUserId("app_user_id")
            .setUserProperties(
                mapOf("age" to "25", "gender" to "male")
            )
            .setLocation(
                APLocation(
                    latitude = 37.7749,
                    longitude = 122.4194
                )
            )
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
            .showSplashScreen(hasDrafts = true)
    }

}