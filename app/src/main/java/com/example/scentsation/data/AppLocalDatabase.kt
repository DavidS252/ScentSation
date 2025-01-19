package com.example.scentsation.data


import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.scentsation.ScentsationApp
import com.example.scentsation.data.post.Post
import com.example.scentsation.data.post.PostDAO
import com.example.scentsation.data.user.User
import com.example.scentsation.data.user.UserDAO

@Database(entities = [User::class, Post::class], version = 7, exportSchema = true)
abstract class AppLocalDbRepository : RoomDatabase() {
    abstract fun userDao(): UserDAO
    abstract fun postDao(): PostDAO
}

object AppLocalDatabase {
    val db: AppLocalDbRepository by lazy {
        val context = ScentsationApp.Globals.appContext
            ?: throw IllegalStateException("Application context not available")

        Room.databaseBuilder(
                context, AppLocalDbRepository::class.java, "scentsation.db"
            ).fallbackToDestructiveMigration(false).build()
    }
}