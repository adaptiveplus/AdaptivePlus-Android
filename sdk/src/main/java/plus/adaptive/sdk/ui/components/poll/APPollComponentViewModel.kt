package plus.adaptive.sdk.ui.components.poll

import com.google.gson.Gson
import plus.adaptive.sdk.core.managers.APSharedPreferences
import plus.adaptive.sdk.core.managers.APSharedPreferences.Companion.POLL_CHOSEN_ANSWER_ID
import plus.adaptive.sdk.core.managers.APSharedPreferences.Companion.POLL_DATA
import plus.adaptive.sdk.data.models.APError
import plus.adaptive.sdk.data.models.APPollData
import plus.adaptive.sdk.data.models.components.APPollComponent
import plus.adaptive.sdk.data.models.network.RequestResultCallback
import plus.adaptive.sdk.data.repositories.APPollRepository
import plus.adaptive.sdk.data.repositories.APUserRepository
import plus.adaptive.sdk.ui.components.core.APComponentContainerViewModel
import plus.adaptive.sdk.ui.components.core.APComponentLifecycleListener
import plus.adaptive.sdk.ui.components.core.vm.APBaseComponentViewModel
import plus.adaptive.sdk.utils.runOnMainThread


internal class APPollComponentViewModel(
    containerViewModel: APComponentContainerViewModel,
    lifecycleListener: APComponentLifecycleListener,
    private val component: APPollComponent,
    private val pollRepository: APPollRepository,
    private val userRepository: APUserRepository?,
    private val preferences: APSharedPreferences?
) : APBaseComponentViewModel(containerViewModel, lifecycleListener) {

    private var pollData: APPollData? = null


    override fun prepare() {
        lifecycleListener.onReady(false)
        mComponentViewController?.prepare()

        pollData = getCachedPollData()

        if (pollData == null) {
            pollRepository.requestPollData(
                pollId = component.id,
                object : RequestResultCallback<APPollData>() {
                    override fun success(response: APPollData) {
                        savePollData(response)
                        pollData = response
                        reset()
                    }

                    override fun failure(error: APError?) { }
                }
            )
        } else {
            reset()
            pollRepository.requestPollData(
                pollId = component.id,
                object : RequestResultCallback<APPollData>() {
                    override fun success(response: APPollData) {
                        savePollData(response)
                    }

                    override fun failure(error: APError?) { }
                }
            )
        }
    }

    override fun resume() {}

    override fun pause() {}

    override fun reset() {
        runOnMainThread {
            mComponentViewController?.reset()
        }
    }

    fun getPollData(): APPollData? = pollData

    fun onPollBuildSuccess() {
        lifecycleListener.onReady(true)
    }

    fun onPollBuildFail() {
        lifecycleListener.onError()
    }

    fun onAnswerChosen(answerId: String) {
        pollRepository.submitChosenAnswer(
            pollId = component.id,
            answerId = answerId,
            object: RequestResultCallback<Any?>() {
                override fun success(response: Any?) {
                    saveChosenAnswerId(answerId)
                }

                override fun failure(error: APError?) {}
            }
        )
    }

    private fun saveChosenAnswerId(answerId: String) {
        userRepository?.getAPUserId()?.let { userId ->
            val prefKey = "${userId}_${component.id}_${POLL_CHOSEN_ANSWER_ID}"
            preferences?.saveString(prefKey, answerId)
        }
    }

    fun getChosenAnswerId() : String? {
        return userRepository?.getAPUserId()?.let { userId ->
            val prefKey = "${userId}_${component.id}_${POLL_CHOSEN_ANSWER_ID}"
            preferences?.getString(prefKey)
        }
    }

    private fun savePollData(pollData: APPollData) {
        userRepository?.getAPUserId()?.let { userId ->
            val prefKey = "${userId}_${component.id}_${POLL_DATA}"
            val serializedPollData = Gson().toJson(pollData)
            preferences?.saveString(prefKey, serializedPollData)
        }
    }

    private fun getCachedPollData() : APPollData? {
        return userRepository?.getAPUserId()?.let { userId ->
            val prefKey = "${userId}_${component.id}_${POLL_DATA}"
            val serializedPollData = preferences?.getString(prefKey)
            try {
                Gson().fromJson(serializedPollData, APPollData::class.java)
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }
}