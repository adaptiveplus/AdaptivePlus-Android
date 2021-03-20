package com.sprintsquads.adaptiveplus.utils

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializer
import com.google.gson.JsonObject
import com.google.gson.JsonSyntaxException
import com.sprintsquads.adaptiveplus.data.models.*
import com.sprintsquads.adaptiveplus.data.models.APEntryPoint
import com.sprintsquads.adaptiveplus.data.models.APLayer
import com.sprintsquads.adaptiveplus.data.models.APStory
import com.sprintsquads.adaptiveplus.data.models.APViewDataModel
import com.sprintsquads.adaptiveplus.data.models.components.*


internal fun getSerializedDataModel(dataModel: Any): String? {
    return Gson().toJson(dataModel)
}

internal fun getDeserializedAPViewDataModel(json: String): APViewDataModel? {
    return try {
        val gsonBuilder = GsonBuilder()
        gsonBuilder.registerTypeAdapter(APViewDataModel::class.java, apViewDataModelDeserializer)
        val apViewDataModelGson = gsonBuilder.create()
        apViewDataModelGson.fromJson(json, APViewDataModel::class.java)
    } catch (e: JsonSyntaxException) {
        e.printStackTrace()
        null
    }
}

private val apViewDataModelDeserializer =
    JsonDeserializer { json, _, _ ->
        try {
            val jsonObject: JsonObject = json.asJsonObject
            val id = jsonObject.get("id").asString
            val options = Gson().fromJson(
                jsonObject.get("options").toString(),
                APViewDataModel.Options::class.java)

            val campaigns = jsonObject.get("campaigns").asJsonArray
            val entryPoints = campaigns.map { campaignJson ->
                val campaignJsonObject = campaignJson.asJsonObject
                val campaignId = campaignJsonObject.get("id").asString
                val updatedAt = campaignJsonObject.get("updatedAt").asString
                val campaignBodyJsonObject = campaignJsonObject.get("body").asJsonObject
                val showOnce = campaignBodyJsonObject.get("showOnce").asBoolean
                val entryPointJsonObject = campaignBodyJsonObject.get("entryPoint").asJsonObject
                val entryPointId = entryPointJsonObject.get("id").asString
                val entryPointBodyJsonObject = entryPointJsonObject.get("body").asJsonObject
                entryPointBodyJsonObject.addProperty("id", entryPointId)
                entryPointBodyJsonObject.addProperty("updatedAt", updatedAt)
                entryPointBodyJsonObject.addProperty("campaignId", campaignId)
                entryPointBodyJsonObject.addProperty("showOnce", showOnce)

                val gsonBuilder = GsonBuilder()
                gsonBuilder.registerTypeAdapter(APLayer::class.java, apLayerDeserializer)
                val apEntryPointGson = gsonBuilder.create()
                apEntryPointGson.fromJson(
                    entryPointBodyJsonObject.toString(),
                    APEntryPoint::class.java)
            }.toList()

            APViewDataModel(
                id = id,
                options = options,
                entryPoints = entryPoints
            )
        } catch (e: JsonSyntaxException) {
            e.printStackTrace()
            null
        }
    }

private val apLayerDeserializer =
    JsonDeserializer { json, _, _ ->
        try {
            val jsonObject: JsonObject = json.asJsonObject
            val type = Gson().fromJson(
                jsonObject.get("type").asString,
                APLayer.Type::class.java)
            val options = Gson().fromJson(
                jsonObject.get("options").toString(),
                APLayer.Options::class.java)
            val componentString = jsonObject.get("component").toString()

            val component = when (type) {
                APLayer.Type.BACKGROUND ->
                    Gson().fromJson(componentString, APBackgroundComponent::class.java)
                APLayer.Type.IMAGE ->
                    Gson().fromJson(componentString, APImageComponent::class.java)
                APLayer.Type.TEXT ->
                    Gson().fromJson(componentString, APTextComponent::class.java)
                APLayer.Type.BUTTON ->
                    Gson().fromJson(componentString, APButtonComponent::class.java)
                else ->
                    null
            }

            APLayer(type, options, component)
        } catch (e: JsonSyntaxException) {
            e.printStackTrace()
            null
        }
    }

internal fun getDeserializedAPStory(json: String): APStory? {
    return try {
        val gsonBuilder = GsonBuilder()
        gsonBuilder.registerTypeAdapter(APStory::class.java, apStoryDeserializer)
        val apStoryGson = gsonBuilder.create()
        apStoryGson.fromJson(json, APStory::class.java)
    } catch (e: JsonSyntaxException) {
        e.printStackTrace()
        null
    }
}

private val apStoryDeserializer =
    JsonDeserializer { json, _, _ ->
        try {
            val jsonObject: JsonObject = json.asJsonObject
            val id = jsonObject.get("id").asString
            val storyBodyJsonObject = jsonObject.get("body").asJsonObject
            storyBodyJsonObject.addProperty("id", id)

            val gsonBuilder = GsonBuilder()
            gsonBuilder.registerTypeAdapter(APSnap::class.java, apSnapDeserializer)
            val apStoryGson = gsonBuilder.create()
            apStoryGson.fromJson(
                storyBodyJsonObject.toString(),
                APStory::class.java)
        } catch (e: JsonSyntaxException) {
            e.printStackTrace()
            null
        }
    }

private val apSnapDeserializer =
    JsonDeserializer { json, _, _ ->
        try {
            val jsonObject: JsonObject = json.asJsonObject
            val id = jsonObject.get("id").asString
            val snapBodyJsonObject = jsonObject.get("body").asJsonObject
            snapBodyJsonObject.addProperty("id", id)

            val gsonBuilder = GsonBuilder()
            gsonBuilder.registerTypeAdapter(APLayer::class.java, apLayerDeserializer)
            gsonBuilder.registerTypeAdapter(
                APSnap.ActionArea::class.java,
                apSnapActionAreaDeserializer)
            val apSnapGson = gsonBuilder.create()
            apSnapGson.fromJson(
                snapBodyJsonObject.toString(),
                APSnap::class.java)
        } catch (e: JsonSyntaxException) {
            e.printStackTrace()
            null
        }
    }

private val apSnapActionAreaDeserializer =
    JsonDeserializer { json, _, _ ->
        try {
            val jsonObject: JsonObject = json.asJsonObject
            val type = Gson().fromJson(
                jsonObject.get("type").asString, APSnap.ActionArea.Type::class.java)
            val bodyJson = jsonObject.get("body").toString()

            when (type) {
                APSnap.ActionArea.Type.BUTTON ->
                    Gson().fromJson(bodyJson, APSnap.ButtonActionArea::class.java)
                else -> null
            }
        } catch (e: JsonSyntaxException) {
            e.printStackTrace()
            null
        }
    }

internal fun deserializeAPActionParams(action: APAction) {
    action.parameters?.get("story")?.let { storyParam ->
        if (storyParam !is APStory) {
            getSerializedDataModel(storyParam)?.let { storyJson ->
                getDeserializedAPStory(storyJson)?.let { story ->
                    action.parameters["story"] = story
                }
            }
        }
    }
}