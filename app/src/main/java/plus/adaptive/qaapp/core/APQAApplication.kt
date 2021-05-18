package plus.adaptive.qaapp.core

import androidx.multidex.MultiDexApplication
import plus.adaptive.sdk.AdaptivePlusSDK


class APQAApplication : MultiDexApplication() {

    override fun onCreate() {
        super.onCreate()

        AdaptivePlusSDK.init(
            apiKey = "7f5kpYzMgApEncRK")
    }
}