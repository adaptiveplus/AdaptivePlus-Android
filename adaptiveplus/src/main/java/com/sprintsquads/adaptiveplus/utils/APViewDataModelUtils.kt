package com.sprintsquads.adaptiveplus.utils

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializer
import com.google.gson.JsonObject
import com.google.gson.JsonSyntaxException
import com.sprintsquads.adaptiveplus.data.models.APLayer
import com.sprintsquads.adaptiveplus.data.models.APViewDataModel
import com.sprintsquads.adaptiveplus.data.models.components.*


internal fun getSerializedAPViewDataModel(dataModel: APViewDataModel): String? {
    return Gson().toJson(dataModel)
}

internal fun getDeserializedAPViewDataModel(json: String): APViewDataModel? {
    return try {
        val gsonBuilder = GsonBuilder()
        gsonBuilder.registerTypeAdapter(APLayer::class.java, layerDeserializer)
        val apViewDataModelGson = gsonBuilder.create()
        apViewDataModelGson.fromJson(json, APViewDataModel::class.java)
    } catch (e: JsonSyntaxException) {
        e.printStackTrace()
        null
    }
}

private val layerDeserializer =
    JsonDeserializer { json, _, _ ->
        try {
            val jsonObject: JsonObject = json.asJsonObject
            val kind = Gson().fromJson(
                jsonObject.get("kind").asString,
                APLayer.Kind::class.java)
            val options = Gson().fromJson(
                jsonObject.get("options").toString(),
                APLayer.Options::class.java)
            val componentString = jsonObject.get("component").toString()

            val component = when (kind) {
                APLayer.Kind.BACKGROUND ->
                    Gson().fromJson(componentString, APBackgroundComponent::class.java)
                APLayer.Kind.IMAGE ->
                    Gson().fromJson(componentString, APImageComponent::class.java)
                APLayer.Kind.TEXT ->
                    Gson().fromJson(componentString, APTextComponent::class.java)
                APLayer.Kind.BUTTON ->
                    Gson().fromJson(componentString, APButtonComponent::class.java)
                else ->
                    null
            }

            APLayer(kind, options, component)
        } catch (e: JsonSyntaxException) {
            e.printStackTrace()
            null
        }
    }

internal fun isAPViewDataModelNullOrEmpty(dataModel: APViewDataModel?): Boolean {
    return dataModel?.entryPoints.isNullOrEmpty()
}