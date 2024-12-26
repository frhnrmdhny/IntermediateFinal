package com.dicoding.picodiploma.loginwithanimation.data.pref

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "session")

class UserPreference private constructor(private val dataStore: DataStore<Preferences>) {

    fun getSession(): Flow<UserModel> {
        return dataStore.data.map { preferences ->
            UserModel(
                preferences[emailKey] ?: "",
                preferences[tokenKey] ?: "",
                preferences[passwordKey] ?: "",
                preferences[isLoginKey] ?: false
            )
        }
    }

    suspend fun saveSession(user: UserModel) {
        dataStore.edit { preferences ->
            preferences[emailKey] = user.email
            preferences[tokenKey] = user.token
            preferences[passwordKey] = user.password
            preferences[isLoginKey] = true
        }
    }

    suspend fun getToken(): String? {
        return try {
            val preferences = dataStore.data.first()
            preferences[tokenKey]
        } catch (e: Exception) {
            null
        }
    }

    suspend fun saveToken(token: String) {
        dataStore.edit { preferences ->
            preferences[tokenKey] = token
        }
    }

    suspend fun clearToken() {
        logout()
    }

    suspend fun getPassword(): String? {
        return try {
            val preferences = dataStore.data.first()
            preferences[passwordKey]
        } catch (e: Exception) {
            null
        }
    }


    suspend fun logout() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: UserPreference? = null
        private val emailKey = stringPreferencesKey("email")
        private val tokenKey = stringPreferencesKey("token")
        private val passwordKey = stringPreferencesKey("password")
        private val isLoginKey = booleanPreferencesKey("isLogin")
        fun getInstance(dataStore: DataStore<Preferences>): UserPreference {
            return INSTANCE ?: synchronized(this) {
                val instance = UserPreference(dataStore)
                INSTANCE = instance
                instance
            }
        }
    }
}
