package plus.adaptive.sdk.utils

import com.google.gson.*
import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken
import plus.adaptive.sdk.core.analytics.APCrashlytics
import plus.adaptive.sdk.data.BASE_SIZE_MULTIPLIER
import plus.adaptive.sdk.data.BASE_SIZE_MULTIPLIER_NEW
import plus.adaptive.sdk.data.models.*
import plus.adaptive.sdk.data.models.actions.*
import plus.adaptive.sdk.data.models.components.*
import plus.adaptive.sdk.data.models.story.*
import plus.adaptive.sdk.data.models.story.APOuterStyles
import plus.adaptive.sdk.data.models.story.APTemplateDataModel
import plus.adaptive.sdk.data.models.story.Campaign
import plus.adaptive.sdk.data.models.story.Layer
import plus.adaptive.sdk.data.models.story.Story


private fun getProcessedAPCarouselViewGson(): Gson {
    val gsonBuilder = GsonBuilder()
    gsonBuilder.registerTypeAdapter(APAction::class.java, apEntryPointActionSerializer)
    gsonBuilder.registerTypeAdapter(APLayer::class.java, apLayerDeserializer)
    gsonBuilder.registerTypeAdapter(APAction::class.java, apEntryPointActionDeserializer)
    return gsonBuilder.create()
}

private fun getProcessedAPSplashScreenViewGson(): Gson {
    val gsonBuilder = GsonBuilder()
    gsonBuilder.registerTypeAdapter(APAction::class.java, apActionSerializer)
    gsonBuilder.registerTypeAdapter(APLayer::class.java, apLayerDeserializer)
    gsonBuilder.registerTypeAdapter(APAction::class.java, apActionDeserializer)
    return gsonBuilder.create()
}

internal fun getSerializedProcessedAPCarouselViewDataModel(
    dataModel: APCarouselViewDataModel
): String? {
    return getProcessedAPCarouselViewGson().toJson(dataModel)
}

internal fun getSerializedProcessedAPSplashScreenViewDataModel(
    dataModel: APSplashScreenViewDataModel
): String? {
    return getProcessedAPSplashScreenViewGson().toJson(dataModel)
}

internal fun getDeserializedProcessedAPCarouselViewDataModel(
    json: String
): APCarouselViewDataModel? {
    return try {
        val dataModel = getProcessedAPCarouselViewGson()
            .fromJson(json, APCarouselViewDataModel::class.java)
        checkAPCarouselViewDataModelProperties(dataModel)
    return dataModel
    } catch (e: Exception) {
        APCrashlytics.logCrash(e)
        e.printStackTrace()
        null
    }
}

internal fun getDeserializedProcessedAPSplashScreenViewDataModel(
    json: String
): APSplashScreenViewDataModel? {
    return try {
        val dataModel = getProcessedAPSplashScreenViewGson()
            .fromJson(json, APSplashScreenViewDataModel::class.java)
        checkAPSplashScreenViewDataModelProperties(dataModel)
        dataModel
    } catch (e: Exception) {
        APCrashlytics.logCrash(e)
        e.printStackTrace()
        null
    }
}

internal fun getUnprocessedAPCarouselViewGson(): Gson {
    val gsonBuilder = GsonBuilder()
    gsonBuilder.registerTypeAdapter(
        APCarouselViewDataModel::class.java,
        apCarouselViewDataModelDeserializer)
    return gsonBuilder.create()
}

internal fun getUnprocessedAPSplashScreenViewGson(): Gson {
    val gsonBuilder = GsonBuilder()
    gsonBuilder.registerTypeAdapter(
        APSplashScreenViewDataModel::class.java,
        apSplashScreenViewDataModelDeserializer)
    return gsonBuilder.create()
}

internal fun getDeserializedUnprocessedAPCarouselViewDataModel(
    json: String
): APCarouselViewDataModel? {
    return try {
        val dataModel = getUnprocessedAPCarouselViewGson()
            .fromJson(json, APCarouselViewDataModel::class.java)
        checkAPCarouselViewDataModelProperties(dataModel)
        magnifyAPCarouselViewDataModel(dataModel)
    } catch (e: Exception) {
        APCrashlytics.logCrash(e)
        e.printStackTrace()
        null
    }
}

internal fun getDeserializedUnprocessedAPSplashScreenViewDataModel(
    json: String
): APSplashScreenViewDataModel? {
    return try {
        val dataModel = getUnprocessedAPSplashScreenViewGson()
            .fromJson(json, APSplashScreenViewDataModel::class.java)
        checkAPSplashScreenViewDataModelProperties(dataModel)
        magnifyAPSplashScreenViewDataModel(dataModel)
    } catch (e: Exception) {
        APCrashlytics.logCrash(e)
        e.printStackTrace()
        null
    }
}

