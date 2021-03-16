package com.sprintsquads.adaptiveplus.ui.stories


internal interface APStoriesProgressController {
    fun moveToNextStory(storyId: String)
    fun moveToPrevStory(storyId: String)
    fun closeStories()
}