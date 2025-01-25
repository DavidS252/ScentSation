package com.example.scentsation.data


import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.scentsation.ScentsationApp
import com.example.scentsation.data.post.Converters
import com.example.scentsation.data.post.Post
import com.example.scentsation.data.post.PostDAO
import com.example.scentsation.data.user.User
import com.example.scentsation.data.user.UserDAO
import com.example.scentsation.data.brand.Brand
import com.example.scentsation.data.brand.BrandDAO
import com.example.scentsation.data.fragrance.Fragrance
import com.example.scentsation.data.fragrance.FragranceDAO

@Database(entities = [User::class, Post::class, Brand::class, Fragrance::class], version = 10, exportSchema = true)
@TypeConverters(Converters::class)
abstract class AppLocalDbRepository : RoomDatabase() {
    abstract fun userDao(): UserDAO
    abstract fun postDao(): PostDAO
    abstract fun brandDao(): BrandDAO
    abstract fun fragranceDao(): FragranceDAO
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