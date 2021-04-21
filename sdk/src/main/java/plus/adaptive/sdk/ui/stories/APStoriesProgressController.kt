package plus.adaptive.sdk.ui.stories


internal interface APStoriesProgressController {
    fun moveToNextStory(storyId: String)
    fun moveToPrevStory(storyId: String)
    fun closeStories()
}