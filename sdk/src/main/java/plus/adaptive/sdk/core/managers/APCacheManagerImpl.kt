package plus.adaptive.sdk.core.managers

import android.content.Context
import plus.adaptive.sdk.data.models.APSplashScreenViewDataModel
import plus.adaptive.sdk.data.models.APCarouselViewDataModel
import plus.adaptive.sdk.data.repositories.APUserRepository
import plus.adaptive.sdk.utils.*
import java.io.*


internal class APCacheManagerImpl(
    private val context: Context,
    private val userRepository: APUserRepository
) : APCacheManager {

    override fun loadAPCarouselViewDataModelFromAssets(
        apViewId: String,
        onSuccess: (dataModel: APCarouselViewDataModel) -> Unit
    ) {
        try {
            val inputStream: InputStream =
                context.assets.open("$apViewId.json")
            val size = inputStream.available()
            val buffer = ByteArray(size)

            inputStream.read(buffer)
            inputStream.close()

            val json = String(buffer, Charsets.UTF_8)

            val dataModel = getDeserializedUnprocessedAPCarouselViewDataModel(json)
            if (dataModel != null) {
                onSuccess.invoke(dataModel)
            }
        } catch (ex: IOException) {
            ex.printStackTrace()
        }
    }

    override fun loadAPCarouselViewDataModelFromCache(
        apViewId: String,
        onResult: (dataModel: APCarouselViewDataModel?) -> Unit
    ) {
        try {
            val userId = userRepository.getAPUser().apId ?: ""
            val dataModelFile = File(context.cacheDir, "${userId}_carousel_$apViewId.json")
            val inputStream: InputStream = dataModelFile.inputStream()
            val size = inputStream.available()
            val buffer = ByteArray(size)

            inputStream.read(buffer)
            inputStream.close()

            val json = String(buffer, Charsets.UTF_8)

            val dataModel = getDeserializedProcessedAPCarouselViewDataModel(json)
            onResult(dataModel)
        } catch (ex: FileNotFoundException) {
            onResult(null)
        } catch (ex: IOException) {
            ex.printStackTrace()
            onResult(null)
        }
    }

    override fun saveAPCarouselViewDataModelToCache(
        apViewId: String,
        dataModel: APCarouselViewDataModel
    ) {
        try {
            val userId = userRepository.getAPUser().apId ?: ""
            val dataModelFile = File(context.cacheDir, "${userId}_carousel_$apViewId.json")
            dataModelFile.createNewFile()
            val outputStream: OutputStream = dataModelFile.outputStream()
            val json = getSerializedProcessedAPCarouselViewDataModel(dataModel)

            if (json != null) {
                outputStream.write(json.toByteArray())
            }

            outputStream.flush()
            outputStream.close()
        } catch (ex: IOException) {
            ex.printStackTrace()
        }
    }

    override fun removeAPCarouselViewDataModelFromCache(
        apViewId: String
    ) {
        try {
            val userId = userRepository.getAPUser().apId ?: ""
            val dataModelFile = File(context.cacheDir, "${userId}_carousel_$apViewId.json")
            dataModelFile.delete()
        } catch (ex: IOException) {
            ex.printStackTrace()
        }
    }

    override fun loadAPSplashScreenViewDataModelFromAssets(
        onResult: (dataModel: APSplashScreenViewDataModel?) -> Unit
    ) {
        try {
            val inputStream: InputStream =
                context.assets.open("splashscreen.json")
            val size = inputStream.available()
            val buffer = ByteArray(size)

            inputStream.read(buffer)
            inputStream.close()

            val json = String(buffer, Charsets.UTF_8)

            val dataModel = getDeserializedUnprocessedAPSplashScreenViewDataModel(json)
            onResult.invoke(dataModel)
        } catch (ex: IOException) {
            ex.printStackTrace()
            onResult.invoke(null)
        }
    }

    override fun loadAPSplashScreenViewDataModelFromCache(
        onResult: (dataModel: APSplashScreenViewDataModel?) -> Unit
    ) {
        try {
            val userId = userRepository.getAPUser().apId ?: ""
            val dataModelFile = File(context.cacheDir, "${userId}_splashscreen.json")
            val inputStream: InputStream = dataModelFile.inputStream()
            val size = inputStream.available()
            val buffer = ByteArray(size)

            inputStream.read(buffer)
            inputStream.close()

            val json = String(buffer, Charsets.UTF_8)

            val dataModel = getDeserializedProcessedAPSplashScreenViewDataModel(json)
            onResult(dataModel)
        } catch (ex: FileNotFoundException) {
            onResult(null)
        } catch (ex: IOException) {
            ex.printStackTrace()
            onResult(null)
        }
    }

    override fun saveAPSplashScreenViewDataModelToCache(
        dataModel: APSplashScreenViewDataModel,
        onSuccess: (() -> Unit)?
    ) {
        try {
            val userId = userRepository.getAPUser().apId ?: ""
            val dataModelFile = File(context.cacheDir, "${userId}_splashscreen.json")
            dataModelFile.createNewFile()
            val outputStream: OutputStream = dataModelFile.outputStream()
            val json = getSerializedProcessedAPSplashScreenViewDataModel(dataModel)

            if (json != null) {
                outputStream.write(json.toByteArray())
                onSuccess?.invoke()
            }

            outputStream.flush()
            outputStream.close()
        } catch (ex: IOException) {
            ex.printStackTrace()
        }
    }

    override fun removeAPSplashScreenViewDataModelFromCache() {
        try {
            val userId = userRepository.getAPUser().apId ?: ""
            val dataModelFile = File(context.cacheDir, "${userId}_splashscreen.json")
            dataModelFile.delete()
        } catch (ex: IOException) {
            ex.printStackTrace()
        }
    }
}