package plus.adaptive.sdk.data.listeners


interface APSplashScreenListener {

    fun onFinish() {}

    fun onRunAPCustomAction(params: HashMap<String, Any>) {}
}