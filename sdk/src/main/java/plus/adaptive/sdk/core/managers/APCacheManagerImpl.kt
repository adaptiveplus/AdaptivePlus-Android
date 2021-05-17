package plus.adaptive.sdk.core.managers

import android.content.Context
import plus.adaptive.sdk.data.models.APLaunchScreenTemplate
import plus.adaptive.sdk.data.models.APViewDataModel
import plus.adaptive.sdk.data.repositories.APUserRepository
import plus.adaptive.sdk.utils.*
import java.io.*


internal class APCacheManagerImpl(
    private val context: Context,
    private val userRepository: APUserRepository
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

            val dataModel = getDeserializedUnprocessedAPViewDataModel(json)
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
            val userId = userRepository.getAPUser().apId ?: ""
            val dataModelFile = File(context.cacheDir, "${userId}_apview_$apViewId.json")
            val inputStream: InputStream = dataModelFile.inputStream()
            val size = inputStream.available()
            val buffer = ByteArray(size)

            inputStream.read(buffer)
            inputStream.close()

            val json = String(buffer, Charsets.UTF_8)

            val dataModel = getDeserializedProcessedAPViewDataModel(json)
            onResult(dataModel)
        } catch (ex: FileNotFoundException) {
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
            val userId = userRepository.getAPUser().apId ?: ""
            val dataModelFile = File(context.cacheDir, "${userId}_apview_$apViewId.json")
            dataModelFile.createNewFile()
            val outputStream: OutputStream = dataModelFile.outputStream()
            val json = getSerializedProcessedAPViewDataModel(dataModel)

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
            val userId = userRepository.getAPUser().apId ?: ""
            val dataModelFile = File(context.cacheDir, "${userId}_apview_$apViewId.json")
            dataModelFile.delete()
        } catch (ex: IOException) {
            ex.printStackTrace()
        }
    }

    override fun loadAPLaunchScreenMockModelFromAssets(
        onSuccess: (dataModel: APLaunchScreenTemplate) -> Unit
    ) {
        try {
            val inputStream: InputStream =
                context.assets.open("launchscreen.json")
            val size = inputStream.available()
            val buffer = ByteArray(size)

            inputStream.read(buffer)
            inputStream.close()

            val json = String(buffer, Charsets.UTF_8)

            val dataModel = getDeserializedUnprocessedAPLaunchScreenModel(json)
            if (dataModel != null) {
                onSuccess.invoke(dataModel)
            }
        } catch (ex: IOException) {
            ex.printStackTrace()
        }
    }

    override fun loadAPLaunchScreenModelFromCache(
        onResult: (dataModel: APLaunchScreenTemplate?) -> Unit
    ) {
        try {
            val userId = userRepository.getAPUser().apId ?: ""
            val dataModelFile = File(context.cacheDir, "${userId}_launchscreen.json")
            val inputStream: InputStream = dataModelFile.inputStream()
            val size = inputStream.available()
            val buffer = ByteArray(size)

            inputStream.read(buffer)
            inputStream.close()

            val json = String(buffer, Charsets.UTF_8)

            val dataModel = getDeserializedProcessedAPLaunchScreenModel(json)
            onResult(dataModel)
        } catch (ex: FileNotFoundException) {
            onResult(null)
        } catch (ex: IOException) {
            ex.printStackTrace()
            onResult(null)
        }
    }

    override fun saveAPLaunchScreenModelToCache(
        dataModel: APLaunchScreenTemplate
    ) {
        try {
            val userId = userRepository.getAPUser().apId ?: ""
            val dataModelFile = File(context.cacheDir, "${userId}_launchscreen.json")
            dataModelFile.createNewFile()
            val outputStream: OutputStream = dataModelFile.outputStream()
            val json = getSerializedProcessedAPLaunchScreenModel(dataModel)

            if (json != null) {
                outputStream.write(json.toByteArray())
            }

            outputStream.flush()
            outputStream.close()
        } catch (ex: IOException) {
            ex.printStackTrace()
        }
    }

    override fun removeAPLaunchScreenModelFromCache() {
        try {
            val userId = userRepository.getAPUser().apId ?: ""
            val dataModelFile = File(context.cacheDir, "${userId}_launchscreen.json")
            dataModelFile.delete()
        } catch (ex: IOException) {
            ex.printStackTrace()
        }
    }
}