package plus.adaptive.qaapp.utils

import android.content.Context
import com.google.gson.Gson
import plus.adaptive.qaapp.data.AdaptiveCustomIP
import plus.adaptive.qaapp.data.APSdkEnvironment
import plus.adaptive.qaapp.data.Environment
import java.io.Serializable


private data class CustomIPsWrapper(
    val ips: List<AdaptiveCustomIP>
) : Serializable

fun addCustomIP(context: Context, ip: AdaptiveCustomIP) {
    val ips = getCustomIPs(context).toMutableList()
    ips.add(ip)

    updateCustomIPs(context, ips)
}

fun removeCustomIP(context: Context, ipName: String) {
    val ips = getCustomIPs(context).toMutableList()
    val remIndex = ips.indexOfFirst { "${it.name}: ${it.ip}" == ipName }
    if (remIndex != -1) {
        ips.removeAt(remIndex)
    }

    updateCustomIPs(context, ips)
}

fun getCustomIPBySpinnerName(context: Context, ipName: String): AdaptiveCustomIP? {
    val ips = getCustomIPs(context)

    return ips.firstOrNull { "${it.name}: ${it.ip}" == ipName }
}

private fun updateCustomIPs(context: Context, ips: List<AdaptiveCustomIP>) {
    val prefs = AppSharedPreferences(context)
    val jsonStr = Gson().toJson(CustomIPsWrapper(ips))

    prefs.saveString(AppSharedPreferences.ADAPTIVE_CUSTOM_IPS, jsonStr)
}

fun getCustomIPs(context: Context): List<AdaptiveCustomIP> {
    val prefs = AppSharedPreferences(context)
    val ipsWrapperStr = prefs.getString(AppSharedPreferences.ADAPTIVE_CUSTOM_IPS)

    val ips = try {
        Gson().fromJson(ipsWrapperStr, CustomIPsWrapper::class.java).ips
    } catch (e: Exception) {
        e.printStackTrace()
        listOf()
    }.toMutableList()

    if (ips.firstOrNull { it.name == "NONE" } == null) {
        ips.add(
            AdaptiveCustomIP(name = "NONE", ip = "")
        )
    }

    return ips
}

private data class EnvsWrapper(
    val envs: List<APSdkEnvironment>
) : Serializable

fun addNewAPView(
    context: Context,
    envName: String,
    apViewId: String,
    hasDrafts: Boolean
) {
    val envs = getEnvs(context).toMutableList()
    envs.firstOrNull { it.name == envName }?.run {
        apViews = apViews.toMutableList().apply {
            add(
                APSdkEnvironment.APView(
                    id = apViewId,
                    hasDrafts = hasDrafts
                )
            )
        }
    }

    updateEnvs(context, envs)
}

fun removeAPView(context: Context, envName: String, apViewId: String) {
    val envs = getEnvs(context).toMutableList()
    envs.firstOrNull { it.name == envName }?.run {
        apViews = apViews.toMutableList().apply {
            val index = indexOfFirst { it.id == apViewId }
            if (index != -1) {
                removeAt(index)
            }
        }
    }

    updateEnvs(context, envs)
}

fun addNewEnv(context: Context, env: APSdkEnvironment) {
    val envs = getEnvs(context).toMutableList()
    envs.add(env)

    updateEnvs(context, envs)
}

fun removeEnv(context: Context, envName: String) {
    val envs = getEnvs(context).toMutableList()
    val remIndex = envs.indexOfFirst { it.name == envName }
    if (remIndex != -1) {
        envs.removeAt(remIndex)
    }

    updateEnvs(context, envs)
}

private fun updateEnvs(context: Context, envs: List<APSdkEnvironment>) {
    val prefs = AppSharedPreferences(context)
    val jsonStr = Gson().toJson(EnvsWrapper(envs))

    prefs.saveString(AppSharedPreferences.ADAPTIVE_ENVS, jsonStr)
}

fun getEnvs(context: Context): List<APSdkEnvironment> {
    val prefs = AppSharedPreferences(context)
    val envsWrapperStr = prefs.getString(AppSharedPreferences.ADAPTIVE_ENVS)

    val envs = try {
        Gson().fromJson(envsWrapperStr, EnvsWrapper::class.java).envs
    } catch (e: Exception) {
        e.printStackTrace()
        listOf()
    }.toMutableList()

    for (localEnvName in Environment.values().map { it.value }) {
        if (envs.firstOrNull { it.name == localEnvName } == null) {
            getLocalEnvByName(localEnvName)?.let { env ->
                envs.add(env)
            }
        }
    }

    return envs
}

fun getEnvByName(context: Context, envName: String) : APSdkEnvironment? {
    val envs = getEnvs(context)

    return envs.firstOrNull { it.name == envName }
}

private fun getLocalEnvByName(envName: String) : APSdkEnvironment? {
    return when (envName) {
        Environment.SDK_V2.value -> APSdkEnvironment(
            name = Environment.SDK_V2.value,
            appId = "plus.adaptive.qaapp",
            apiKey = "7f5kpYzMgApEncRK",
            apViews = sdkV2APViews
        )
        Environment.MOCK.value -> APSdkEnvironment(
            name = Environment.MOCK.value,
            appId = "plus.adaptive.qaapp",
            apiKey = "",
            apViews = listOf()
        )
        Environment.ALEXEY_DEV.value -> APSdkEnvironment(
            name = Environment.ALEXEY_DEV.value,
            appId = "plus.adaptive.qaapp",
            apiKey = "mljEZExo9cr98j4L",
            apViews = listOf()
        )
        else -> null
    }
}

private val sdkV2APViews = listOf(
    APSdkEnvironment.APView(id = "", hasDrafts = true),
    APSdkEnvironment.APView(id = "", hasDrafts = false)
)