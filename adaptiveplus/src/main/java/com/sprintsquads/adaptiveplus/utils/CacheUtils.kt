package com.sprintsquads.adaptiveplus.utils

import android.content.Context
import com.sprintsquads.adaptiveplus.data.models.APViewDataModel
import java.io.*


internal fun loadAPViewDataModelFromCache(
    ctx: Context,
    apViewId: String,
    userId: String,
    onResult: (dataModel: APViewDataModel?) -> Unit
) {
    try {
        val dataModelFile = File(ctx.cacheDir, "${userId}_$apViewId.json")
        val inputStream: InputStream = dataModelFile.inputStream()
        val size = inputStream.available()
        val buffer = ByteArray(size)

        inputStream.read(buffer)
        inputStream.close()

        val json = String(buffer, Charsets.UTF_8)

        val dataModel = getDeserializedAPViewDataModel(json)
        onResult(dataModel)
    } catch (ex: FileNotFoundException) {
        ex.printStackTrace()
        onResult(null)
    } catch (ex: IOException) {
        ex.printStackTrace()
        onResult(null)
    }
}

internal fun saveAPViewDataModelToCache(
    ctx: Context,
    apViewId: String,
    userId: String,
    dataModel: APViewDataModel
) {
    try {
        val dataModelFile = File(ctx.cacheDir, "${userId}_$apViewId.json")
        dataModelFile.createNewFile()
        val outputStream: OutputStream = dataModelFile.outputStream()
        val json = getSerializedAPViewDataModel(dataModel)

        if (json != null) {
            outputStream.write(json.toByteArray())
        }

        outputStream.flush()
        outputStream.close()
    } catch (ex: IOException) {
        ex.printStackTrace()
    }
}

internal fun removeAPViewDataModelFromCache(
    ctx: Context,
    apViewId: String,
    userId: String
) {
    try {
        val dataModelFile = File(ctx.cacheDir, "${userId}_$apViewId.json")
        dataModelFile.delete()
    } catch (ex: IOException) {
        ex.printStackTrace()
    }
}

internal fun loadAPViewMockDataModelFromAssets(
    ctx: Context,
    apViewId: String,
    onSuccess: (dataModel: APViewDataModel) -> Unit
) {
    try {
        val inputStream: InputStream =
            ctx.assets.open("$apViewId.json")
        val size = inputStream.available()
        val buffer = ByteArray(size)

        inputStream.read(buffer)
        inputStream.close()

        val json = String(buffer, Charsets.UTF_8)

        val dataModel = getDeserializedAPViewDataModel(json)
        if (dataModel != null) {
            onSuccess(dataModel)
        }
    } catch (ex: IOException) {
        ex.printStackTrace()
    }
}