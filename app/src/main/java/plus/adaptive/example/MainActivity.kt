package plus.adaptive.example

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import plus.adaptive.sdk.AdaptivePlusSDK


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        AdaptivePlusSDK().start(this)
    }

}