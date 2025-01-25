package com.example.scentsation.data.fragrance

import android.net.Uri
import com.example.scentsation.data.post.PostFirebaseModel.Companion.POSTS_COLLECTION_PATH
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.firestoreSettings
import com.google.firebase.storage.storage

class FragranceFirebaseModel {

    private val db = Firebase.firestore
    private val storage = Firebase.storage

    companion object {
        const val FRAGRANCES_COLLECTION_PATH = "fragrance"
    }

    init {
        val settings = firestoreSettings {
            isPersistenceEnabled = true
        }
        db.firestoreSettings = settings
    }

    fun getFragrances(callback: (List<Fragrance>) -> Unit) {
        db.collection(FRAGRANCES_COLLECTION_PATH)
            .get().addOnCompleteListener{
                when (it.isSuccessful) {
                    true -> {
                        val fragrances: MutableList<Fragrance> = mutableListOf()
                        for(json in it.result) {
                            val fragrance = Fragrance.fromJSON(json.data)
                            fragrances.add(fragrance)
                        }
                        callback(fragrances)
                    }
                    false -> callback(listOf())
                }
            }
    }

    fun getImage(imageId: String, callback: (Uri) -> Unit) {
        storage.reference.child("images/$FRAGRANCES_COLLECTION_PATH/$imageId")
            .downloadUrl
            .addOnSuccessListener { uri ->
                callback(uri)
            }
    }
}