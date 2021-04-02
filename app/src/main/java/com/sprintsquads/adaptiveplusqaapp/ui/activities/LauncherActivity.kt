package com.sprintsquads.adaptiveplusqaapp.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.sprintsquads.adaptiveplus.sdk.data.APLocation
import com.sprintsquads.adaptiveplusqaapp.R
import com.sprintsquads.adaptiveplusqaapp.data.Gender
import com.sprintsquads.adaptiveplusqaapp.data.Locale
import com.sprintsquads.adaptiveplusqaapp.ui.dialogs.AddCustomIPDialog
import com.sprintsquads.adaptiveplusqaapp.ui.dialogs.AddEnvDialog
import com.sprintsquads.adaptiveplusqaapp.utils.*
import kotlinx.android.synthetic.main.activity_launcher.*


class LauncherActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_launcher)

        updateEnvAdapter()
        updateIPsAdapter()

        agePicker.minValue = 0
        agePicker.maxValue = 100
        agePicker.value = 24

        maleRadioButton.isChecked = true

        val locales = listOf(
            Locale.EN.value,
            Locale.RU.value,
            Locale.KZ.value
        )

        localeSpinner.adapter = ArrayAdapter<String>(
            this, android.R.layout.simple_spinner_dropdown_item, locales)

        userIdEditText.setText("s10s_QA_user")

        startButton.setOnClickListener {
            openTestActivity()
        }

        addEnvBtn.setOnClickListener {
            showAddEnvDialog()
        }

        removeEnvBtn.setOnClickListener {
            val checkDialog = AlertDialog.Builder(this)
                .setMessage("Delete ${envSpinner.selectedItem} environment?")
                .setPositiveButton("YES") { _, _ ->
                    removeEnv(this, envSpinner.selectedItem.toString())
                    updateEnvAdapter()
                }
                .setNegativeButton("NO") { _, _ -> }
                .create()
            checkDialog.show()
        }

        addIpBtn.setOnClickListener {
            showAddCustomIPDialog()
        }

        removeIpBtn.setOnClickListener {
            val checkDialog = AlertDialog.Builder(this)
                .setMessage("Delete ${ipSpinner.selectedItem} IP address?")
                .setPositiveButton("YES") { _, _ ->
                    removeCustomIP(this, ipSpinner.selectedItem.toString())
                    updateIPsAdapter()
                }
                .setNegativeButton("NO") { _, _ -> }
                .create()
            checkDialog.show()
        }

        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
//                Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            // Get new FCM registration token
            val token = task.result

            // Log and toast
            fcmTokenValue.text = token
        })
    }

    private fun openTestActivity() {
        val intent = Intent(this, MainActivity::class.java)

        val envName = envSpinner.selectedItem.toString()

        val gender =
            if (maleRadioButton.isChecked) Gender.MALE.value
            else Gender.FEMALE.value

        intent.putExtra(MainActivity.EXTRA_ENV_NAME, envName)
        intent.putExtra(MainActivity.EXTRA_AGE, agePicker.value)
        intent.putExtra(MainActivity.EXTRA_GENDER, gender)
        intent.putExtra(MainActivity.EXTRA_USER_ID, userIdEditText.text.toString())
        intent.putExtra(MainActivity.EXTRA_LOCALE, localeSpinner.selectedItem.toString())
        intent.putExtra(MainActivity.EXTRA_CUSTOM_IP, getCustomIPBySpinnerName(this, ipSpinner.selectedItem.toString())?.ip)
        intent.putExtra(MainActivity.EXTRA_LOCATION, getAdaptiveLocation())

        startActivity(intent)
    }

    private fun showAddEnvDialog() {
        val dialog = AddEnvDialog.newInstance(object: AddEnvDialog.InteractionInterface {
            override fun onDismiss() {
                updateEnvAdapter()
            }
        })
        dialog.show(supportFragmentManager, dialog.tag)
    }

    private fun showAddCustomIPDialog() {
        val dialog = AddCustomIPDialog.newInstance(object: AddCustomIPDialog.InteractionInterface {
            override fun onDismiss() {
                updateIPsAdapter()
            }
        })
        dialog.show(supportFragmentManager, dialog.tag)
    }

    private fun updateEnvAdapter() {
        val envsNames = getEnvs(this).map { it.name }

        envSpinner.adapter = ArrayAdapter<String>(
            this, android.R.layout.simple_spinner_dropdown_item, envsNames)
    }

    private fun updateIPsAdapter() {
        val ipNames = getCustomIPs(this).map { "${it.name}: ${it.ip}" }

        ipSpinner.adapter = ArrayAdapter<String>(
            this, android.R.layout.simple_spinner_dropdown_item, ipNames)
    }

    private fun getAdaptiveLocation(): APLocation? {
        val lat = latitudeEditText.text.toString().toDoubleOrNull()
        val lon = longitudeEditText.text.toString().toDoubleOrNull()

        if (lat != null && lon != null) {
            return APLocation(latitude = lat, longitude = lon)
        }

        return null
    }

}