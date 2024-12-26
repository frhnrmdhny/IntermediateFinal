package com.dicoding.picodiploma.loginwithanimation.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.dicoding.picodiploma.loginwithanimation.data.StoriesRepository
import com.dicoding.picodiploma.loginwithanimation.data.remote.response.AddStoryResponse
import com.dicoding.picodiploma.loginwithanimation.data.remote.response.ListStoryItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody

class HomeViewModel(private val storiesRepository: StoriesRepository) : ViewModel() {

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    private val _uploadSuccess = MutableLiveData<AddStoryResponse>()
    val uploadSuccess: LiveData<AddStoryResponse> get() = _uploadSuccess

    private val _uploadStoryResponse = MutableLiveData<Result<String>>()
    val uploadStoriesResponse: LiveData<Result<String>> get() = _uploadStoryResponse

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    val pagedStories: Flow<PagingData<ListStoryItem>> = storiesRepository.getPagedStories()

    fun logout() = viewModelScope.launch {
        try {
            storiesRepository.logout()
        } catch (e: Exception) {
            _error.postValue("Logout error: ${e.message}")
        }
    }

    fun uploadStories(photo: MultipartBody.Part, description: RequestBody) {
        viewModelScope.launch {
            try {
                _isLoading.postValue(true)
                val response = storiesRepository.uploadStories(description, photo)
                _uploadSuccess.postValue(response)
            } catch (e: Exception) {
                _error.postValue("Failed to upload story: ${e.message}")
            } finally {
                _isLoading.postValue(false)
            }
        }
    }
}