private val apCarouselViewDataModelDeserializer =
    JsonDeserializer { json, _, _ ->
        try {
            val jsonObject: JsonObject = json.asJsonObject
            val id = jsonObject.get("id").asString
            val options = Gson().fromJson(
                jsonObject.get("options").toString(),
                APCarouselViewDataModel.Options::class.java)

            val campaigns = jsonObject.get("campaigns").asJsonArray
            val entryPoints = campaigns.map { campaignJson ->
                try {
                    val campaignJsonObject = campaignJson.asJsonObject
                    val campaignId = campaignJsonObject.get("id").asString
                    val updatedAt = campaignJsonObject.get("updatedAt").asString
                    val status = campaignJsonObject.get("status")?.asString
                    val campaignBodyJsonObject = campaignJsonObject.get("body").asJsonObject
                    val showOnce = campaignBodyJsonObject.get("showOnce")?.asBoolean ?: false
                    val showCount = campaignJsonObject.get("showCount")?.asInt
                    if(campaignBodyJsonObject.has("banner")){
                        val bannerJsonObject = campaignBodyJsonObject.get("banner").asJsonObject
                        val bannerId = bannerJsonObject.get("id").asString
                        val bannerBodyJsonObject = bannerJsonObject.get("body").asJsonObject
                        bannerBodyJsonObject.addProperty("id", bannerId)
                        bannerBodyJsonObject.addProperty("updatedAt", updatedAt)
                        bannerBodyJsonObject.addProperty("campaignId", campaignId)
                        bannerBodyJsonObject.addProperty("status", status)
                        bannerBodyJsonObject.addProperty("showOnce", showOnce)
                        bannerBodyJsonObject.addProperty("showCount", showCount)
                        val gsonBuilder = GsonBuilder()
                        gsonBuilder.registerTypeAdapter(APLayer::class.java, apLayerDeserializer)
                        gsonBuilder.registerTypeAdapter(APAction::class.java, apEntryPointActionDeserializer)
                        val apEntryPointGson = gsonBuilder.create()
                        val apEntryPoint = apEntryPointGson.fromJson(
                            bannerBodyJsonObject.toString(),
                            APEntryPoint::class.java)
                        checkAPEntryPointProperties(apEntryPoint)
                        apEntryPoint
                    } else {
                        null
                    }
                } catch (e: Exception) {
                    APCrashlytics.logCrash(e)
                    e.printStackTrace()
                    null
                }
            }.toList()

            APCarouselViewDataModel(
                id = id,
                options = options,
                entryPoints = entryPoints.filterNotNull()
            )
        } catch (e: JsonSyntaxException) {
            APCrashlytics.logCrash(e)
            e.printStackTrace()
            null
        }
    }

private val apSplashScreenViewDataModelDeserializer =
    JsonDeserializer { json, _, _ ->
        try {
            val jsonObject: JsonObject = json.asJsonObject
            val id = jsonObject.get("id").asString
            val options = Gson().fromJson(
                jsonObject.get("options").toString(),
                APSplashScreenViewDataModel.Options::class.java)

            val campaigns = jsonObject.get("campaigns").asJsonArray
            val splashScreens = campaigns.map { campaignJson ->
                try {
                    val campaignJsonObject = campaignJson.asJsonObject
                    val campaignId = campaignJsonObject.get("id").asString
                    val status = campaignJsonObject.get("status").asString
                    val showCount = campaignJsonObject.get("showCount")?.asInt
                    val campaignBodyJsonObject = campaignJsonObject.get("body").asJsonObject
                    val splashScreenJsonObject = campaignBodyJsonObject.get("splashScreen").asJsonObject
                    val splashScreenId = splashScreenJsonObject.get("id").asString
                    val splashScreenBodyJsonObject = splashScreenJsonObject.get("body").asJsonObject
                    splashScreenBodyJsonObject.addProperty("id", splashScreenId)
                    splashScreenBodyJsonObject.addProperty("campaignId", campaignId)
                    splashScreenBodyJsonObject.addProperty("status", status)
                    splashScreenBodyJsonObject.addProperty("showCount", showCount)

                    val gsonBuilder = GsonBuilder()
                    gsonBuilder.registerTypeAdapter(APLayer::class.java, apLayerDeserializer)
                    gsonBuilder.registerTypeAdapter(APAction::class.java, apActionDeserializer)
                    val apSplashScreenGson = gsonBuilder.create()
                    val apSplashScreen = apSplashScreenGson.fromJson(
                        splashScreenBodyJsonObject.toString(),
                        APSplashScreen::class.java)

                    checkAPSplashScreenProperties(apSplashScreen)

                    apSplashScreen
                } catch (e: Exception) {
                    APCrashlytics.logCrash(e)
                    e.printStackTrace()
                    null
                }
            }.toList()

            APSplashScreenViewDataModel(
                id = id,
                options = options,
                splashScreens = splashScreens.filterNotNull()
            )
        } catch (e: JsonSyntaxException) {
            APCrashlytics.logCrash(e)
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
                APLayer.Type.POLL ->
                    componentGson.fromJson(componentString, APPollComponent::class.java)
                else ->
                    null
            }

            APLayer(type, options, component)
        } catch (e: JsonSyntaxException) {
            APCrashlytics.logCrash(e)
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
            APCrashlytics.logCrash(e)
            e.printStackTrace()
            null
        }
    }

