package plus.adaptive.example

import android.app.Application
import plus.adaptive.sdk.AdaptivePlusSDK


class MyApp : Application() {

    override fun onCreate() {
        super.onCreate()

        AdaptivePlusSDK.init(
            apiKey = "your api key")
    }
}