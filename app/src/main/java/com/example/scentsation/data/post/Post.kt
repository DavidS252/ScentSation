package com.example.scentsation.data.post

import android.content.Context
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import com.example.scentsation.ScentsationApp
import com.google.common.reflect.TypeToken
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import com.google.gson.Gson

class Converters {

    @TypeConverter
    fun fromList(value: List<String>?): String {
        return Gson().toJson(value)
    }

    @TypeConverter
    fun toList(value: String): List<String>? {
        val listType = object : TypeToken<List<String>>() {}.type
        return Gson().fromJson(value, listType)
    }
}

@Entity
data class Post(
    @PrimaryKey val id: String,
    val fragranceName: String,
    val brandName: String,
    val fragranceRating: String,
    val userId: String,
    val description: String,
    var isDeleted: Boolean = false,
    var photo: String? = null,
    var aromas: List<String> = emptyList(),
    var timestamp: Long? = null,
)  {

    companion object {
        var lastUpdated: Long
            get() {
                return ScentsationApp.Globals.appContext?.getSharedPreferences(
                    "TAG",
                    Context.MODE_PRIVATE
                )?.getLong(POST_LAST_UPDATED, 0) ?: 0
            }
            set(value) {
                ScentsationApp.Globals?.appContext?.getSharedPreferences(
                    "TAG",
                    Context.MODE_PRIVATE
                )?.edit()?.putLong(POST_LAST_UPDATED, value)?.apply()
            }

        const val ID_KEY = "id"
        const val FRAGRANCE_NAME_KEY = "fragranceName"
        const val BRAND_NAME_KEY = "brandName"
        const val FRAGRANCE_RATING_KEY = "fragranceRating"
        const val USER_ID_KEY = "userId"
        const val LAST_UPDATED_KEY = "timestamp"
        const val DESCRIPTION_KEY = "description"
        const val AROMAS_KEY = "aromas"
        const val IS_DELETED_KEY = "is_deleted"
        private const val POST_LAST_UPDATED = "post_last_updated"

        fun fromJSON(json: Map<String, Any>): Post {
            val id = json[ID_KEY] as? String ?: ""
            val fragranceName = json[FRAGRANCE_NAME_KEY] as? String ?: ""
            val brandName = json[BRAND_NAME_KEY] as? String ?: ""
            val fragranceRating = json[FRAGRANCE_RATING_KEY] as? String ?: ""
            val description = json[DESCRIPTION_KEY] as? String ?: ""
            val aromas = json[AROMAS_KEY] as? List<String> ?: emptyList()
            val isDeleted = json[IS_DELETED_KEY] as? Boolean ?: false
            val userId = json[USER_ID_KEY] as? String ?: ""

            val post = Post(id, fragranceName, brandName, fragranceRating, userId, description, isDeleted,
                null, aromas)

            val timestamp: Timestamp? = json[LAST_UPDATED_KEY] as? Timestamp
            timestamp?.let {
                post.timestamp = it.seconds
            }

            return post
        }
    }

    val json: Map<String, Any>
        get() {
            return hashMapOf(
                ID_KEY to id,
                FRAGRANCE_NAME_KEY to fragranceName,
                BRAND_NAME_KEY to brandName,
                FRAGRANCE_RATING_KEY to fragranceRating,
                LAST_UPDATED_KEY to FieldValue.serverTimestamp(),
                DESCRIPTION_KEY to description,
                AROMAS_KEY to aromas,
                IS_DELETED_KEY to isDeleted,
                USER_ID_KEY to userId
            )
        }

    val deleteJson: Map<String, Any>
        get() {
            return hashMapOf(
                IS_DELETED_KEY to true,
                LAST_UPDATED_KEY to FieldValue.serverTimestamp(),
            )
        }

    val updateJson: Map<String, Any>
        get() {
            return hashMapOf(
                DESCRIPTION_KEY to description,
                LAST_UPDATED_KEY to FieldValue.serverTimestamp(),
            )
        }
}