private val apStorySerializer =
    JsonSerializer<APStory> { src, _, _ ->
        val jsonObject = JsonObject()

        jsonObject.addProperty("id", src.id)

        val gsonBuilder = GsonBuilder()
        gsonBuilder.registerTypeAdapter(APSnap::class.java, apSnapSerializer)
        val apStoryGson = gsonBuilder.create()

        jsonObject.add("body", apStoryGson.toJsonTree(src))

        jsonObject
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
            APCrashlytics.logCrash(e)
            e.printStackTrace()
            null
        }
    }

private val apSnapSerializer =
    JsonSerializer<APSnap> { src, _, _ ->
        val jsonObject = JsonObject()

        jsonObject.addProperty("id", src.id)

        val gsonBuilder = GsonBuilder()
        gsonBuilder.registerTypeAdapter(APAction::class.java, apActionSerializer)
        gsonBuilder.registerTypeAdapter(APSnap.ActionArea::class.java, apSnapActionAreaSerializer)
        val apSnapGson = gsonBuilder.create()

        jsonObject.add("body", apSnapGson.toJsonTree(src))

        jsonObject
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
            APCrashlytics.logCrash(e)
            e.printStackTrace()
            null
        }
    }

private val apSnapActionAreaSerializer =
    JsonSerializer<APSnap.ActionArea> { src, _, _ ->
        val jsonObject = JsonObject()

        when (src) {
            is APSnap.ButtonActionArea -> {
                val clazz = APSnap.ActionArea.Type.BUTTON.javaClass
                val name = APSnap.ActionArea.Type.BUTTON.name
                val annotation = clazz.getField(name).getAnnotation(SerializedName::class.java)
                jsonObject.addProperty("type", annotation.value)

                val gsonBuilder = GsonBuilder()
                gsonBuilder.registerTypeAdapter(APAction::class.java, apActionSerializer)
                val apSnapActionAreaGson = gsonBuilder.create()

                jsonObject.add("body", apSnapActionAreaGson.toJsonTree(src))
            }
            else -> {}
        }

        jsonObject
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
                    val isWebView = paramsJsonObject.get("isWebView")?.asBoolean
                    APOpenWebLinkAction(url, isWebView)
                }
                APAction.Type.CUSTOM -> {
                    val paramsType = object: TypeToken<HashMap<String, Any>?>(){}.type
                    val params = Gson().fromJson<HashMap<String, Any>>(
                        jsonObject.get("parameters").toString(), paramsType)
                    APCustomAction(params)
                }
                APAction.Type.SEND_SMS -> {
                    val phoneNumber = paramsJsonObject.get("phoneNumber").asString
                    val message = paramsJsonObject.get("message").asString
                    APSendSMSAction(phoneNumber = phoneNumber, message = message)
                }
                APAction.Type.CALL -> {
                    val phoneNumber = paramsJsonObject.get("phoneNumber").asString
                    APCallPhoneAction(phoneNumber)
                }
                else -> null
            }
        } catch (e: JsonSyntaxException) {
            APCrashlytics.logCrash(e)
            e.printStackTrace()
            null
        }
    }

