package plus.adaptive.qaapp.data

import java.io.Serializable


data class APSdkEnvironment(
    val name: String,
    val appId: String,
    val apiKey: String,
    var apViews: List<APView>
) : Serializable {

    data class APView(
        val id: String,
        val hasDrafts: Boolean?,
        val isInstruction: Boolean?,
    ) : Serializable
}