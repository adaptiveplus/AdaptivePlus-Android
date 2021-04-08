package com.sprintsquads.adaptiveplus.utils

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializer
import com.google.gson.JsonObject
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import com.sprintsquads.adaptiveplus.data.models.*
import com.sprintsquads.adaptiveplus.data.models.actions.*
import com.sprintsquads.adaptiveplus.data.models.components.*


internal fun getAPViewGson(): Gson {
    val gsonBuilder = GsonBuilder()
    gsonBuilder.registerTypeAdapter(APViewDataModel::class.java, apViewDataModelDeserializer)
    return gsonBuilder.create()
}

internal fun getSerializedDataModel(dataModel: Any): String? {
    return Gson().toJson(dataModel)
}

internal fun getDeserializedAPViewDataModel(json: String): APViewDataModel? {
    return try {
        getAPViewGson().fromJson(json, APViewDataModel::class.java)
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
                gsonBuilder.registerTypeAdapter(APAction::class.java, apEntryPointActionDeserializer)
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

            val gsonBuilder = GsonBuilder()
            gsonBuilder.registerTypeAdapter(APAction::class.java, apActionDeserializer)
            val componentGson = gsonBuilder.create()

            val component = when (type) {
                APLayer.Type.BACKGROUND ->
                    componentGson.fromJson(componentString, APBackgroundComponent::class.java)
                APLayer.Type.IMAGE ->
                    componentGson.fromJson(componentString, APImageComponent::class.java)
                APLayer.Type.TEXT ->
                    componentGson.fromJson(componentString, APTextComponent::class.java)
                APLayer.Type.BUTTON ->
                    componentGson.fromJson(componentString, APButtonComponent::class.java)
                APLayer.Type.GIF ->
                    componentGson.fromJson(componentString, APGIFComponent::class.java)
                else ->
                    null
            }

            APLayer(type, options, component)
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

            val gsonBuilder = GsonBuilder()
            gsonBuilder.registerTypeAdapter(APAction::class.java, apActionDeserializer)
            val actionAreaGson = gsonBuilder.create()

            when (type) {
                APSnap.ActionArea.Type.BUTTON ->
                    actionAreaGson.fromJson(bodyJson, APSnap.ButtonActionArea::class.java)
                else -> null
            }
        } catch (e: JsonSyntaxException) {
            e.printStackTrace()
            null
        }
    }

private val apActionDeserializer =
    JsonDeserializer { json, _, _ ->
        try {
            val jsonObject: JsonObject = json.asJsonObject
            val type = Gson().fromJson(
                jsonObject.get("type").asString, APAction.Type::class.java)
            val paramsJsonObject = jsonObject.get("parameters").asJsonObject

            when (type) {
                APAction.Type.OPEN_WEB_LINK -> {
                    val url = paramsJsonObject.get("url").asString
                    APOpenWebLinkAction(url)
                }
                APAction.Type.CUSTOM -> {
                    val paramsType = object: TypeToken<HashMap<String, Any>?>(){}.type
                    val params = Gson().fromJson<HashMap<String, Any>>(
                        jsonObject.get("parameters").toString(), paramsType)
                    APCustomAction(params)
                }
                else -> null
            }
        } catch (e: JsonSyntaxException) {
            e.printStackTrace()
            null
        }
    }

private val apEntryPointActionDeserializer =
    JsonDeserializer { json, _, _ ->
        try {
            val jsonObject: JsonObject = json.asJsonObject
            val type = Gson().fromJson(
                jsonObject.get("type").asString, APAction.Type::class.java)
            val paramsJsonObject = jsonObject.get("parameters").asJsonObject

            when (type) {
                APAction.Type.SHOW_STORY -> {
                    val gsonBuilder = GsonBuilder()
                    gsonBuilder.registerTypeAdapter(APStory::class.java, apStoryDeserializer)
                    val storyGson = gsonBuilder.create()
                    val story = storyGson.fromJson(
                        paramsJsonObject.get("story").toString(), APStory::class.java)
                    APShowStoryAction(story)
                }
                APAction.Type.OPEN_WEB_LINK -> {
                    val url = paramsJsonObject.get("url").asString
                    APOpenWebLinkAction(url)
                }
                APAction.Type.CUSTOM -> {
                    val paramsType = object: TypeToken<HashMap<String, Any>?>(){}.type
                    val params = Gson().fromJson<HashMap<String, Any>>(
                        jsonObject.get("parameters").toString(), paramsType)
                    APCustomAction(params)
                }
                else -> null
            }
        } catch (e: JsonSyntaxException) {
            e.printStackTrace()
            null
        }
    }