package plus.adaptive.sdk.ui.apview.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import plus.adaptive.sdk.core.managers.APCacheManager
import plus.adaptive.sdk.core.managers.APSharedPreferences
import plus.adaptive.sdk.data.models.APError
import plus.adaptive.sdk.data.models.network.RequestResultCallback
import plus.adaptive.sdk.data.models.story.APTemplateDataModel
import plus.adaptive.sdk.data.repositories.APUserRepository
import plus.adaptive.sdk.data.repositories.APViewRepository
import plus.adaptive.sdk.utils.runOnMainThread


internal class APInstructionViewModel(
    private val apViewRepository: APViewRepository,
    private val userRepository: APUserRepository,
    private val cacheManager: APCacheManager,
    private val preferences: APSharedPreferences
) : ViewModel(){

    val instructionDataModelLiveData: LiveData<APTemplateDataModel?>
        get() = _instructionDataModelLiveData

    private val _instructionDataModelLiveData = MutableLiveData<APTemplateDataModel?>()

    private fun setInstructionDataModel(
        dataModel: APTemplateDataModel,
        isCached: Boolean = false,
        appViewId: String
    ) {
//        if (!isCached) {
            saveAPTemplateViewDataModelToCache(appViewId, dataModel)
//        }
        _instructionDataModelLiveData.value = dataModel
    }

    private fun saveAPTemplateViewDataModelToCache(appViewId: String, dataModel: APTemplateDataModel) {
        cacheManager.saveAPTemplateViewDataModelToCache(appViewId, dataModel)
    }

    fun requestTemplate(apViewId: String, hasDrafts: Boolean = false) {
        apViewRepository.requestTemplate(
            apViewId, hasDrafts, object: RequestResultCallback<APTemplateDataModel>() {
                override fun success(response: APTemplateDataModel) {
                    runOnMainThread {
                        setInstructionDataModel(
                            dataModel = response,
                            appViewId = apViewId
                        )
                    }
                }

                override fun failure(error: APError?) {
                    runOnMainThread {
                        _instructionDataModelLiveData.value?.let {
                            setInstructionDataModel(
                                dataModel = it,
                                appViewId = apViewId
                            )
                        }
                    }
                }
            }
        )
    }

    fun loadTemplateFromCache(apViewId: String) {
        cacheManager.loadAPTemplateViewDataModelFromCache(apViewId) { dataModel ->
            if (dataModel != null) {

                runOnMainThread {
                    setInstructionDataModel(
                        dataModel = dataModel,
                        isCached = true,
                        appViewId = apViewId
                    )
                }
            }
        }
    }
}