private val apActionSerializer =
    JsonSerializer<APAction> { src, _, _ ->
        val jsonObject = JsonObject()

        when (src) {
            is APOpenWebLinkAction -> {
                val clazz = APAction.Type.OPEN_WEB_LINK.javaClass
                val name = APAction.Type.OPEN_WEB_LINK.name
                val annotation = clazz.getField(name).getAnnotation(SerializedName::class.java)
                jsonObject.addProperty("type", annotation.value)
                jsonObject.add("parameters", Gson().toJsonTree(src))
            }
            is APCustomAction -> {
                val clazz = APAction.Type.CUSTOM.javaClass
                val name = APAction.Type.CUSTOM.name
                val annotation = clazz.getField(name).getAnnotation(SerializedName::class.java)
                jsonObject.addProperty("type", annotation.value)
                jsonObject.add("parameters", Gson().toJsonTree(src.parameters))
            }
            is APSendSMSAction -> {
                val clazz = APAction.Type.SEND_SMS.javaClass
                val name = APAction.Type.SEND_SMS.name
                val annotation = clazz.getField(name).getAnnotation(SerializedName::class.java)
                jsonObject.addProperty("type", annotation.value)
                jsonObject.add("parameters", Gson().toJsonTree(src))
            }
            is APCallPhoneAction -> {
                val clazz = APAction.Type.CALL.javaClass
                val name = APAction.Type.CALL.name
                val annotation = clazz.getField(name).getAnnotation(SerializedName::class.java)
                jsonObject.addProperty("type", annotation.value)
                jsonObject.add("parameters", Gson().toJsonTree(src))
            }
            else -> {}
        }

        jsonObject
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
                    val isWebView = paramsJsonObject.get("isWebView")?.asBoolean
                    APOpenWebLinkAction(url, isWebView)
                }
                APAction.Type.CUSTOM -> {
                    val paramsType = object: TypeToken<HashMap<String, Any>?>(){}.type
                    val params = Gson().fromJson<HashMap<String, Any>>(
                        jsonObject.get("parameters").toString(), paramsType)
                    APCustomAction(params)
                }
                APAction.Type.SEND_SMS -> {
                    val phoneNumber = paramsJsonObject.get("phoneNumber").asString
                    val message = paramsJsonObject.get("message").asString
                    APSendSMSAction(phoneNumber = phoneNumber, message = message)
                }
                APAction.Type.CALL -> {
                    val phoneNumber = paramsJsonObject.get("phoneNumber").asString
                    APCallPhoneAction(phoneNumber)
                }
                else -> null
            }
        } catch (e: JsonSyntaxException) {
            APCrashlytics.logCrash(e)
            e.printStackTrace()
            null
        }
    }

private val apEntryPointActionSerializer =
    JsonSerializer<APAction> { src, _, _ ->
        val jsonObject = JsonObject()

        when (src) {
            is APShowStoryAction -> {
                val clazz = APAction.Type.SHOW_STORY.javaClass
                val name = APAction.Type.SHOW_STORY.name
                val annotation = clazz.getField(name).getAnnotation(SerializedName::class.java)
                jsonObject.addProperty("type", annotation.value)

                val gsonBuilder = GsonBuilder()
                gsonBuilder.registerTypeAdapter(APStory::class.java, apStorySerializer)
                val apEntryPointActionGson = gsonBuilder.create()

                jsonObject.add("parameters", apEntryPointActionGson.toJsonTree(src))
            }
            is APOpenWebLinkAction -> {
                val clazz = APAction.Type.OPEN_WEB_LINK.javaClass
                val name = APAction.Type.OPEN_WEB_LINK.name
                val annotation = clazz.getField(name).getAnnotation(SerializedName::class.java)
                jsonObject.addProperty("type", annotation.value)
                jsonObject.add("parameters", Gson().toJsonTree(src))
            }
            is APCustomAction -> {
                val clazz = APAction.Type.CUSTOM.javaClass
                val name = APAction.Type.CUSTOM.name
                val annotation = clazz.getField(name).getAnnotation(SerializedName::class.java)
                jsonObject.addProperty("type", annotation.value)
                jsonObject.add("parameters", Gson().toJsonTree(src.parameters))
            }
            is APSendSMSAction -> {
                val clazz = APAction.Type.SEND_SMS.javaClass
                val name = APAction.Type.SEND_SMS.name
                val annotation = clazz.getField(name).getAnnotation(SerializedName::class.java)
                jsonObject.addProperty("type", annotation.value)
                jsonObject.add("parameters", Gson().toJsonTree(src))
            }
            is APCallPhoneAction -> {
                val clazz = APAction.Type.CALL.javaClass
                val name = APAction.Type.CALL.name
                val annotation = clazz.getField(name).getAnnotation(SerializedName::class.java)
                jsonObject.addProperty("type", annotation.value)
                jsonObject.add("parameters", Gson().toJsonTree(src))
            }
            else -> {}
        }

        jsonObject
    }

internal fun checkAPCarouselViewDataModelProperties(dataModel: APCarouselViewDataModel) {
    dataModel.run {
        id
        options.run {
            width
            height
            cornerRadius
            magnetize
            autoScroll
            checkAPPaddingProperties(padding)
            spacing
            screenWidth
            showBorder
        }
        entryPoints
    }
}

