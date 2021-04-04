package com.sprintsquads.adaptiveplusqaapp.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.FragmentManager
import com.sprintsquads.adaptiveplus.sdk.AdaptivePlusSDK
import com.sprintsquads.adaptiveplus.sdk.data.APLocation
import com.sprintsquads.adaptiveplusqaapp.R
import com.sprintsquads.adaptiveplusqaapp.data.APSdkEnvironment
import com.sprintsquads.adaptiveplusqaapp.data.Environment
import com.sprintsquads.adaptiveplusqaapp.ui.fragments.ApiFragment
import com.sprintsquads.adaptiveplusqaapp.ui.fragments.MockFragment
import com.sprintsquads.adaptiveplusqaapp.utils.getEnvByName


class MainActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_ENV_NAME = "extra_env_name"
        const val EXTRA_USER_ID = "extra_user_id"
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

        val location = intent?.getSerializableExtra(EXTRA_LOCATION) as? APLocation

        AdaptivePlusSDK().apply {
            setTestEnvironment(
                context = this@MainActivity,
                clientId = env.clientId,
                clientSecret = env.clientSecret,
                baseUrl = env.baseApiUrl,
                customIP = customIP
            )

            start(
                context = this@MainActivity,
                userId = userId,
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

}