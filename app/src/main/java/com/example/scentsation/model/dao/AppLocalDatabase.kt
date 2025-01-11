package com.example.scentsation.model.dao

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [User::class, Post::class], version = 7, exportSchema = true)
@TypeConverters(LatLngCoxnverter::class)
abstract class AppLocalDbRepository : RoomDatabase() {
    abstract fun userDao(): UserDAO
    abstract fun postDao(): PostDAO
}

object AppLocalDatabase {
    val db: AppLocalDbRepository by lazy {
        val context = FoodieFindsApp.Globals.appContext
            ?: throw IllegalStateException("Application context not available")

        Room.databaseBuilder(
            context, AppLocalDbRepository::class.java, "foodie-finds"
        ).fallbackToDestructiveMigration().build()
    }
}