internal fun checkAPSplashScreenViewDataModelProperties(dataModel: APSplashScreenViewDataModel) {
    dataModel.run {
        id
        options.run {
            width
            height
            screenWidth
        }
        splashScreens
    }
}

private fun checkAPPaddingProperties(padding: APPadding) {
    padding.run {
        top
        bottom
        left
        right
    }
}

private fun checkAPEntryPointProperties(entryPoint: APEntryPoint) {
    entryPoint.run {
        id
        updatedAt
        campaignId
        status
        layers
        showCount
        actions
    }
}

private fun checkAPSplashScreenProperties(
    splashScreen: APSplashScreen
) {
    splashScreen.run {
        id
        campaignId
        status
        showCount
        showTime
        layers.forEach { checkAPLayerProperties(it) }
        actions?.forEach { checkAPActionProperties(it) }
    }
}

private fun checkAPLayerProperties(apLayer: APLayer) {
    apLayer.run {
        type
        options.run {
            position.run {
                x
                y
                width
                height
                angle
            }
            opacity
        }
        component?.let { checkAPComponentProperties(component) }
    }
}

private fun checkAPComponentProperties(apComponent: APComponent) {
    apComponent.run {
        when (this) {
            is APBackgroundComponent -> {
                color
            }
            is APTextComponent -> {
                value
                font?.let { checkAPFontProperties(it) }
            }
            is APImageComponent -> {
                url
                border?.run {
                    active.run {
                        width
                        checkAPGradientColorProperties(color)
                        padding
                        cornerRadius
                    }
                    inactive.run {
                        width
                        checkAPGradientColorProperties(color)
                        padding
                        cornerRadius
                    }
                }
                cornerRadius
                loadingColor
            }
            is APGIFComponent -> {
                url
                border?.run {
                    active.run {
                        width
                        checkAPGradientColorProperties(color)
                        padding
                        cornerRadius
                    }
                    inactive.run {
                        width
                        checkAPGradientColorProperties(color)
                        padding
                        cornerRadius
                    }
                }
                cornerRadius
                loadingColor
            }
            is APButtonComponent -> {
                text.run {
                    value
                    font?.let { checkAPFontProperties(it) }
                }
                actions.forEach { checkAPActionProperties(it) }
                cornerRadius
                backgroundColor
            }
            is APPollComponent -> {
                id
                type
            }
            else -> { }
        }
    }
}

private fun checkAPFontProperties(apFont: APFont) {
    apFont.run {
        family
        style
        size
        color
        align
        letterSpacing
        lineHeight
    }
}

private fun checkAPGradientColorProperties(color: APGradientColor) {
    color.run {
        startColor
        endColor
        angle
    }
}

private fun checkAPActionProperties(apAction: APAction) {
    apAction.run {
        when (this) {
            is APShowStoryAction -> {
                checkAPStoryProperties(story)
            }
            is APOpenWebLinkAction -> {
                url
                isWebView
            }
            is APCustomAction -> {
                parameters
            }
            is APSendSMSAction -> {
                phoneNumber
                message
            }
            is APCallPhoneAction -> {
                phoneNumber
            }
            else -> { }
        }
    }
}

private fun checkAPStoryProperties(apStory: APStory) {
    apStory.run {
        id
        campaignId
        snaps.forEach { checkAPSnapProperties(it) }
    }
}

private fun checkAPSnapProperties(apSnap: APSnap) {
    apSnap.run {
        id
        layers.forEach { checkAPLayerProperties(it) }
        width
        height
        actionAreaHeight
        actionArea.run {
            when (this) {
                is APSnap.ButtonActionArea -> {
                    text.run {
                        value
                        font?.let { checkAPFontProperties(it) }
                    }
                    actions.forEach { checkAPActionProperties(it) }
                    cornerRadius
                    backgroundColor
                    border?.run {
                        width
                        checkAPGradientColorProperties(color)
                    }
                }
                else -> { }
            }
        }
        showTime
    }
}

internal fun magnifyAPCarouselViewDataModel(dataModel: APCarouselViewDataModel) = dataModel.run {
    APCarouselViewDataModel(
        id = id,
        options = APCarouselViewDataModel.Options(
            width = options.width * BASE_SIZE_MULTIPLIER_NEW,
            height = options.height * BASE_SIZE_MULTIPLIER_NEW,
            cornerRadius = options.cornerRadius * BASE_SIZE_MULTIPLIER_NEW,
            magnetize = options.magnetize,
            autoScroll = options.autoScroll,
            padding = magnifyAPPadding(options.padding),
            spacing = options.spacing * BASE_SIZE_MULTIPLIER_NEW,
            screenWidth = options.screenWidth * BASE_SIZE_MULTIPLIER_NEW,
            showBorder = options.showBorder
        ),
        entryPoints = entryPoints.map { magnifyAPEntryPoint(it) }
    )
}

