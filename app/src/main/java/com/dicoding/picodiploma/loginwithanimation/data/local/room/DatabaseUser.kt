package com.dicoding.picodiploma.loginwithanimation.data.local.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.dicoding.picodiploma.loginwithanimation.data.local.entity.UserEntity

@Database(entities = [UserEntity::class], version = 3, exportSchema = false)
abstract class DatabaseUser : RoomDatabase() {
    abstract fun userDao(): UserDao

    companion object {
        @Volatile
        private var instance: DatabaseUser? = null

        fun getInstance(context: Context): DatabaseUser =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    DatabaseUser::class.java, "user_database"
                ).build().also { instance = it }
            }
    }
}