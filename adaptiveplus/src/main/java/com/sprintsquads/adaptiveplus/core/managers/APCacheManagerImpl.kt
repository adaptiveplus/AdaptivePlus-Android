package com.sprintsquads.adaptiveplus.core.managers

import android.content.Context
import com.sprintsquads.adaptiveplus.data.models.APViewDataModel
import com.sprintsquads.adaptiveplus.sdk.AdaptivePlusSDK
import com.sprintsquads.adaptiveplus.utils.getDeserializedAPViewDataModel
import com.sprintsquads.adaptiveplus.utils.getSerializedDataModel
import java.io.*


internal class APCacheManagerImpl(
    private val context: Context
) : APCacheManager {
    @Deprecated(
        message = "Only for development purposes.",
        level = DeprecationLevel.WARNING)
    override fun loadAPViewMockDataModelFromAssets(
        apViewId: String,
        onSuccess: (dataModel: APViewDataModel) -> Unit
    ) {
        try {
            val inputStream: InputStream =
                context.assets.open("$apViewId.json")
            val size = inputStream.available()
            val buffer = ByteArray(size)

            inputStream.read(buffer)
            inputStream.close()

            val json = String(buffer, Charsets.UTF_8)

            val dataModel = getDeserializedAPViewDataModel(json)
            if (dataModel != null) {
                onSuccess.invoke(dataModel)
            }
        } catch (ex: IOException) {
            ex.printStackTrace()
        }
    }

    override fun loadAPViewDataModelFromCache(
        apViewId: String,
        onResult: (dataModel: APViewDataModel?) -> Unit
    ) {
        try {
            val userId = AdaptivePlusSDK().getUserId() ?: ""
            val dataModelFile = File(context.cacheDir, "${userId}_$apViewId.json")
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

    override fun saveAPViewDataModelToCache(
        apViewId: String,
        dataModel: APViewDataModel
    ) {
        try {
            val userId = AdaptivePlusSDK().getUserId() ?: ""
            val dataModelFile = File(context.cacheDir, "${userId}_$apViewId.json")
            dataModelFile.createNewFile()
            val outputStream: OutputStream = dataModelFile.outputStream()
            val json = getSerializedDataModel(dataModel)

            if (json != null) {
                outputStream.write(json.toByteArray())
            }

            outputStream.flush()
            outputStream.close()
        } catch (ex: IOException) {
            ex.printStackTrace()
        }
    }

    override fun removeAPViewDataModelFromCache(
        apViewId: String
    ) {
        try {
            val userId = AdaptivePlusSDK().getUserId() ?: ""
            val dataModelFile = File(context.cacheDir, "${userId}_$apViewId.json")
            dataModelFile.delete()
        } catch (ex: IOException) {
            ex.printStackTrace()
        }
    }
}