internal fun magnifyAPSplashScreenViewDataModel(dataModel: APSplashScreenViewDataModel) = dataModel.run {
    APSplashScreenViewDataModel(
        id = id,
        options = APSplashScreenViewDataModel.Options(
            width = options.width * BASE_SIZE_MULTIPLIER_NEW,
            height = options.height * BASE_SIZE_MULTIPLIER_NEW,
            screenWidth = options.screenWidth * BASE_SIZE_MULTIPLIER_NEW
        ),
        splashScreens = splashScreens.map { magnifyAPSplashScreen(it) }
    )
}

private fun magnifyAPEntryPoint(entryPoint: APEntryPoint) = entryPoint.run {
    APEntryPoint(
        id = id,
        updatedAt = updatedAt,
        campaignId = campaignId,
        status = status,
        showOnce = showOnce,
        showCount = showCount,
        layers = layers.map { magnifyAPLayer(it) },
        actions = actions.map { magnifyAPEntryPointAction(it) }
    )
}

private fun magnifyAPSplashScreen(splashScreen: APSplashScreen) = splashScreen.run {
    APSplashScreen(
        id = id,
        campaignId = campaignId,
        status = status,
        showCount = showCount,
        showTime = showTime,
        layers = layers.map { magnifyAPLayer(it) },
        actions = actions
    )
}

private fun magnifyAPLayer(layer: APLayer) = layer.run {
    APLayer(
        type = type,
        options = APLayer.Options(
            position = APLayer.Position(
                x = options.position.x * BASE_SIZE_MULTIPLIER_NEW,
                y = options.position.y * BASE_SIZE_MULTIPLIER_NEW,
                width = options.position.width * BASE_SIZE_MULTIPLIER_NEW,
                height = options.position.height * BASE_SIZE_MULTIPLIER_NEW,
                angle = options.position.angle
            ),
            opacity = options.opacity
        ),
        component = component?.let { magnifyAPComponent(it) }
    )
}

private fun magnifyAPComponent(component: APComponent) = when (component) {
    is APButtonComponent -> component.run {
        APButtonComponent(
            text = APSnap.ButtonActionArea.Text(
                value = text.value,
                font = text.font?.let { magnifyAPFont(it) }
            ),
            actions = actions,
            cornerRadius = cornerRadius * BASE_SIZE_MULTIPLIER_NEW,
            backgroundColor = backgroundColor
        )
    }
    is APGIFComponent -> component.run {
        APGIFComponent(
            url = url,
            border = border?.let {
                APGIFComponent.Border(
                    active = APGIFComponent.Border.State(
                        width = it.active.width * BASE_SIZE_MULTIPLIER_NEW,
                        color = it.active.color,
                        padding = it.active.padding * BASE_SIZE_MULTIPLIER_NEW,
                        cornerRadius = it.active.cornerRadius * BASE_SIZE_MULTIPLIER_NEW
                    ),
                    inactive = APGIFComponent.Border.State(
                        width = it.inactive.width * BASE_SIZE_MULTIPLIER_NEW,
                        color = it.inactive.color,
                        padding = it.inactive.padding * BASE_SIZE_MULTIPLIER_NEW,
                        cornerRadius = it.inactive.cornerRadius * BASE_SIZE_MULTIPLIER_NEW
                    )
                )
            },
            cornerRadius = cornerRadius?.times(BASE_SIZE_MULTIPLIER_NEW),
            loadingColor = loadingColor
        )
    }
    is APImageComponent -> component.run {
        APImageComponent(
            url = url,
            border = border?.let {
                APImageComponent.Border(
                    active = APImageComponent.Border.State(
                        width = it.active.width * BASE_SIZE_MULTIPLIER_NEW,
                        color = it.active.color,
                        padding = it.active.padding * BASE_SIZE_MULTIPLIER_NEW,
                        cornerRadius = it.active.cornerRadius * BASE_SIZE_MULTIPLIER_NEW
                    ),
                    inactive = APImageComponent.Border.State(
                        width = it.inactive.width * BASE_SIZE_MULTIPLIER_NEW,
                        color = it.inactive.color,
                        padding = it.inactive.padding * BASE_SIZE_MULTIPLIER_NEW,
                        cornerRadius = it.inactive.cornerRadius * BASE_SIZE_MULTIPLIER_NEW
                    )
                )
            },
            cornerRadius = cornerRadius?.times(BASE_SIZE_MULTIPLIER_NEW),
            loadingColor = loadingColor
        )
    }
    is APTextComponent -> component.run {
        APTextComponent(
            value = value,
            font = font?.let { magnifyAPFont(it) }
        )
    }
    is APPollComponent -> component.run {
        APPollComponent(
            id = id,
            type = type
        )
    }
    else -> component
}

