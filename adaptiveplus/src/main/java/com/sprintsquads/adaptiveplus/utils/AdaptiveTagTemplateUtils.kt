package com.sprintsquads.adaptiveplus.utils

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializer
import com.google.gson.JsonObject
import com.google.gson.JsonSyntaxException
import com.sprintsquads.adaptiveplus.data.models.AdaptiveLayer
import com.sprintsquads.adaptiveplus.data.models.AdaptiveTagTemplate
import com.sprintsquads.adaptiveplus.data.models.components.*


internal fun getSerializedTagTemplate(template: AdaptiveTagTemplate): String? {
    return Gson().toJson(template)
}

internal fun getDeserializedTagTemplate(json: String): AdaptiveTagTemplate? {
    return try {
        val gsonBuilder = GsonBuilder()
        gsonBuilder.registerTypeAdapter(AdaptiveLayer::class.java, layerDeserializer)
        val tagTemplateGson = gsonBuilder.create()
        tagTemplateGson.fromJson(json, AdaptiveTagTemplate::class.java)
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
                AdaptiveLayer.Kind::class.java)
            val options = Gson().fromJson(
                jsonObject.get("options").toString(),
                AdaptiveLayer.Options::class.java)
            val componentString = jsonObject.get("component").toString()

            val component = when (kind) {
                AdaptiveLayer.Kind.BACKGROUND ->
                    Gson().fromJson(componentString, AdaptiveBackgroundComponent::class.java)
                AdaptiveLayer.Kind.IMAGE ->
                    Gson().fromJson(componentString, AdaptiveImageComponent::class.java)
                AdaptiveLayer.Kind.TEXT ->
                    Gson().fromJson(componentString, AdaptiveTextComponent::class.java)
                AdaptiveLayer.Kind.BUTTON ->
                    Gson().fromJson(componentString, AdaptiveButtonComponent::class.java)
                else ->
                    null
            }

            AdaptiveLayer(kind, options, component)
        } catch (e: JsonSyntaxException) {
            e.printStackTrace()
            null
        }
    }

internal fun isTagTemplateNullOrEmpty(template: AdaptiveTagTemplate?): Boolean {
    return template?.entries.isNullOrEmpty()
}