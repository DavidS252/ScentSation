package com.example.scentsation.data.user
import android.content.Context
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.scentsation.ScentsationApp
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue

@Entity
class User(
    @PrimaryKey val id: String,
    val userName: String,
    var profileImage: String? = null,
    var lastUpdated: Long? = null,
) {
    companion object {
        var lastUpdated: Long
            get() {
                return ScentsationApp.Globals.appContext?.getSharedPreferences(
                    "TAG",
                    Context.MODE_PRIVATE
                )?.getLong(USER_LAST_UPDATED, 0) ?: 0
            }
            set(value) {
                ScentsationApp.Globals?.appContext?.getSharedPreferences(
                    "TAG",
                    Context.MODE_PRIVATE
                )?.edit()?.putLong(USER_LAST_UPDATED, value)?.apply()
            }

        const val ID_KEY = "id"
        const val USER_NAME_KEY = "userName"
        const val LAST_UPDATED_KEY = "lastUpdated"
        const val USER_LAST_UPDATED = "user_last_updated"

        fun fromJSON(json: Map<String, Any>): User {
            val id = json[ID_KEY] as? String ?: ""
            val userName = json[USER_NAME_KEY] as? String ?: ""
            val user = User(id, userName)

            val lastUpdated: Timestamp? = json[LAST_UPDATED_KEY] as? Timestamp
            lastUpdated?.let {
                user.lastUpdated = it.seconds
            }
            return user
        }
    }

    val json: Map<String, Any>
        get() {
            return hashMapOf(
                ID_KEY to id,
                USER_NAME_KEY to userName,
                LAST_UPDATED_KEY to FieldValue.serverTimestamp(),
            )
        }

    val updateJson: Map<String, Any>
        get() {
            return hashMapOf(
                USER_NAME_KEY to userName,
                LAST_UPDATED_KEY to FieldValue.serverTimestamp(),
            )
        }

}