private fun magnifyAPEntryPointAction(action: APAction) = when (action) {
    is APShowStoryAction -> action.run {
        APShowStoryAction(
            story = magnifyAPStory(story)
        )
    }
    else -> action
}

private fun magnifyAPStory(story: APStory) = story.run {
    APStory(
        id = id,
        campaignId = campaignId,
        snaps = snaps.map { magnifyAPSnap(it) }
    )
}

private fun magnifyAPSnap(snap: APSnap) = snap.run {
    APSnap(
        id = id,
        layers = layers.map { magnifyAPLayer(it) },
        width = width * BASE_SIZE_MULTIPLIER_NEW,
        height = height * BASE_SIZE_MULTIPLIER_NEW,
        actionAreaHeight = actionAreaHeight?.times(BASE_SIZE_MULTIPLIER_NEW),
        actionArea = actionArea,
        showTime = showTime
    )
}

private fun magnifyAPPadding(padding: APPadding) = padding.run {
    APPadding(
        top = top * BASE_SIZE_MULTIPLIER_NEW,
        bottom = bottom * BASE_SIZE_MULTIPLIER_NEW,
        left = left * BASE_SIZE_MULTIPLIER_NEW,
        right = right * BASE_SIZE_MULTIPLIER_NEW
    )
}

internal fun magnifyAPFont(font: APFont) = font.run {
    APFont(
        family = family,
        style = style,
        size = size * BASE_SIZE_MULTIPLIER_NEW,
        color = color,
        align = align,
        letterSpacing = letterSpacing * BASE_SIZE_MULTIPLIER_NEW,
        lineHeight = lineHeight?.times(BASE_SIZE_MULTIPLIER_NEW)
    )
}

internal fun magnifyAPTemplateDataModel(dataModel: APTemplateDataModel) = dataModel.run {
    APTemplateDataModel(
        id = id,
        options = APTemplateDataModel.Options(
            width = options.width * BASE_SIZE_MULTIPLIER,
            height = options.height * BASE_SIZE_MULTIPLIER,
            cornerRadius = options.cornerRadius * BASE_SIZE_MULTIPLIER,
            magnetize = options.magnetize,
            autoScroll = options.autoScroll,
            padding = magnifyAPPadding(options.padding),
            spacing = options.spacing * BASE_SIZE_MULTIPLIER,
            screenWidth = options.screenWidth * BASE_SIZE_MULTIPLIER,
            showBorder = options.showBorder,
            outerStyles = magnifyOuterStyles(options.outerStyles)
        ),
        campaigns = campaigns.map { magnifyCampaign(it) }
    )
}

private fun magnifyOuterStyles(outerStyles: APOuterStyles?) = outerStyles?.run {
    APOuterStyles(
        width = width * BASE_SIZE_MULTIPLIER_NEW,
        height = height * BASE_SIZE_MULTIPLIER_NEW,
        cornerRadius = cornerRadius * BASE_SIZE_MULTIPLIER_NEW,
        hasTextUnderImage = hasTextUnderImage,
        outerSize = outerSize,
        outerImageLoadingColor = outerImageLoadingColor
    )
}

private fun magnifyCampaign(campaign: Campaign) = campaign.run {
    Campaign(
        id = id,
        updatedAt = updatedAt,
        body = magnifyCampaignBody(body),
        status = status,
        showCount = showCount
    )
}

private fun magnifyCampaignBody(body: Campaign.APBody)= body.run {
    Campaign.APBody(
        story = magnifyStory(story),
        instruction = magnifyStory(instruction)
    )
}

private fun magnifyStory(story: Story?) = story?.run {
    Story(
        id = id,
        type = type,
        body = magnifyStoryBody(body)
    )
}

private fun magnifyStoryBody(body: Story.Body) = body.run {
    Story.Body(
        campaignId = campaignId,
        snaps = snaps.map { magnifySnap(it) },
        outerText = magnifyValue(outerText),
        outerBorderColor = outerBorderColor,
        outerImageUrl = outerImageUrl,
        outerStyles = magnifyOuterStyles(outerStyles)
    )
}

