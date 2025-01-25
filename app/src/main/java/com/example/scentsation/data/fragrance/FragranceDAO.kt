package com.example.scentsation.data.fragrance

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query

@Dao
interface FragranceDAO {
    @Query("SELECT * FROM fragrance")
    fun getFragrances(): LiveData<MutableList<Fragrance>>
}