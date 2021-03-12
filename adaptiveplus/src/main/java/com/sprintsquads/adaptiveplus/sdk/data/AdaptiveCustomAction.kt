package com.sprintsquads.adaptiveplus.sdk.data


interface AdaptiveCustomAction {
    fun onRun(params: HashMap<String, Any>)
}