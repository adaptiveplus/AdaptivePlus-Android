package com.sprintsquads.adaptiveplus.sdk.data


interface APCustomAction {
    fun onRun(params: HashMap<String, Any>)
}