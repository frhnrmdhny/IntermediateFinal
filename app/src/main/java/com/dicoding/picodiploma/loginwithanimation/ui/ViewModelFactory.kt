package com.dicoding.picodiploma.loginwithanimation.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dicoding.picodiploma.loginwithanimation.di.Injection
import com.dicoding.picodiploma.loginwithanimation.ui.main.MainViewModel
import com.dicoding.picodiploma.loginwithanimation.ui.story.StoriesDetailViewModel
import com.dicoding.picodiploma.loginwithanimation.ui.home.HomeViewModel
import com.dicoding.picodiploma.loginwithanimation.ui.login.LoginViewModel
import com.dicoding.picodiploma.loginwithanimation.ui.signup.SignupViewModel

class ViewModelFactory(
    private val context: Context,
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                MainViewModel(Injection.provideRepository(context)) as T
            }

            modelClass.isAssignableFrom(SignupViewModel::class.java) -> {
                SignupViewModel(Injection.provideRepository(context)) as T
            }

            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(Injection.provideRepository(context)) as T
            }
            
            modelClass.isAssignableFrom(HomeViewModel::class.java) -> {
                HomeViewModel(Injection.provideStoryRepository(context)) as T
            }

            modelClass.isAssignableFrom(StoriesDetailViewModel::class.java) -> {
                StoriesDetailViewModel(Injection.provideStoryRepository(context)) as T
            }

            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }
}