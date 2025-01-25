package com.example.scentsation.data.brand

import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.firestoreSettings

class BrandFirebaseModel {

    private val db = Firebase.firestore

    companion object {
        const val POSTS_COLLECTION_PATH = "brands"
    }

    init {
        val settings = firestoreSettings {
            isPersistenceEnabled = true
        }
        db.firestoreSettings = settings
    }

    fun getBrands(since: Long, callback: (List<Brand>) -> Unit) {
        db.collection(POSTS_COLLECTION_PATH)
            .get().addOnCompleteListener {
                when (it.isSuccessful) {
                    true -> {
                        val brands: MutableList<Brand> = mutableListOf()
                        for (json in it.result) {
                            val brand = Brand.fromJSON(json.data)
                            brands.add(brand)
                        }
                        callback(brands)
                    }

                    false -> callback(listOf())
                }
            }
    }
}