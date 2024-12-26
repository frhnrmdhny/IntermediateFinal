package com.dicoding.picodiploma.loginwithanimation.ui.story

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.picodiploma.loginwithanimation.data.StoriesRepository
import com.dicoding.picodiploma.loginwithanimation.data.remote.response.Story
import kotlinx.coroutines.launch

class StoriesDetailViewModel(private val storiesRepository: StoriesRepository) : ViewModel() {
    private val _story = MutableLiveData<Story>()
    val story: LiveData<Story> get() = _story

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    fun getDetailStory(id: String) = viewModelScope.launch {
        try {
            val response = storiesRepository.getDetailStories(id)
            _story.postValue(response)
        } catch (e: Exception) {
            _error.postValue("Failed to load stories: ${e.message}")
            Log.e("StoryViewModel", "Error fetching stories", e)
        }
    }
}