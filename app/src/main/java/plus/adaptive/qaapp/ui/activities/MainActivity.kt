package plus.adaptive.qaapp.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.FragmentManager
import plus.adaptive.sdk.AdaptivePlusSDK
import plus.adaptive.sdk.data.models.APLocation
import plus.adaptive.qaapp.R
import plus.adaptive.qaapp.data.APSdkEnvironment
import plus.adaptive.qaapp.data.Environment
import plus.adaptive.qaapp.ui.fragments.ApiFragment
import plus.adaptive.qaapp.ui.fragments.MockFragment
import plus.adaptive.qaapp.utils.getEnvByName


class MainActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_ENV_NAME = "extra_env_name"
        const val EXTRA_USER_ID = "extra_user_id"
        const val EXTRA_GENDER = "extra_gender"
        const val EXTRA_AGE = "extra_age"
        const val EXTRA_LOCALE = "extra_locale"
        const val EXTRA_CUSTOM_IP = "extra_custom_ip"
        const val EXTRA_LOCATION = "extra_location"
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val envName = intent?.getStringExtra(EXTRA_ENV_NAME) ?: Environment.MOCK.value
        val env = getEnvByName(this, envName)

        supportActionBar?.title = envName
        supportActionBar?.subtitle = env?.baseApiUrl

        if (env == null) {
            finish()
            return
        }

        var customIP = intent?.getStringExtra(EXTRA_CUSTOM_IP)
        if (customIP.isNullOrEmpty()) {
            customIP = null
        }

        val locale = intent?.getStringExtra(EXTRA_LOCALE) ?: "ru"
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

        AdaptivePlusSDK().apply {
            setTestEnvironment(
                context = this@MainActivity,
                channelSecret = env.channelSecret,
                baseUrl = env.baseApiUrl,
                customIP = customIP
            )

            start(
                context = this@MainActivity,
                userId = userId,
                userProperties = userProperties,
                location = location,
                locale = locale,
                isDebuggable = true
            )
        }

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
    }

    override fun onDestroy() {
        AdaptivePlusSDK().stop()
        super.onDestroy()
    }
}