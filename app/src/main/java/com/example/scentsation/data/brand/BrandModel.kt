package com.example.scentsation.data.brand

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.scentsation.data.AppLocalDatabase

class BrandModel private constructor() {

    private val database = AppLocalDatabase.db
    private val brands: LiveData<MutableList<Brand>>? = null

    companion object {
        val instance: BrandModel = BrandModel()
    }

    fun getBrands() : LiveData<MutableList<Brand>> {
        return brands ?: database.brandDao().getBrands()
    }
}