private fun magnifySnap(snap: Snap) = snap.run{
    Snap(
        id = id,
        type = type,
        body = magnifySnapBody(body),
        position = position
    )
}

private fun magnifySnapBody(body: Snap.Body) = body.run {
    var area : Double? = null
    actionAreaHeight?.let {
        area = it * BASE_SIZE_MULTIPLIER_NEW
    }
    Snap.Body(
        layers = layers.map { magnifyLayer(it) },
        width = width * BASE_SIZE_MULTIPLIER_NEW,
        height = height * BASE_SIZE_MULTIPLIER_NEW,
        actionAreaHeight = area,
        actionArea = magnifyActionArea(actionArea),
        showTime = showTime
    )
}

private fun magnifyActionArea(actionArea: ActionArea?) = actionArea?.run {
    ActionArea(
        type = type,
        body = magnifyActionAreaBody(body)
    )
}

private fun magnifyActionAreaBody(body: ActionArea.Body?) = body?.run {
    ActionArea.Body(
        text = magnifyText(text),
        actions = actions.map { magnifyAction(it) },
        cornerRadius = cornerRadius * BASE_SIZE_MULTIPLIER_NEW,
        backgroundColor = backgroundColor,
        border = magnifyBorder(border)
    )
}

private fun magnifyBorder(border: APSnap.ButtonActionArea.Border) = border.run {
    APSnap.ButtonActionArea.Border(
        width = width * BASE_SIZE_MULTIPLIER_NEW,
        color = magnifyColor(color)
    )
}

private fun magnifyColor(color: APGradientColor) = color.run {
    APGradientColor(
        startColor = startColor,
        endColor = endColor,
        angle = angle
    )
}

private fun magnifyText(text: Text) = text.run {
    Text(
        font = magnifyNotNullFont(font),
        value = magnifyValue(value)
    )
}

private fun magnifyAction(action: Action) = action.run {
    Action(
        type = type,
        parameters = parameters,
        name = name
    )
}

private fun magnifyLayer(layer: Layer) = layer.run {
    Layer(
        type = type,
        options = magnifyLayerOptions(options),
        component = magnifyLayerComponent(component)
    )
}

private fun magnifyLayerComponent(component: Layer.Component?) = component?.run {
    var radius : Double? = null
    cornerRadius?.let {
        radius = it * BASE_SIZE_MULTIPLIER_NEW
    }
    Layer.Component(
        color = color,
        url = url,
        text = text,
        cornerRadius = radius,
        loadingColor = loadingColor,
        value = magnifyNullValue(value),
        font = magnifyFont(font),
        id = id,
        question = question,
        type = type,
        answers = answers
    )
}

private fun magnifyFont(font: APFont?) = font?.run {
    var height : Double? = null
    lineHeight?.let {
        height = it * BASE_SIZE_MULTIPLIER_NEW
    }
    APFont(
        family = family,
        style = style,
        size =  size * BASE_SIZE_MULTIPLIER_NEW,
        color = color,
        align = align,
        letterSpacing = letterSpacing * BASE_SIZE_MULTIPLIER_NEW,
        lineHeight = height
    )
}

private fun magnifyNotNullFont(font: APFont) = font.run {
    var height : Double? = null
    lineHeight?.let {
        height = it * BASE_SIZE_MULTIPLIER_NEW
    }
    APFont(
        family = family,
        style = style,
        size =  size * BASE_SIZE_MULTIPLIER_NEW,
        color = color,
        align = align,
        letterSpacing = letterSpacing * BASE_SIZE_MULTIPLIER_NEW,
        lineHeight = height
    )
}

private fun magnifyValue(value: APTextComponent.APLocale) = value.run {
    APTextComponent.APLocale(
        EN = EN,
        KZ = KZ,
        RU = RU
    )
}

private fun magnifyNullValue(value: APTextComponent.APLocale?) = value?.run {
    APTextComponent.APLocale(
        EN = EN,
        KZ = KZ,
        RU = RU
    )
}

private fun magnifyLayerOptions(options: APLayer.Options) = options.run {
    APLayer.Options(
        position = magnifyPosition(position),
        opacity = opacity
    )
}

private fun magnifyPosition(position:  APLayer.Position) = position.run {
    APLayer.Position(
        x = x * BASE_SIZE_MULTIPLIER_NEW,
        y = y * BASE_SIZE_MULTIPLIER_NEW,
        width = width * BASE_SIZE_MULTIPLIER_NEW,
        height = height * BASE_SIZE_MULTIPLIER_NEW,
        angle = angle
    )
}