package plus.adaptive.qaapp.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import plus.adaptive.qaapp.R
import plus.adaptive.qaapp.data.APSdkEnvironment
import plus.adaptive.qaapp.data.Environment
import plus.adaptive.qaapp.data.Language
import plus.adaptive.qaapp.ui.fragments.ApiFragment
import plus.adaptive.qaapp.ui.fragments.MockFragment
import plus.adaptive.qaapp.utils.getEnvByName
import plus.adaptive.sdk.AdaptivePlusSDK
import plus.adaptive.sdk.data.listeners.APSplashScreenListener
import plus.adaptive.sdk.data.models.APLocation
import java.util.*


class MainActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_ENV_NAME = "extra_env_name"
        const val EXTRA_USER_ID = "extra_user_id"
        const val EXTRA_GENDER = "extra_gender"
        const val EXTRA_AGE = "extra_age"
        const val EXTRA_LANGUAGE = "extra_language"
        const val EXTRA_CUSTOM_IP = "extra_custom_ip"
        const val EXTRA_LOCATION = "extra_location"
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val envName = intent?.getStringExtra(EXTRA_ENV_NAME) ?: Environment.MOCK.value
        val env = getEnvByName(this, envName)

        supportActionBar?.title = envName

        if (env == null) {
            finish()
            return
        }

        var customIP = intent?.getStringExtra(EXTRA_CUSTOM_IP)
        if (customIP.isNullOrEmpty()) {
            customIP = null
        }

        val locale = when (intent?.getStringExtra(EXTRA_LANGUAGE)) {
            Language.RU.value -> Locale("ru","RU")
            Language.EN.value -> Locale.ENGLISH
            Language.KZ.value -> Locale("kk", "KZ")
            else -> Locale.ENGLISH
        }
        val userId = intent?.getStringExtra(EXTRA_USER_ID)

        val userProperties = mutableMapOf<String, String>()
        intent?.getStringExtra(EXTRA_GENDER)?.let {
            userProperties.put("gender", it)
        }
        if (intent?.hasExtra(EXTRA_AGE) == true) {
            intent?.getIntExtra(EXTRA_AGE, 0)?.let {
                userProperties.put("age", it.toString())
            }
        }

        val location = intent?.getSerializableExtra(EXTRA_LOCATION) as? APLocation

        AdaptivePlusSDK.init(env.apiKey)
        AdaptivePlusSDK.setCustomIP(this, customIP)

        AdaptivePlusSDK
            .newInstance(this)
            .setUserId(userId)
            .setUserProperties(userProperties)
            .setLocation(location)
            .setLocale(locale)
            .setIsDebuggable(true)
            .start()

        if (envName == Environment.MOCK.value) {
            showMockFragment()
        } else {
            showApiFragment(env)
        }
    }

    private fun showApiFragment(env: APSdkEnvironment) {
        supportFragmentManager.popBackStack(
            null,
            FragmentManager.POP_BACK_STACK_INCLUSIVE
        )

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.content, ApiFragment.newInstance(env.name))
            .commit()

        AdaptivePlusSDK
            .newInstance(this)
            .setSplashScreenListener(
                object: APSplashScreenListener {
                    override fun onFinish() {
                        Toast.makeText(
                            this@MainActivity,
                            "Launch Screen Finished",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            )
            .showSplashScreen()
    }

    private fun showMockFragment() {
        supportFragmentManager.popBackStack(
            null,
            FragmentManager.POP_BACK_STACK_INCLUSIVE
        )

        supportFragmentManager
            .beginTransaction()
            .replace(
                R.id.content,
                MockFragment.newInstance()
            )
            .commit()

        AdaptivePlusSDK
            .newInstance(this)
            .setSplashScreenListener(
                object: APSplashScreenListener {
                    override fun onFinish() {
                        Toast.makeText(
                            this@MainActivity,
                            "Launch Screen Finished",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            )
            .showMockSplashScreen()
    }

    override fun onDestroy() {
        AdaptivePlusSDK.newInstance(this).stop()
        super.onDestroy()
    }
}