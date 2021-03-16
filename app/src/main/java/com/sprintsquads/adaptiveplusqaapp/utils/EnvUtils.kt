package com.sprintsquads.adaptiveplusqaapp.utils

import android.content.Context
import com.google.gson.Gson
import com.sprintsquads.adaptiveplusqaapp.data.AdaptiveCustomIP
import com.sprintsquads.adaptiveplusqaapp.data.APSdkEnvironment
import com.sprintsquads.adaptiveplusqaapp.data.Environment
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
        listOf<AdaptiveCustomIP>()
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
    loadingType: APSdkEnvironment.APView.LoadingType,
    isInstructions: Boolean,
    isOnboarding: Boolean,
    hasBookmarks: Boolean
) {
    val envs = getEnvs(context).toMutableList()
    envs.firstOrNull { it.name == envName }?.run {
        apViews = apViews.toMutableList().apply {
            add(
                APSdkEnvironment.APView(
                    id = apViewId,
                    loadingType = loadingType,
                    isInstructions = isInstructions,
                    isOnboarding = isOnboarding,
                    hasBookmarks = hasBookmarks
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
        listOf<APSdkEnvironment>()
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
        Environment.STAGING.value -> APSdkEnvironment(
            name = Environment.STAGING.value,
            appId = "com.s10s.adaptiveplussampleapp",
            companySecret = "Tbh1qthkdODSBU5kSkTiRNH4rU6taNtliHz/z1qYIQG5fkISQtxyShr5rj2UaqR4GHOo5m+xJ8j3iMqJI9Pw4Ao4wheJNzw0q0tpwz/JEVuhz023e1IYTvfM/UuYilzcB/hctmWIQbprmmgu8O7eOXuoDqk7I6vReieJ6YH/Rnh5913sXoACyVzvehk8gm8BZFeL9YKc2ulReaEf9L0fUTKjlxqnlU0kEz14M6JiiE6EH5xTwIJJiR6I6FDVXd7uGuCe8j3FYpSga3cpY74trHXEME0c6boeJM2uw7WGiadDfz3hPPf1SH4TRPJk9uXfj1BfwE8I5E2KT97XD90G9w==",
            appSecret = "LdtUp8n+WBc3VXOdCpAfFPskhSYEqGf32YaXY94C9u4D265avLmhyO5qlkG95G56bmYDxfua6Xc+TaJflCDOB63BNv6BG5ISCtwsQws1M5YY0Swiw533jhZh2lQ4c9wMw6OtopIAgOFOYx6Ymv40opvR1euKuUOVA2daitmbgKwwgSbZpBhWnq9gKYLjGYet104B5bSeZ4/SSGsweXZY0E+bneylrdCstnUkdixIbE9bkJRJs+2/gHj2zxQ9t8QqG26LyXVpmNml1RGcivK8XbIsImGLpFm1v9743acnu2UCm9P7P+GBw0kvVutZmpHa+EFPtJShykWkwvVfI35B3Q==",
            baseApiUrl = "https://api-staging.adaptive.plus",
            apViews = stagingAPViews
        )
        Environment.HOMEBANK_DEV.value -> APSdkEnvironment(
            name = Environment.HOMEBANK_DEV.value,
            appId = "kz.kkb.homebank.dev",
            companySecret = "Tbh1qthkdODSBU5kSkTiRNH4rU6taNtliHz/z1qYIQG5fkISQtxyShr5rj2UaqR4GHOo5m+xJ8j3iMqJI9Pw4Ao4wheJNzw0q0tpwz/JEVuhz023e1IYTvfM/UuYilzcB/hctmWIQbprmmgu8O7eOXuoDqk7I6vReieJ6YH/Rnh5913sXoACyVzvehk8gm8BZFeL9YKc2ulReaEf9L0fUTKjlxqnlU0kEz14M6JiiE6EH5xTwIJJiR6I6FDVXd7uGuCe8j3FYpSga3cpY74trHXEME0c6boeJM2uw7WGiadDfz3hPPf1SH4TRPJk9uXfj1BfwE8I5E2KT97XD90G9w==",
            appSecret = "qOt2DpO2gS4YVZ8gNgS1BhI1+R0jMrSBo+jLfLBcQT238FqSfTKcD+XxeTnBHFqQZLrkO/wHMkKEEKOh1G1xw+2vwNsEezChjnN7nMbiowK9zUwKKW95jhPFpcpKUf30W5TDvPtVZAm5I9st0s4kXrDxP53uPvpqg2va1fFPu8LoGkXTSLn//KBrKYdqiyxmd7nEOaT28+IUC8aP+GI0jqtqqBSbu/ycfNBoP7cWGZyv6oTIxFNazN8BU4JmcvSdeCm+qiDedFQapvsHYDxDR2bc/zGACQJtrllX9rp0z47Z8njW8BUDtLd5I+bfm/MRXQCM3mcC5TuSuEhMqFpvxQ==",
            baseApiUrl = "https://test-adaptive-api.homebank.kz",
            apViews = homebankAPViews
        )
        Environment.HOMEBANK_PROD.value -> APSdkEnvironment(
            name = Environment.HOMEBANK_PROD.value,
            appId = "kz.kkb.homebank",
            companySecret = "Tbh1qthkdODSBU5kSkTiRNH4rU6taNtliHz/z1qYIQG5fkISQtxyShr5rj2UaqR4GHOo5m+xJ8j3iMqJI9Pw4Ao4wheJNzw0q0tpwz/JEVuhz023e1IYTvfM/UuYilzcB/hctmWIQbprmmgu8O7eOXuoDqk7I6vReieJ6YH/Rnh5913sXoACyVzvehk8gm8BZFeL9YKc2ulReaEf9L0fUTKjlxqnlU0kEz14M6JiiE6EH5xTwIJJiR6I6FDVXd7uGuCe8j3FYpSga3cpY74trHXEME0c6boeJM2uw7WGiadDfz3hPPf1SH4TRPJk9uXfj1BfwE8I5E2KT97XD90G9w==",
            appSecret = "6+e6T6gluLgjM30X1EMbLXBkrfVDECV5NA2UwIygChbuqi4tFLwgdghqin9ISbxvm9EA7mn58JQTn6iJaPukXdZj96ZW53kQxed9Yv8R3KA+W7SY630lTQ8xTQ2ArU4b1bac/m+V9nqmxHS6tX4XiXDPYPzxfLrH9mNDsQMzzKxB0igs31L1DwfcrgJMFowTxQBkHpMXoR32nSVK99Ovxa+vzgXSF2E2lrk5eNicJhL6uW1BP7nu2Ju+4PDkX/f34ZMR50LFD5UfrEeEAjnhOhy0DvS0BdNK7PUPc5gB9fnXOmDiA+P7YTt5hRLaVO+Bgup5HrXfC3SyO+3HOxy5Ug==",
            baseApiUrl = "https://adaptive-api.homebank.kz",
            apViews = homebankAPViews
        )
        Environment.MYCAR.value -> APSdkEnvironment(
            name = Environment.MYCAR.value,
            appId = "kz.smartmk.mycars.android",
            companySecret = "N48RyrE1lSwKj0IIddb3IFfhmy+Vywrh2v3K8LWWG0jBrxAHVP76k2Lgj/IwXEU59APriXZH8KtPDFf4ZXJyf0F1kV7/nskUXxZeY2PWcSHBfchtxZiBT+GYZgQoK3jfz7preff7Q2wyBxuuWU+KWyLpTo5vAxx/40C6wL3vVcBieQ5TkrHvd4eGPkqSL52bC8UgIR2IuLcT89CC90I5avF4pHDSbFIWcpUIcV7l0HIfP+uU5Y7FMgrpm/3scMhUoHYMy5Ta0mUaZfFZbZ2ozjycpJiwq/bELpFZSl+WuD1Lf/DsGNjofbvsmAYGeDk9ODyD+Y/kQKgO6uwWHnRAlQ==",
            appSecret = "4F8vwnCXg+sSF/B8+YjG8eCeNzxYDmYhFSON6kwNl+TVxXxAY8RKUy2ZRem9PqOUWj9OwM+VTn/mlCaDJPNbDSuv1qLJAMMwj8y25kmDd0XwJapevJUfQejzHerLMEREc0gpcm7cRv3x324mqfG4GIjueBbkYZQYy11C7WTpzZ7rWm2kU0601+KICpgmeb8alZwC4+54psJDBSHm1kz2pZjvwOBtdvqp/6qe2USlliou0zUPQ+zUw0X06mw22nnWlWwUDlWDxa8+PkDYtzNynhJwlB75KzSMGQD8wwCgUdjhWmtczdo1gmCEFqExvxkiNwHnQ3IooDiBsubEqXkGkA==",
            baseApiUrl = "https://adaptive-api.mycar.kz",
            apViews = mycarAPViews
        )
        Environment.MYCAR_PREPROD.value -> APSdkEnvironment(
            name = Environment.MYCAR_PREPROD.value,
            appId = "ru.agima.mycars.android",
            companySecret = "N48RyrE1lSwKj0IIddb3IFfhmy+Vywrh2v3K8LWWG0jBrxAHVP76k2Lgj/IwXEU59APriXZH8KtPDFf4ZXJyf0F1kV7/nskUXxZeY2PWcSHBfchtxZiBT+GYZgQoK3jfz7preff7Q2wyBxuuWU+KWyLpTo5vAxx/40C6wL3vVcBieQ5TkrHvd4eGPkqSL52bC8UgIR2IuLcT89CC90I5avF4pHDSbFIWcpUIcV7l0HIfP+uU5Y7FMgrpm/3scMhUoHYMy5Ta0mUaZfFZbZ2ozjycpJiwq/bELpFZSl+WuD1Lf/DsGNjofbvsmAYGeDk9ODyD+Y/kQKgO6uwWHnRAlQ==",
            appSecret = "FSCcUdSR9skHCo4dX7Jegn2HgJwAwckvbuui8Igwno+4P5FSjH7nkLVKDhOl+3LXaMrSmbvMKe6fp6YpDcbhDMgSJCS4Gik06aynaZLkV5z5wz1VBdmuYehHc3YHy5ueGmM8mk5P7QGuD+EVC2XbO2u2fWJMgI0Uw4Wm4hbRtMlPxaCNclFlOhqxbc3csjLHl2aTjDWT91ML0zppxaP4IaEyUIhYMsq0aFy3w3UDB4vztGhULvAFLzm6daHcoUsc5U44dvs/B9S7kHSGYcZBmnObiJmDdl6/XNwDFn25356J1kOc4YNmQTHHS5HJOlRIWcvcwBgluBNjDyQspN85iA==",
            baseApiUrl = "http://178.154.231.192:3030",
            apViews = mycarPreprodAPViews
        )
        Environment.MOCK.value -> APSdkEnvironment(
            name = Environment.MOCK.value,
            appId = "",
            companySecret = "",
            appSecret = "",
            baseApiUrl = "",
            apViews = listOf()
        )
        else -> null
    }
}

private val stagingAPViews = listOf(
    APSdkEnvironment.APView(id = "5f85ffadb069f4001221c9f9", loadingType = APSdkEnvironment.APView.LoadingType.EMPTY),
    APSdkEnvironment.APView(id = "5f859fac6aa1a80012ee31fd", loadingType = APSdkEnvironment.APView.LoadingType.EMPTY),
    APSdkEnvironment.APView(id = "5f859fb96aa1a80012ee31fe", loadingType = APSdkEnvironment.APView.LoadingType.EMPTY),
    APSdkEnvironment.APView(id = "5f5f79d22e6c6500125c2e56", loadingType = APSdkEnvironment.APView.LoadingType.EMPTY),
    APSdkEnvironment.APView(id = "5f859f846aa1a80012ee31fb", loadingType = APSdkEnvironment.APView.LoadingType.EMPTY),
    APSdkEnvironment.APView(id = "5f859f946aa1a80012ee31fc", loadingType = APSdkEnvironment.APView.LoadingType.EMPTY),
    APSdkEnvironment.APView(id = "5f5f7a2b2e6c6500125c2e58", loadingType = APSdkEnvironment.APView.LoadingType.EMPTY),
    APSdkEnvironment.APView(id = "5f859f1f6aa1a80012ee31a9", loadingType = APSdkEnvironment.APView.LoadingType.EMPTY),
    APSdkEnvironment.APView(id = "5f859f386aa1a80012ee31aa", loadingType = APSdkEnvironment.APView.LoadingType.EMPTY),
    APSdkEnvironment.APView(id = "5f5f7a6d2e6c6500125c2e5a", loadingType = APSdkEnvironment.APView.LoadingType.EMPTY),
    APSdkEnvironment.APView(id = "5f5f7a9e2e6c6500125c2e5c", loadingType = APSdkEnvironment.APView.LoadingType.EMPTY),
    APSdkEnvironment.APView(id = "5f5f7a042e6c6500125c2e57", loadingType = APSdkEnvironment.APView.LoadingType.EMPTY),
    APSdkEnvironment.APView(id = "5f5f7a562e6c6500125c2e59", loadingType = APSdkEnvironment.APView.LoadingType.EMPTY),
    APSdkEnvironment.APView(id = "5f5f7a892e6c6500125c2e5b", loadingType = APSdkEnvironment.APView.LoadingType.EMPTY),
    APSdkEnvironment.APView(id = "5f894b3c44795c0012511f35", loadingType = APSdkEnvironment.APView.LoadingType.EMPTY),
    APSdkEnvironment.APView(id = "5fc61e6fa2577e0018a8b4de", loadingType = APSdkEnvironment.APView.LoadingType.EMPTY)
)

private val homebankAPViews = listOf(
    APSdkEnvironment.APView(id = "5f645dcb1fcfdf0018828dde", loadingType = APSdkEnvironment.APView.LoadingType.EMPTY),
    APSdkEnvironment.APView(id = "5f645d741fcfdf0018828ddd", loadingType = APSdkEnvironment.APView.LoadingType.EMPTY),
    APSdkEnvironment.APView(id = "5f645da672b21a0018d808b0", loadingType = APSdkEnvironment.APView.LoadingType.EMPTY),
    APSdkEnvironment.APView(id = "5f645df372b21a0018d808b1", loadingType = APSdkEnvironment.APView.LoadingType.EMPTY),
    APSdkEnvironment.APView(id = "5f645e0c1fcfdf0018828ddf", loadingType = APSdkEnvironment.APView.LoadingType.EMPTY),
    APSdkEnvironment.APView(id = "5f645e2e72b21a0018d808b2", loadingType = APSdkEnvironment.APView.LoadingType.EMPTY),
    APSdkEnvironment.APView(id = "5f645e471fcfdf0018828de0", loadingType = APSdkEnvironment.APView.LoadingType.EMPTY)
)

private val mycarAPViews = listOf(
    APSdkEnvironment.APView(id = "5f871516851f910012576f44", loadingType = APSdkEnvironment.APView.LoadingType.EMPTY),
    APSdkEnvironment.APView(id = "5f871535851f910012576f45", loadingType = APSdkEnvironment.APView.LoadingType.EMPTY),
    APSdkEnvironment.APView(id = "5f871570851f910012576f46", loadingType = APSdkEnvironment.APView.LoadingType.EMPTY),
    APSdkEnvironment.APView(id = "5f8715b4851f910012576f48", loadingType = APSdkEnvironment.APView.LoadingType.EMPTY),
    APSdkEnvironment.APView(id = "5f87158a851f910012576f47", loadingType = APSdkEnvironment.APView.LoadingType.EMPTY)
)

private val mycarPreprodAPViews = listOf(
    APSdkEnvironment.APView(id = "5f871570851f910012576f46", loadingType = APSdkEnvironment.APView.LoadingType.EMPTY),
    APSdkEnvironment.APView(id = "5f871516851f910012576f44", loadingType = APSdkEnvironment.APView.LoadingType.EMPTY),
    APSdkEnvironment.APView(id = "5fe3d8d6a49a340018f9932f", loadingType = APSdkEnvironment.APView.LoadingType.EMPTY, isInstructions = true)
)