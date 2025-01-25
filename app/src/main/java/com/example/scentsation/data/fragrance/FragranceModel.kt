package com.example.scentsation.data.fragrance

import android.net.Uri
import androidx.lifecycle.LiveData
import com.example.scentsation.data.AppLocalDatabase
import java.util.concurrent.Executors

class FragranceModel private constructor() {

    enum class LoadingState {
        LOADING, LOADED
    }

    private val database = AppLocalDatabase.db
    private val firebaseModel = FragranceFirebaseModel()
    private val fragrances: LiveData<MutableList<Fragrance>>? = null

    companion object {
        val instance: FragranceModel = FragranceModel()
    }

    fun getFragrances(): LiveData<MutableList<Fragrance>> {
        return fragrances ?: database.fragranceDao().getFragrances()
    }

    fun getPostImage(imageId: String, callback: (Uri) -> Unit) {
        firebaseModel.getImage(imageId, callback);
    }
}