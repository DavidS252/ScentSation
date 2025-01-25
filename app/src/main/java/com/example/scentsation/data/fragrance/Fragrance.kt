package com.example.scentsation.data.fragrance

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity
data class Fragrance(
    @PrimaryKey val id: String = "",
    val fragranceName: String = "",
    val brandId: String = "",
    val photoUrl: String? = null
) : Serializable {

    companion object{
        const val ID_KEY = "id"
        const val FRAGRANCE_NAME_KEY = "fragranceName"
        const val BRAND_ID_KEY = "brandId"

        fun fromJSON(json: Map<String, Any>): Fragrance {
            val id = json[ID_KEY] as? String ?: ""
            val fragranceName = json[FRAGRANCE_NAME_KEY] as? String ?: ""
            val brandId = json[BRAND_ID_KEY] as? String ?: ""

            val fragrance = Fragrance(id, fragranceName, brandId, null)
            return fragrance
        }
    }

    val json: Map<String, Any>
        get() {
            return hashMapOf(
                ID_KEY to id,
                FRAGRANCE_NAME_KEY to fragranceName,
                BRAND_ID_KEY to brandId)
        }
}