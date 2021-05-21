package plus.adaptive.sdk.utils

import com.google.gson.*
import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken
import plus.adaptive.sdk.core.analytics.APCrashlytics
import plus.adaptive.sdk.data.BASE_SIZE_MULTIPLIER
import plus.adaptive.sdk.data.models.*
import plus.adaptive.sdk.data.models.actions.*
import plus.adaptive.sdk.data.models.components.*


private fun getProcessedAPViewGson(): Gson {
    val gsonBuilder = GsonBuilder()
    gsonBuilder.registerTypeAdapter(APAction::class.java, apEntryPointActionSerializer)
    gsonBuilder.registerTypeAdapter(APLayer::class.java, apLayerDeserializer)
    gsonBuilder.registerTypeAdapter(APAction::class.java, apEntryPointActionDeserializer)
    return gsonBuilder.create()
}

private fun getProcessedAPSplashScreenGson(): Gson {
    val gsonBuilder = GsonBuilder()
    gsonBuilder.registerTypeAdapter(APLayer::class.java, apLayerDeserializer)
    return gsonBuilder.create()
}

internal fun getSerializedProcessedAPCarouselViewDataModel(dataModel: APCarouselViewDataModel): String? {
    return getProcessedAPViewGson().toJson(dataModel)
}

internal fun getSerializedProcessedAPSplashScreenModel(dataModel: APSplashScreenViewDataModel): String? {
    return getProcessedAPSplashScreenGson().toJson(dataModel)
}

internal fun getDeserializedProcessedAPCarouselViewDataModel(json: String): APCarouselViewDataModel? {
    return try {
        val dataModel = getProcessedAPViewGson().fromJson(json, APCarouselViewDataModel::class.java)
        checkAPCarouselViewDataModelProperties(dataModel)
        dataModel
    } catch (e: Exception) {
        APCrashlytics.logCrash(e)
        e.printStackTrace()
        null
    }
}

internal fun getDeserializedProcessedAPSplashScreenModel(json: String): APSplashScreenViewDataModel? {
    return try {
        val dataModel = getProcessedAPSplashScreenGson().fromJson(json, APSplashScreenViewDataModel::class.java)
        checkAPSplashScreenViewDataModelProperties(dataModel)
        dataModel
    } catch (e: Exception) {
        APCrashlytics.logCrash(e)
        e.printStackTrace()
        null
    }
}

internal fun getUnprocessedAPViewGson(): Gson {
    val gsonBuilder = GsonBuilder()
    gsonBuilder.registerTypeAdapter(APCarouselViewDataModel::class.java, apCarouselViewDataModelDeserializer)
    return gsonBuilder.create()
}

internal fun getUnprocessedAPSplashScreenGson(): Gson {
    val gsonBuilder = GsonBuilder()
    gsonBuilder.registerTypeAdapter(APSplashScreenViewDataModel::class.java, apSplashScreenViewDataModelDeserializer)
    return gsonBuilder.create()
}

internal fun getDeserializedUnprocessedAPCarouselViewDataModel(json: String): APCarouselViewDataModel? {
    return try {
        val dataModel = getUnprocessedAPViewGson().fromJson(json, APCarouselViewDataModel::class.java)
        checkAPCarouselViewDataModelProperties(dataModel)
        magnifyAPCarouselViewDataModel(dataModel)
    } catch (e: Exception) {
        APCrashlytics.logCrash(e)
        e.printStackTrace()
        null
    }
}

internal fun getDeserializedUnprocessedAPSplashScreenModel(json: String): APSplashScreenViewDataModel? {
    return try {
        val dataModel = getUnprocessedAPSplashScreenGson()
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
                    val entryPointJsonObject = campaignBodyJsonObject.get("entryPoint").asJsonObject
                    val entryPointId = entryPointJsonObject.get("id").asString
                    val entryPointBodyJsonObject = entryPointJsonObject.get("body").asJsonObject
                    entryPointBodyJsonObject.addProperty("id", entryPointId)
                    entryPointBodyJsonObject.addProperty("updatedAt", updatedAt)
                    entryPointBodyJsonObject.addProperty("campaignId", campaignId)
                    entryPointBodyJsonObject.addProperty("status", status)
                    entryPointBodyJsonObject.addProperty("showOnce", showOnce)

                    val gsonBuilder = GsonBuilder()
                    gsonBuilder.registerTypeAdapter(APLayer::class.java, apLayerDeserializer)
                    gsonBuilder.registerTypeAdapter(APAction::class.java, apEntryPointActionDeserializer)
                    val apEntryPointGson = gsonBuilder.create()
                    val apEntryPoint = apEntryPointGson.fromJson(
                        entryPointBodyJsonObject.toString(),
                        APEntryPoint::class.java)

                    checkAPEntryPointProperties(apEntryPoint)

                    apEntryPoint
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
                    APOpenWebLinkAction(url)
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
                    APOpenWebLinkAction(url)
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
        showOnce
        layers.forEach { checkAPLayerProperties(it) }
        actions.forEach { checkAPActionProperties(it) }
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
            width = options.width * BASE_SIZE_MULTIPLIER,
            height = options.height * BASE_SIZE_MULTIPLIER,
            cornerRadius = options.cornerRadius * BASE_SIZE_MULTIPLIER,
            magnetize = options.magnetize,
            autoScroll = options.autoScroll,
            padding = magnifyAPPadding(options.padding),
            spacing = options.spacing * BASE_SIZE_MULTIPLIER,
            screenWidth = options.screenWidth * BASE_SIZE_MULTIPLIER,
            showBorder = options.showBorder
        ),
        entryPoints = entryPoints.map { magnifyAPEntryPoint(it) }
    )
}

