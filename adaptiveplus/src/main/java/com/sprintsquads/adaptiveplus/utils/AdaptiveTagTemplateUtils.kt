package com.sprintsquads.adaptiveplus.utils

import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.sprintsquads.adaptiveplus.data.models.AdaptiveTagTemplate


internal fun getSerializedTagTemplate(template: AdaptiveTagTemplate): String? {
    return Gson().toJson(template)
}

internal fun getDeserializedTagTemplate(json: String): AdaptiveTagTemplate? {
    return try {
        Gson().fromJson(json, AdaptiveTagTemplate::class.java)
    } catch (e: JsonSyntaxException) {
        e.printStackTrace()
        null
    }
}

internal fun isTagTemplateNullOrEmpty(template: AdaptiveTagTemplate?): Boolean {
    return template?.entries.isNullOrEmpty()
}