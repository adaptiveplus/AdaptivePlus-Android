package com.sprintsquads.adaptiveplus.utils

import android.content.Context
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.sprintsquads.adaptiveplus.data.GLIDE_TIMEOUT
import com.sprintsquads.adaptiveplus.data.models.AdaptiveTagTemplate
import java.io.*


internal fun loadTemplateFromCache(
    ctx: Context,
    tagId: String,
    userId: String,
    onResult: (template: AdaptiveTagTemplate?) -> Unit
) {
    try {
        val templateFile = File(ctx.cacheDir, "${userId}_$tagId.json")
        val inputStream: InputStream = templateFile.inputStream()
        val size = inputStream.available()
        val buffer = ByteArray(size)

        inputStream.read(buffer)
        inputStream.close()

        val json = String(buffer, Charsets.UTF_8)

        val template = getDeserializedTagTemplate(json)
        onResult(template)
    } catch (ex: FileNotFoundException) {
        ex.printStackTrace()
        onResult(null)
    } catch (ex: IOException) {
        ex.printStackTrace()
        onResult(null)
    }
}

internal fun loadTemplateFromCache(
    ctx: Context,
    tagId: String,
    userId: String
): AdaptiveTagTemplate? {
    try {
        val templateFile = File(ctx.cacheDir, "${userId}_$tagId.json")
        val inputStream: InputStream = templateFile.inputStream()
        val size = inputStream.available()
        val buffer = ByteArray(size)

        inputStream.read(buffer)
        inputStream.close()

        val json = String(buffer, Charsets.UTF_8)

        return getDeserializedTagTemplate(json)
    } catch (ex: FileNotFoundException) {
        ex.printStackTrace()
        return null
    } catch (ex: IOException) {
        ex.printStackTrace()
        return null
    }
}

internal fun saveTemplateToCache(
    ctx: Context,
    tagId: String,
    userId: String,
    template: AdaptiveTagTemplate
) {
    try {
        val templateFile = File(ctx.cacheDir, "${userId}_$tagId.json")
        templateFile.createNewFile()
        val outputStream: OutputStream = templateFile.outputStream()
        val json = getSerializedTagTemplate(template)

        if (json != null) {
            outputStream.write(json.toByteArray())
        }

        outputStream.flush()
        outputStream.close()
    } catch (ex: IOException) {
        ex.printStackTrace()
    }
}

internal fun removeTemplateFromCache(
    ctx: Context,
    tagId: String,
    userId: String
) {
    try {
        val templateFile = File(ctx.cacheDir, "${userId}_$tagId.json")
        templateFile.delete()
    } catch (ex: IOException) {
        ex.printStackTrace()
    }
}

internal fun loadMockTemplateFromAssets(
    ctx: Context,
    tagId: String,
    onSuccess: (template: AdaptiveTagTemplate) -> Unit
) {
    try {
        val inputStream: InputStream =
            ctx.assets.open("$tagId.json")
        val size = inputStream.available()
        val buffer = ByteArray(size)

        inputStream.read(buffer)
        inputStream.close()

        val json = String(buffer, Charsets.UTF_8)

        val template = getDeserializedTagTemplate(json)
        if (template != null) {
            onSuccess(template)
        }
    } catch (ex: IOException) {
        ex.printStackTrace()
    }
}

internal fun preloadImage(ctx: Context, imageUrl: String) {
    Glide.with(ctx)
        .load(imageUrl)
        .timeout(GLIDE_TIMEOUT)
        .diskCacheStrategy(DiskCacheStrategy.DATA)
        .preload()
}