internal fun magnifyAPSplashScreenViewDataModel(dataModel: APSplashScreenViewDataModel) = dataModel.run {
    APSplashScreenViewDataModel(
        id = id,
        options = APSplashScreenViewDataModel.Options(
            width = options.width * BASE_SIZE_MULTIPLIER,
            height = options.height * BASE_SIZE_MULTIPLIER,
            screenWidth = options.screenWidth * BASE_SIZE_MULTIPLIER
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
                x = options.position.x * BASE_SIZE_MULTIPLIER,
                y = options.position.y * BASE_SIZE_MULTIPLIER,
                width = options.position.width * BASE_SIZE_MULTIPLIER,
                height = options.position.height * BASE_SIZE_MULTIPLIER,
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
            text = APButtonComponent.Text(
                value = text.value,
                font = text.font?.let { magnifyAPFont(it) }
            ),
            actions = actions,
            cornerRadius = cornerRadius * BASE_SIZE_MULTIPLIER,
            backgroundColor = backgroundColor
        )
    }
    is APGIFComponent -> component.run {
        APGIFComponent(
            url = url,
            border = border?.let {
                APGIFComponent.Border(
                    active = APGIFComponent.Border.State(
                        width = it.active.width * BASE_SIZE_MULTIPLIER,
                        color = it.active.color,
                        padding = it.active.padding * BASE_SIZE_MULTIPLIER,
                        cornerRadius = it.active.cornerRadius * BASE_SIZE_MULTIPLIER
                    ),
                    inactive = APGIFComponent.Border.State(
                        width = it.inactive.width * BASE_SIZE_MULTIPLIER,
                        color = it.inactive.color,
                        padding = it.inactive.padding * BASE_SIZE_MULTIPLIER,
                        cornerRadius = it.inactive.cornerRadius * BASE_SIZE_MULTIPLIER
                    )
                )
            },
            cornerRadius = cornerRadius?.times(BASE_SIZE_MULTIPLIER),
            loadingColor = loadingColor
        )
    }
    is APImageComponent -> component.run {
        APImageComponent(
            url = url,
            border = border?.let {
                APImageComponent.Border(
                    active = APImageComponent.Border.State(
                        width = it.active.width * BASE_SIZE_MULTIPLIER,
                        color = it.active.color,
                        padding = it.active.padding * BASE_SIZE_MULTIPLIER,
                        cornerRadius = it.active.cornerRadius * BASE_SIZE_MULTIPLIER
                    ),
                    inactive = APImageComponent.Border.State(
                        width = it.inactive.width * BASE_SIZE_MULTIPLIER,
                        color = it.inactive.color,
                        padding = it.inactive.padding * BASE_SIZE_MULTIPLIER,
                        cornerRadius = it.inactive.cornerRadius * BASE_SIZE_MULTIPLIER
                    )
                )
            },
            cornerRadius = cornerRadius?.times(BASE_SIZE_MULTIPLIER),
            loadingColor = loadingColor
        )
    }
    is APTextComponent -> component.run {
        APTextComponent(
            value = value,
            font = font?.let { magnifyAPFont(it) }
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
        width = width * BASE_SIZE_MULTIPLIER,
        height = height * BASE_SIZE_MULTIPLIER,
        actionAreaHeight = actionAreaHeight?.times(BASE_SIZE_MULTIPLIER),
        actionArea = actionArea,
        showTime = showTime
    )
}

private fun magnifyAPPadding(padding: APPadding) = padding.run {
    APPadding(
        top = top * BASE_SIZE_MULTIPLIER,
        bottom = bottom * BASE_SIZE_MULTIPLIER,
        left = left * BASE_SIZE_MULTIPLIER,
        right = right * BASE_SIZE_MULTIPLIER
    )
}

private fun magnifyAPFont(font: APFont) = font.run {
    APFont(
        family = family,
        style = style,
        size = size * BASE_SIZE_MULTIPLIER,
        color = color,
        align = align,
        letterSpacing = letterSpacing * BASE_SIZE_MULTIPLIER,
        lineHeight = lineHeight?.times(BASE_SIZE_MULTIPLIER)
    )
}