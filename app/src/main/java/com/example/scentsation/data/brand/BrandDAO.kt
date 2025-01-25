package com.example.scentsation.data.brand

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query

@Dao
interface BrandDAO {
    @Query("SELECT * FROM brand")
    fun getBrands(): LiveData<MutableList<Brand>>
}